package com.inforad.mapapp.model

import com.google.gson.annotations.SerializedName

data class Cliente(
    @SerializedName("name")
    val nombre: String
)
