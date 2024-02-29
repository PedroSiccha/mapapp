package com.inforad.mapapp.infrastructure

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth")
    suspend fun authenticate(
        @Body authRequest: AuthRequest
    ): Response<AuthResponse>
}

val retrofit = Retrofit.Builder()
    .baseUrl("http://127.0.0.1:3000/api/") // URL base del servicio web
    .addConverterFactory(GsonConverterFactory.create()) // Convertidor de JSON
    .build()

val authService = retrofit.create(AuthService::class.java)