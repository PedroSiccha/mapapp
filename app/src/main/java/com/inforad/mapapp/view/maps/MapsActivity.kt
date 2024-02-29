package com.inforad.mapapp.view.maps

import android.content.pm.PackageManager
import android.location.GpsStatus
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest
import com.inforad.mapapp.R
import com.inforad.mapapp.databinding.ActivityMapsBinding
import com.inforad.mapapp.model.Location
import com.inforad.mapapp.service.ApiService
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
        val token = intent.getStringExtra("token")
        mMap = binding.osmmap
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        mMap.mapCenter
        mMap.setMultiTouchControls(true)
        mMap.addMapListener(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
                mMyLocationOverlay = MyLocationNewOverlay(mMap)
                mMap.overlays.add(mMyLocationOverlay)
                mMyLocationOverlay.enableMyLocation()
                mMyLocationOverlay.enableFollowLocation()
            }
        }

        getLocations()

        binding.btnUpdate.setOnClickListener {
            getLocations()
        }

//        val locations = listOf(
//            Location("Local 1", -9.223612, -77.6855180),
//            Location("Local 2", -9.224941, -77.688457),
//            Location("Local 3", -9.222564, -77.687502),
//            Location("Local 3", -9.222627,-77.6830392)
//            // Agrega más ubicaciones según sea necesario
//        )

//        for (location in locations) {
//            val marker = Marker(mMap)
//            marker.position = GeoPoint(location.latitude, location.longitude)
//            marker.title = location.name
//            mMap.overlays.add(marker)
//        }

    }

    private fun getLocations() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.162.130:80/map_backend/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // Aquí deberías recuperar el token de autenticación guardado previamente
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
                // Permission granted, initialize location services
                // You can also show a message to user that location permission is granted and reload the activity
                recreate()
            } else {
                // Permission denied, handle this accordingly
            }
        }
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        Log.e("TAG", "onCreate:la ${event?.source?.getMapCenter()?.latitude}")
        Log.e("TAG", "onCreate:lo ${event?.source?.getMapCenter()?.longitude}")
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        Log.e("TAG", "onZoom zoom level: ${event?.zoomLevel}   source:  ${event?.source}")
        return false
    }

    override fun onGpsStatusChanged(event: Int) {

    }

    data class Location(val name: String, val latitude: Double, val longitude: Double)

    override fun onResponse(
        call: Call<List<com.inforad.mapapp.model.Location>>,
        response: Response<List<com.inforad.mapapp.model.Location>>
    ) {
        if (response.isSuccessful) {
            val locations = response.body()
            // Agregar marcadores para cada ubicación en el mapa
            locations?.forEach { location ->
                Log.e("LOCATION", location.toString())
                val marker = Marker(mMap)
                marker.position = GeoPoint((location.latitude * 1E6).toInt(), (location.longitude*1E6).toInt())
                marker.title = location.localname
                mMap.overlays.add(marker)
            }
        } else {
            // Manejar errores de la respuesta
        }
    }

    override fun onFailure(call: Call<List<com.inforad.mapapp.model.Location>>, t: Throwable) {
        Log.e("Error: ", t.message.toString())
    }

}