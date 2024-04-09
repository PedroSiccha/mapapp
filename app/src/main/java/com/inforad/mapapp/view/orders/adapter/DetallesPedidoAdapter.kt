package com.inforad.mapapp.view.orders.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inforad.mapapp.R
import com.inforad.mapapp.model.DetallePedido

class DetallesPedidoAdapter(private val detallesPedido: List<DetallePedido>) :
    RecyclerView.Adapter<DetallesPedidoAdapter.DetallePedidoViewHolder>() {

    inner class DetallePedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreProductoTextView: TextView = itemView.findViewById(R.id.atvProduct)
        private val presentacionTextView: TextView = itemView.findViewById(R.id.tvPresentacion)
        private val cantidadTextView: TextView = itemView.findViewById(R.id.tvCantidad)
        private val precioTextView: TextView = itemView.findViewById(R.id.tvPrecio)

        fun bind(detallePedido: DetallePedido) {
            nombreProductoTextView.text = detallePedido.product_name
            presentacionTextView.text = detallePedido.und
            cantidadTextView.text = detallePedido.quantity.toString()
            precioTextView.text = detallePedido.price.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetallePedidoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_products, parent, false)
        return DetallePedidoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DetallePedidoViewHolder, position: Int) {
        holder.bind(detallesPedido[position])
    }

    override fun getItemCount(): Int {
        return detallesPedido.size
    }

    fun calcularTotal(): Double {
        var total = 0.0
        for (detalle in detallesPedido) {
            total += detalle.price ?: 0.0
        }
        return total
    }

}