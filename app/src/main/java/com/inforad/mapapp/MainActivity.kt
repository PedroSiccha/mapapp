package com.inforad.mapapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.inforad.mapapp.databinding.ActivityMainBinding
import com.inforad.mapapp.model.LoginRequest
import com.inforad.mapapp.model.LoginResponse
import com.inforad.mapapp.service.ApiService
import com.inforad.mapapp.view.maps.MapsActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response as OkHttpResponse
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnNext.setOnClickListener {
            validateLogin(binding.etEmail.text.toString().trim(), binding.etPassword.text.toString().trim())
        }
    }

    private fun validateLogin(email: String, password: String) {

        val interceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): OkHttpResponse {
                val request = chain.request()
                // Aquí puedes imprimir información sobre la solicitud, como la URL
                println("Solicitando a la URL: ${request.method()} ${request.url()}")
                println("Body: ${request.body().to(email).to(password)}")
                // Continúa con la cadena de interceptores
                return chain.proceed(request)
            }
        }

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor)

//        val loggingInterceptor = HttpLoggingInterceptor()
//        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        httpClient.addInterceptor(loggingInterceptor)

        val client = httpClient.build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://clincia.000webhostapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.login(LoginRequest(email, password))

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    Toast.makeText(applicationContext, "Bienvenido", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@MainActivity, MapsActivity::class.java)
                    intent.putExtra("token", token)
                    startActivity(intent)
                    // Aquí puedes guardar el token en SharedPreferences u otro lugar para usarlo posteriormente
                } else {
                    //textViewLocations.text = "Error al iniciar sesión."
                    Toast.makeText(applicationContext, "Error al iniciar sesión.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                //textViewLocations.text = "Error de red: " + t.message
                Log.e("Error de red: ", t.message.toString())
                Toast.makeText(applicationContext, "Error de red: " + t.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}