package com.inforad.mapapp.view.orders.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inforad.mapapp.R
import com.inforad.mapapp.model.Order

class OrderAdapter(private val orders: List<Order>): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_order, parent, false)
        return OrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = orders[position]
        holder.bind(currentOrder)
    }

    override fun getItemCount() = orders.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
//        private val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
//        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
//        private val tvTotalAmount: TextView = itemView.findViewById(R.id.tvTotalAmount)
//        private val tvClientName: TextView = itemView.findViewById(R.id.tvClientName)

        fun bind(order: Order) {
//            tvOrderId.text = "Numero Orden: ${order.orderId}"
//            tvOrderDate.text = "Ordenado ${order.orderDate}"
//            tvStatus.text = order.status
//            tvTotalAmount.text = "Total: ${order.totalAmount}"
//            tvClientName.text = order.clientName
        }
    }
}