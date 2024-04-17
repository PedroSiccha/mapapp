package com.inforad.mapapp.view.orders.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.inforad.mapapp.R
import com.inforad.mapapp.databinding.ActivityListOrderBinding
import com.inforad.mapapp.databinding.FragmentCreateOrderBinding
import com.inforad.mapapp.databinding.FragmentListOrderBinding
import com.inforad.mapapp.model.Order
import com.inforad.mapapp.service.OrderApi
import com.inforad.mapapp.view.orders.adapter.OrderAdapter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class ListOrderFragment : Fragment() {
    private lateinit var binding: FragmentListOrderBinding
    private lateinit var navigationView: NavigationView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var orders: MutableList<Order>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListOrderBinding.inflate(inflater, container, false)
        val view = binding.root

        orders = mutableListOf()
        orderAdapter = OrderAdapter(orders)
        binding.rvListPedidos.adapter = orderAdapter

        fetchOrders()

        return view
    }

    private fun fetchOrders(userId: Int? = 1) {
        val url = "${OrderApi.BASE_URL}orders/get-by-user.php?user_id=$userId" // Reemplaza con la URL de tu backend PHP

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al obtener los pedidos", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body()?.let { responseBody ->
                    try {
                        val json = responseBody.string()
                        val orders = parseJsonToOrders(json)

                        // Configurar RecyclerView
                        val recyclerView: RecyclerView = binding.rvListPedidos// view.findViewById(R.id.rvListPedidos)
                        val adapter = OrderAdapter(orders)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(requireContext())
                        /*
                        val json = responseBody.string()
                        Log.i("Bodi", json.toString())
                        val presentaciones = mutableListOf<String>()
                        val jsonArray = JSONArray(json)
                        Log.i("jsonArray", jsonArray.toString())
                        for (i in 0 until jsonArray.length()) {
                            val presentacion = jsonArray.getJSONObject(i).getString("username") // Reemplaza "name" con el nombre del campo que contiene la presentación en tu JSON
                            presentaciones.add(presentacion)
                        }

                         */
                        // Actualizar el Spinner con las presentaciones obtenidas
//                        spinner?.post {
//                            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, presentaciones.toTypedArray())
//                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                            spinner?.adapter = adapter
//                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Mostrar un mensaje de error al usuario si hay un problema al procesar la respuesta JSON
                        Toast.makeText(requireContext(), "Error al procesar las presentaciones", Toast.LENGTH_SHORT).show()
                    } finally {
                        responseBody.close() // Cerrar el cuerpo de la respuesta
                    }
                }
            }
        })
    }

}