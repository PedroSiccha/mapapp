package com.inforad.mapapp.model

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("id")
    val id: Int,

    @SerializedName("localname")
    val localname: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("description")
    val descripttion: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("created_at")
    val createdAt: String
)
