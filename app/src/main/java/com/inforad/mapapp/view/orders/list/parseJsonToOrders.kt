package com.inforad.mapapp.view.orders.list

import com.inforad.mapapp.model.Order
import org.json.JSONArray

fun parseJsonToOrders(json: String): List<Order> {
    val orders = mutableListOf<Order>()
    val jsonArray = JSONArray(json)

    for (i in 0 until jsonArray.length()) {
        val orderObject = jsonArray.getJSONObject(i)
        val orderId = orderObject.getString("order_id")
        val orderDate = orderObject.getString("order_date")
        val clientName = orderObject.getString("client_name")
        val clientPhone = orderObject.getString("client_phone")
        val status = orderObject.getString("status")

        // Aquí podrías también analizar los objetos de artículos del pedido si es necesario

        val order = Order(orderId.toInt(), orderDate, clientName, clientPhone, status)
        orders.add(order)
    }

    return orders
}
