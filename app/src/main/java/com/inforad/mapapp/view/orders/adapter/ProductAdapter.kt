package com.inforad.mapapp.view.orders.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.inforad.mapapp.R
import com.inforad.mapapp.model.DetallePedido
import com.inforad.mapapp.model.Producto


class ProductAdapter(private val listener: OnProductClickListener) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    var mContext: Context? = null
    private var products: List<Producto> = ArrayList()
    var txtUnidad = ""
    private var detallesPedido: MutableList<DetallePedido> = ArrayList()


    fun setDetallesPedido(detallesPedido: List<DetallePedido>) {
        this.detallesPedido.clear()
        this.detallesPedido.addAll(detallesPedido)
        notifyDataSetChanged()
    }

    interface OnProductClickListener {
        fun onProductAddedToCart(detallesPedido: DetallePedido)
    }

    fun setProducts(context: Context, products: List<Producto>) {
        this.products = products
        notifyDataSetChanged()
        mContext = context;
    }

    fun clearProducts() {
        this.products = emptyList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_products, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProduct)
        private val tvProductStock: TextView = itemView.findViewById(R.id.tvStock)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tvUnid)
        private val ivAdd: ImageView = itemView.findViewById(R.id.ivAdd)
        private var cantidad: Int = 0
        fun bind(product: Producto) {
            tvProductName.text = product.name
            tvProductStock.text = product.stock
            tvProductPrice.text = "S/. ${product.price}"
            ivAdd.setOnClickListener {
                dialogUnid(product)
            }

        }

        private fun dialogUnid(product: Producto) {
            val dialogViewOrders = LayoutInflater.from(mContext).inflate(R.layout.dialog_cantidad_product, null)
            val dialog = AlertDialog.Builder(mContext)
                .setView(dialogViewOrders)
                .create()

            dialog.show()
            val spinnerPresentacion = dialogViewOrders.findViewById<Spinner>(R.id.buttonSearchPresentacion)
            createSelect(spinnerPresentacion, mContext!!)

            val tvCantidades = dialogViewOrders.findViewById<TextView>(R.id.tvCantidades)
            val tvData = dialogViewOrders.findViewById<TextView>(R.id.et_data)
            val btnSumar = dialogViewOrders.findViewById<ImageView>(R.id.btnSumar)
            val btnRestar = dialogViewOrders.findViewById<ImageView>(R.id.btnRestar)
            val txtNombre = dialogViewOrders.findViewById<AppCompatTextView>(R.id.tvProduct)
            val txtPrecio = dialogViewOrders.findViewById<AppCompatEditText>(R.id.et_payment_amount)
            val buttonConfirm = dialogViewOrders.findViewById<Button>(R.id.buttonConfirm)

            txtNombre.setText(product.name)

            btnSumar.setOnClickListener { sumarCantidad(tvCantidades, txtPrecio, product, tvData) }
            btnRestar.setOnClickListener { restarCantidad(tvCantidades, txtPrecio, product, tvData) }

            buttonConfirm.setOnClickListener {

                // Crear un objeto DetallePedido con los datos del producto y agregarlo a la lista
                val detallePedido = DetallePedido(
                    product_id = product.id,
                    product_name = product.name,
                    und = txtUnidad,
                    quantity = cantidad,
                    price = txtPrecio.text.toString().toDouble()
                )
                listener.onProductAddedToCart(detallePedido)

                // Cerrar el diálogo después de agregar el producto
                dialog.dismiss()
            }

        }

        private fun sumarCantidad(tvCantidades: TextView, txtPrecio: AppCompatEditText, product: Producto, tvData: TextView) {
            // Incrementar la cantidad y actualizar el TextView
            var und = 1
            if (txtUnidad == "DOC") {
                und = 12
            } else {
                und = 1
            }
            cantidad++
            val price = product.price.toDoubleOrNull() ?: 0.0
            val total = cantidad * price * und
            val editableTotal = Editable.Factory.getInstance().newEditable(total.toString())
            tvCantidades.text = cantidad.toString()
            txtPrecio.text = editableTotal
            tvData.text = "S/. ${ editableTotal }       ${ txtUnidad }      ${ cantidad.toString() }"

        }

        private fun restarCantidad(tvCantidades: TextView, txtPrecio: AppCompatEditText, product: Producto, tvData: TextView) {
            // Verificar que la cantidad no sea menor que cero antes de restar
            var und = 1
            if (txtUnidad == "DOC") {
                und = 12
            } else {
                und = 1
            }
            val price = product.price.toDoubleOrNull() ?: 0.0
            if (cantidad > 0) {
                cantidad--
                val total = cantidad * price * und
                val editableTotal = Editable.Factory.getInstance().newEditable(total.toString())
                tvCantidades.text = cantidad.toString()
                txtPrecio.text = editableTotal
                tvData.text = "S/. ${ editableTotal }       ${ txtUnidad }      ${ cantidad.toString() }"
            }
        }

        private fun createSelect(spinner: Spinner?, context: Context) {
            val options = arrayOf("¿PRESENTACIÓN?", "UND", "DOC")
            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner?.adapter = adapter

            // Manejar la selección del usuario
            spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedOption = parent?.getItemAtPosition(position).toString()
                    txtUnidad = selectedOption
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Hacer algo si no se selecciona nada
                }
            }
        }
    }
}
