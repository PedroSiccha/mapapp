package com.inforad.mapapp.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OrderApi {
    private const val BASE_URL = "https://clincia.000webhostapp.com/api/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: ApiService = retrofit.create(ApiService::class.java)
}