package com.inforad.mapapp.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OrderApi {
    val BASE_URL = "http://192.168.145.130/map_backend/public_html/api/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: ApiService = retrofit.create(ApiService::class.java)
}