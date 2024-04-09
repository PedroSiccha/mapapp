package com.inforad.mapapp.model

data class CreateOrderResponse(
    val success: Boolean,
    val message: String?,
    val order_id: Int?
)
