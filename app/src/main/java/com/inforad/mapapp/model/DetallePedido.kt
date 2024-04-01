package com.inforad.mapapp.model

import com.google.gson.annotations.SerializedName

data class DetallePedido(

    @SerializedName("order_id")
    val order_id: Int? = 0,

    @SerializedName("product_id")
    val product_id: Int? = 0,

    @SerializedName("product_name")
    val product_name: String? = "",

    @SerializedName("quantity")
    val quantity: Int? = 0,

    @SerializedName("price")
    val price: Double? = 0.0,

    @SerializedName("und")
    val und: String? = ""
)
