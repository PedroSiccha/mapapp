package com.inforad.mapapp.model

data class OrderRequest(
    val user_id: String,
    val client_id: String,
    val total_amount: Double,
    val order_details: List<DetallePedido>
)
