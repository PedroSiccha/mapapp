package com.inforad.mapapp.model

import com.google.gson.annotations.SerializedName

data class Producto(

    @SerializedName("id")
    val id: Int? = 0,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("price")
    val price: String,

    @SerializedName("stock")
    val stock: String
)
