package com.inforad.mapapp.model

data class Order(
    val orderId: Int,
    val orderDate: String,
    val status: String,
    val totalAmount: String,
    val clientName: String
)
