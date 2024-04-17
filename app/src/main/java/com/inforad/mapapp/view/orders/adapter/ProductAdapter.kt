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
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.inforad.mapapp.R
import com.inforad.mapapp.model.DetallePedido
import com.inforad.mapapp.model.Producto
import com.inforad.mapapp.service.OrderApi
import okhttp3.*
import org.json.JSONArray
import java.io.IOException


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
            createSelect(product.id!!.toInt(), spinnerPresentacion, mContext!!)

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

        private fun createSelect(productoId: Int, spinner: Spinner?, context: Context) {
            val url = "${OrderApi.BASE_URL}presentation/get-by-products.php?product_id=$productoId" // Reemplaza con la URL de tu backend PHP

            val client = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Manejar la falla de la solicitud
                    e.printStackTrace()
                    // Mostrar un mensaje de error al usuario si falla la solicitud
                    Toast.makeText(context, "Error al obtener las presentaciones", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call, response: Response) {
                    // Manejar la respuesta del servidor
                    response.body()?.let { responseBody ->
                        try {
                            val json = responseBody.string()
                            val presentaciones = mutableListOf<String>()
                            val jsonArray = JSONArray(json)
                            for (i in 0 until jsonArray.length()) {
                                val presentacion = jsonArray.getJSONObject(i).getString("name") // Reemplaza "name" con el nombre del campo que contiene la presentación en tu JSON
                                presentaciones.add(presentacion)
                            }

                            // Actualizar el Spinner con las presentaciones obtenidas
                            spinner?.post {
                                val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, presentaciones.toTypedArray())
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinner?.adapter = adapter
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                            // Mostrar un mensaje de error al usuario si hay un problema al procesar la respuesta JSON
                            Toast.makeText(context, "Error al procesar las presentaciones", Toast.LENGTH_SHORT).show()
                        } finally {
                            responseBody.close() // Cerrar el cuerpo de la respuesta
                        }
                    }
                }
            })
        }
    }
}
