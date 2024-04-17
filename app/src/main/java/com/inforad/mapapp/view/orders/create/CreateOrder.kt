package com.inforad.mapapp.view.orders.create

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.inforad.mapapp.R
import com.inforad.mapapp.databinding.ActivityCreateOrderBinding
import com.inforad.mapapp.model.Cliente
import com.inforad.mapapp.model.CreateOrderResponse
import com.inforad.mapapp.model.DetallePedido
import com.inforad.mapapp.model.OrderRequest
import com.inforad.mapapp.model.Producto
import com.inforad.mapapp.service.ApiService
import com.inforad.mapapp.service.OrderApi
import com.inforad.mapapp.view.maps.MapsActivity
import com.inforad.mapapp.view.orders.adapter.DetallesPedidoAdapter
import com.inforad.mapapp.view.orders.adapter.ProductAdapter
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class CreateOrder : AppCompatActivity(), ProductAdapter.OnProductClickListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityCreateOrderBinding
    private lateinit var progressBar: AlertDialog
    private lateinit var etSearchProduct: EditText
    private lateinit var rvProducts: RecyclerView
    private lateinit var rvDetallePedido: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var detallesPedido: MutableList<DetallePedido>

    private lateinit var adapter: DetallesPedidoAdapter
    var token = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        progressBar = createProgressDialog()
        detallesPedido = mutableListOf()
        viewProgress(false)
        searchClient()
        searchProduct()
        token = intent.getStringExtra("token").toString()
        productAdapter.clearProducts()
        binding.buttonCreate.setOnClickListener {
            viewProgress(true)
            createOrder()
        }
    }

    private fun searchProduct() {
        etSearchProduct = binding.etSearchProduct
        rvProducts = binding.rvListProducts
        rvProducts.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(this)
        rvProducts.adapter = productAdapter

        rvDetallePedido = binding.rvProducts
        adapter = DetallesPedidoAdapter(detallesPedido)
        rvDetallePedido.adapter = adapter
        rvDetallePedido.layoutManager = LinearLayoutManager(this)

        val interceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val request = chain.request()
                // Aquí puedes imprimir información sobre la solicitud, como la URL
                println("Solicitando a la URL: ${request.method()} ${request.url()}")
                println("Body: ${request.body().let { it.toString() }}")
                // Continúa con la cadena de interceptores
                return chain.proceed(request)
            }
        }

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor)

        val client = httpClient.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(OrderApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        etSearchProduct.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val productName = s.toString()
                if (productName.isNullOrEmpty()) {
                    productAdapter.clearProducts()
                } else {
                    buscarProducto(apiService, productName)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onProductAddedToCart(detallesPedido: DetallePedido) {
        this.detallesPedido.add(detallesPedido)
        adapter.notifyDataSetChanged()
        val total = adapter.calcularTotal()
        binding.tvImport.text = total.toString()

    }

    private fun buscarProducto(apiService: ApiService, productName: String) {
        val call = apiService.buscarProducto(productName)
        call.enqueue(object : Callback<List<Producto>> {
            override fun onResponse(call: Call<List<Producto>>, response: Response<List<Producto>>) {
                if (response.isSuccessful) {
                    val productos = response.body()
                    if (!productos.isNullOrEmpty()) {
                        // Limpiar la lista de productos antes de agregar los nuevos resultados
                        productAdapter.clearProducts()
                        // Actualizar el adaptador del RecyclerView con los nuevos resultados de la búsqueda
                        productAdapter.setProducts(this@CreateOrder, productos)
                    } else {
                        // Si no se encontraron productos, limpiar el adaptador
                        productAdapter.clearProducts()
                    }
                } else {
                    // Manejar error de respuesta
                    productAdapter.clearProducts()
                }
            }

            override fun onFailure(call: Call<List<Producto>>, t: Throwable) {
                // Manejar error de red
                productAdapter.clearProducts()
            }
        })
    }

    private fun searchClient() {

        val interceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val request = chain.request()
                // Aquí puedes imprimir información sobre la solicitud, como la URL
                println("Solicitando a la URL: ${request.method()} ${request.url()}")
                println("Body: ${request.body().let { it.toString() }}")
                // Continúa con la cadena de interceptores
                return chain.proceed(request)
            }
        }

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor)

        val client = httpClient.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(OrderApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        binding.ivFilter.setOnClickListener(View.OnClickListener {
            viewProgress(true)
            val clienteDocumento: String = binding.etSearchClient.getText().toString().trim()

            // Realiza la solicitud HTTP
            val call: Call<Cliente> = apiService.buscarCliente(clienteDocumento)
            call.enqueue(object : Callback<Cliente?> {
                override fun onResponse(call: Call<Cliente?>?, response: Response<Cliente?>) {
                    viewProgress(false)
                    if (response.isSuccessful()) {
                        // Si la solicitud fue exitosa, obtén el nombre del cliente
                        val cliente: Cliente? = response.body()
                        if (cliente != null) {
                            val nombreCliente: String = cliente.nombre
                            // Muestra el nombre del cliente en el EditText etNameClient
                            binding.etNameClient.setText(nombreCliente)
                        }
                    } else {
                        // Si la solicitud no fue exitosa, muestra un mensaje de error
                        Toast.makeText(
                            applicationContext,
                            "Error al obtener el nombre del cliente",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Cliente?>?, t: Throwable) {
                    viewProgress(false)
                    // Maneja errores de la solicitud
                    t.printStackTrace()
                    Toast.makeText(
                        applicationContext,
                        "Error de red: " + t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        })
    }

    private fun viewProgress(status: Boolean) {
        if (status) {
            progressBar.show()
        } else {
            progressBar.dismiss()
        }
    }

    private fun createProgressDialog(): AlertDialog {
        val dialogViewOrders = LayoutInflater.from(this).inflate(R.layout.custom_progress_dialog, null)
        return AlertDialog.Builder(this)
            .setView(dialogViewOrders)
            .create()
    }

    private fun createOrder() {
        val user_id = "1" // Obtén el ID del usuario de alguna manera
        val client_id = "1"
        val total_amount = binding.tvImport.text.toString().toDouble() // Obtén el importe total del TextView

        // Crear una lista de detalles de pedido
        val orderDetails = mutableListOf<DetallePedido>()
        for (detalle in detallesPedido) {
            orderDetails.add(DetallePedido(detalle.product_id, detalle.quantity, detalle.price.toString()))
        }

        // Crear un objeto OrderRequest con los datos necesarios
        val orderRequest = OrderRequest(user_id, client_id, total_amount, orderDetails)

        val interceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val request = chain.request()
                // Aquí puedes imprimir información sobre la solicitud, como la URL
                println("Solicitando a la URL: ${request.method()} ${request.url()}")
                println("Body: ${request.body().let { it.toString() }}")
                // Continúa con la cadena de interceptores
                return chain.proceed(request)
            }
        }

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor)

        val client = httpClient.build()

        // Crear una instancia de Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(OrderApi.BASE_URL) // Reemplaza "https://tu-backend.com/api/" con la URL real de tu backend
            .addConverterFactory(GsonConverterFactory.create()) // Usa GsonConverterFactory para convertir JSON a objetos Kotlin
            .client(client)
            .build()

        // Crear una instancia del servicio ApiService
        val apiService = retrofit.create(ApiService::class.java)

        // Realizar la solicitud al backend
        apiService.createOrder(orderRequest).enqueue(object : Callback<CreateOrderResponse> {
            override fun onResponse(call: Call<CreateOrderResponse>, response: Response<CreateOrderResponse>) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData != null && responseData.success) {
                        val orderId = responseData.order_id
                        viewProgress(false)
                        Toast.makeText(applicationContext, "Orden generada con éxito. ID de orden: $orderId", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        viewProgress(false)
                        Toast.makeText(applicationContext, "Error al crear la orden: ${responseData?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    viewProgress(false)
                    Toast.makeText(applicationContext, "Error al crear la orden", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CreateOrderResponse>, t: Throwable) {
                viewProgress(false)
                Toast.makeText(applicationContext, "Error de red: " + t.message, Toast.LENGTH_SHORT).show()
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
                // Lógica para manejar el clic en "Mis Pedidos"
                // Por ejemplo, navegar a la actividad de mis pedidos
                return true
            }
            R.id.map -> {
                item.isChecked = true
                finish()
                return true
            }
            R.id.createsale -> {
                return true
            }
            R.id.profile -> {
                // Lógica para manejar el clic en "Perfil"
                // Por ejemplo, abrir la actividad de perfil del usuario
                return true
            }
        }
        return false
    }




}