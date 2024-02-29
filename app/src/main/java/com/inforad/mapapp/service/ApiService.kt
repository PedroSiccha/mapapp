package com.inforad.mapapp.service

import com.inforad.mapapp.model.Location
import com.inforad.mapapp.model.LoginRequest
import com.inforad.mapapp.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("login.php")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("locations.php")
    fun getLocations(@Header("Authorization") authorization: String): Call<List<Location>>
}