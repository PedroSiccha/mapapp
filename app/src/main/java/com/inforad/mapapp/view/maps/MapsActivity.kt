package com.inforad.mapapp.view.maps

import android.content.pm.PackageManager
import android.location.GpsStatus
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.app.AlertDialog
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.inforad.mapapp.R
import com.inforad.mapapp.databinding.ActivityMapsBinding
import com.inforad.mapapp.model.Location
import com.inforad.mapapp.service.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MapsActivity : AppCompatActivity(), MapListener, GpsStatus.Listener, Callback<List<Location>> {
    private lateinit var binding: ActivityMapsBinding
    lateinit var mMap: MapView
    lateinit var controller: IMapController
    lateinit var mMyLocationOverlay: MyLocationNewOverlay
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        )
        mMap = binding.osmmap
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        mMap.mapCenter
        mMap.setMultiTouchControls(true)
        mMap.addMapListener(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getPosition()

        getLocations()

        binding.ivUpdateUbication.setOnClickListener {
            getLocations()
        }

        binding.ivMiUbication.setOnClickListener {
            getPosition()
            getLocations()
        }

    }

    private fun getPosition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val geoPoint = GeoPoint(it.latitude, it.longitude)
                controller = mMap.controller
                controller.setZoom(17.0)
                controller.setCenter(geoPoint)

//                val newMarker = Marker(mMap)
////                newMarker.position = geoPoint
//                newMarker.icon = resources.getDrawable(R.drawable.marker)
//                mMap.overlays.add(newMarker)

                mMyLocationOverlay = MyLocationNewOverlay(mMap)
                mMap.overlays.add(mMyLocationOverlay)
                mMyLocationOverlay.enableMyLocation()
                mMyLocationOverlay.enableFollowLocation()
            }
        }
    }

    private fun getLocations() {
        val interceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val request = chain.request()
                println("Solicitando a la URL: ${request.method()} ${request.url()}")
                return chain.proceed(request)
            }
        }

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor)
        val client = httpClient.build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://clincia.000webhostapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val token = intent.getStringExtra("token")

        val call = apiService.getLocations("Bearer $token")
        call.enqueue(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate()
            } else {}
        }
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        return false
    }

    override fun onGpsStatusChanged(event: Int) {}

    data class Location(val name: String, val latitude: Double, val longitude: Double)

    override fun onResponse(
        call: Call<List<com.inforad.mapapp.model.Location>>,
        response: Response<List<com.inforad.mapapp.model.Location>>
    ) {
        if (response.isSuccessful) {
            val locations = response.body()
            locations?.forEach { location ->
                val marker = Marker(mMap)
                marker.position = GeoPoint((location.latitude * 1E6).toInt(), (location.longitude*1E6).toInt())
                marker.title = "${location.localname}"
                marker.subDescription = "Descripcion: ${location.descripttion}\nTelefono: ${location.phone}\nEmail: ${location.email}\nCategoria: ${location.category}"
                var txtEstado = ""
                when (location.status) {
                    "PENDIENTE" -> {
                        marker.icon = resources.getDrawable(R.drawable.markerlocal)
                        txtEstado = "Mis Pedidos"
                    }
                    "ENTREGADO" -> {
                        marker.icon = resources.getDrawable(R.drawable.ubicacionentregado)
                        txtEstado = "Crear Pedidos"
                    }
                    "SIN PEDIDO" -> {
                        marker.icon = resources.getDrawable(R.drawable.sinpedido)
                        txtEstado = "Crear Pedidos"
                    }
                    else -> marker.icon = resources.getDrawable(R.drawable.markerlocal)
                }


                marker.setOnMarkerClickListener { marker, mapView ->
                    val dialogView = LayoutInflater.from(this@MapsActivity).inflate(R.layout.custom_marker_dialog, null)
                    val dialog = AlertDialog.Builder(this@MapsActivity)
                        .setView(dialogView)
                        .create()
                    val markerPoint = mapView.projection.toPixels(marker.position, null)
                    val offsetX = -(dialogView.width / 2)
                    val offsetY = -(dialogView.height + marker.icon.getIntrinsicHeight())
                    dialog.window?.attributes?.apply {
                        x = markerPoint.x + offsetX
                        y = markerPoint.y + offsetY
                        gravity = Gravity.TOP or Gravity.START
                    }

                    dialogView.findViewById<TextView>(R.id.markerTitle).text = location.localname
                    dialogView.findViewById<TextView>(R.id.markerDescription).text = location.descripttion
                    dialogView.findViewById<TextView>(R.id.markerEmail).text = location.email
                    dialogView.findViewById<TextView>(R.id.markerPhone).text = location.phone
                    dialogView.findViewById<TextView>(R.id.markerCategory).text = location.category
                    dialogView.findViewById<TextView>(R.id.markerStatus).text = location.status
                    dialogView.findViewById<TextView>(R.id.buttonVerMas).setText(txtEstado)
                    dialogView.findViewById<ImageView>(R.id.ivClose).setOnClickListener {
                        dialog.dismiss()
                    }
                    dialogView.findViewById<Button>(R.id.buttonVerMas).setOnClickListener {
                        dialog.dismiss()
                    }

                    if (!isFinishing) {
                        dialog.show()
                    }
                    true

                }
                mMap.overlays.add(marker)

            }
        } else {}
    }

    val markerListener = object : Marker.OnMarkerClickListener {
        override fun onMarkerClick(marker: Marker?, mapView: MapView?): Boolean {
            marker?.let {
                // Crear un cuadro de diálogo personalizado
                val dialogView = LayoutInflater.from(applicationContext).inflate(R.layout.custom_marker_dialog, null)
                val dialog = AlertDialog.Builder(applicationContext)
                    .setView(dialogView)
                    .create()

                // Configurar título y descripción
                dialogView.findViewById<TextView>(R.id.markerTitle).text = marker.title
                dialogView.findViewById<TextView>(R.id.markerDescription).text = marker.snippet

                // Configurar botón "Ver más"
                dialogView.findViewById<Button>(R.id.buttonVerMas).setOnClickListener {
                    // Aquí puedes manejar la acción cuando se hace clic en el botón "Ver más"
                    // Por ejemplo, abrir una nueva actividad o mostrar más detalles en otro diálogo
                    // Puedes acceder a los detalles del marcador desde 'marker' (como title, snippet, etc.)
                    //showMoreDetailsDialog(marker)
                    dialog.dismiss() // Cerrar el cuadro de diálogo actual
                }

                // Mostrar el cuadro de diálogo personalizado
                dialog.show()
            }

            // Devolver true para indicar que se ha manejado el clic en el marcador
            return true
        }
    }

    override fun onFailure(call: Call<List<com.inforad.mapapp.model.Location>>, t: Throwable) {}

}