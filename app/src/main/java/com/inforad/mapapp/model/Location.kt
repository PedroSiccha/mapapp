package com.inforad.mapapp.model

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("id") val id: Int,
    @SerializedName("localname") val localname: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("created_at") val createdAt: String
)
