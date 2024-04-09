package com.inforad.mapapp.service

import com.inforad.mapapp.model.Cliente
import com.inforad.mapapp.model.CreateOrderResponse
import com.inforad.mapapp.model.Location
import com.inforad.mapapp.model.LoginRequest
import com.inforad.mapapp.model.LoginResponse
import com.inforad.mapapp.model.Order
import com.inforad.mapapp.model.OrderRequest
import com.inforad.mapapp.model.Producto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("login.php")
    fun login(
        @Body
        loginRequest: LoginRequest
    ): Call<LoginResponse>

    @GET("locations.php")
    fun getLocations(
        @Header("Authorization")
        authorization: String
    ): Call<List<Location>>


    @GET("clients/get-client.php")
    fun buscarCliente(
        @Query("documento")
        documento: String
    ): Call<Cliente>

    @GET("products/search-products.php")
    fun buscarProducto(
        @Query("nombre")
        productName: String
    ): Call<List<Producto>>

    @POST("orders/save-order.php") // Reemplaza "create_order.php" con la ruta real de tu endpoint en el backend
    fun createOrder(
        @Body
        orderRequest: OrderRequest
    ): Call<CreateOrderResponse>

    @GET("orders/get-by-user.php")
    suspend fun getOrders(): List<Order>

}