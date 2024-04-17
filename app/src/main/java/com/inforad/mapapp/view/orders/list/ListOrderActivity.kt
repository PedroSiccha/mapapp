package com.inforad.mapapp.view.orders.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.inforad.mapapp.R
import com.inforad.mapapp.databinding.ActivityListOrderBinding
import com.inforad.mapapp.model.Order
import com.inforad.mapapp.service.OrderApi
import com.inforad.mapapp.view.orders.adapter.OrderAdapter
import com.inforad.mapapp.view.orders.create.CreateOrder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class ListOrderActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityListOrderBinding
    private lateinit var navigationView: NavigationView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var orders: MutableList<Order>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        orders = mutableListOf()
        orderAdapter = OrderAdapter(orders)
        binding.rvListPedidos.adapter = orderAdapter

        // Llamar a la función para obtener los pedidos desde la API
        fetchOrders()

    }

    private fun fetchOrders(userId: Int = 1) {
        val url = "${OrderApi.BASE_URL}orders/get-by-user.php?user_id=$userId" // Reemplaza con la URL de tu backend PHP

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Manejar la falla de la solicitud
                e.printStackTrace()
                // Mostrar un mensaje de error al usuario si falla la solicitud
                Toast.makeText(this@ListOrderActivity, "Error al obtener los pedidos", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                // Manejar la respuesta del servidor
                response.body()?.let { responseBody ->
                    try {
                        val json = responseBody.string()
                        Log.e("Bodi", json.toString())
                        val presentaciones = mutableListOf<String>()
                        val jsonArray = JSONArray(json)
                        Log.e("jsonArray", jsonArray.toString())
                        for (i in 0 until jsonArray.length()) {
                            val presentacion = jsonArray.getJSONObject(i).getString("username") // Reemplaza "name" con el nombre del campo que contiene la presentación en tu JSON
                            presentaciones.add(presentacion)
                        }

                        Log.e("Presentaciones", presentaciones.toString())

                        // Actualizar el Spinner con las presentaciones obtenidas
//                        spinner?.post {
//                            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, presentaciones.toTypedArray())
//                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                            spinner?.adapter = adapter
//                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Mostrar un mensaje de error al usuario si hay un problema al procesar la respuesta JSON
                        Toast.makeText(this@ListOrderActivity, "Error al procesar las presentaciones", Toast.LENGTH_SHORT).show()
                    } finally {
                        responseBody.close() // Cerrar el cuerpo de la respuesta
                    }
                }
            }
        })
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                finish()
                return true
            }
            R.id.mysale -> {
                return true
            }
            R.id.map -> {
                finish()
                return true
            }
            R.id.createsale -> {
                item.isChecked = true
                val intent = Intent(this, CreateOrder::class.java)
                startActivity(intent)
                return true
            }
            R.id.profile -> {
                return true
            }
        }
        return false
    }

}