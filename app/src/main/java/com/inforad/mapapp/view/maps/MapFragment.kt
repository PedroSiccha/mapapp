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
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.inforad.mapapp.R
import com.inforad.mapapp.databinding.ActivityMapsBinding
import com.inforad.mapapp.databinding.FragmentMapBinding
import com.inforad.mapapp.model.Location
import com.inforad.mapapp.service.ApiService
import com.inforad.mapapp.view.orders.create.CreateOrder
import com.inforad.mapapp.view.orders.list.ListOrderActivity
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
const val LOCATION_PERMISSION_REQUEST_CODE = 1

class MapFragment : Fragment(), MapListener, GpsStatus.Listener, Callback<List<Location>> {
    private lateinit var binding: FragmentMapBinding

    lateinit var mMap: MapView
    lateinit var controller: IMapController
    lateinit var mMyLocationOverlay: MyLocationNewOverlay
    lateinit var progressBar: AlertDialog
    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val options = arrayOf("VENTA", "NO VENTA")
    var token = ""
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root
        progressBar = createProgressDialog()
        bottomSheetBehavior = BottomSheetBehavior.from(binding.sheet)
        viewMenuLateral()
        viewBottomSheet()
//        val bottomNavigationView = binding.bottomNavigationView
////        bottomNavigationView.setOnNavigationItemSelectedListener(this)
//        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//            }
//            true
//        }
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        Configuration.getInstance().load(
            requireContext(),
            requireActivity().getSharedPreferences(getString(R.string.app_name), AppCompatActivity.MODE_PRIVATE)
        )
        mMap = binding.osmmap
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        mMap.mapCenter
        mMap.setMultiTouchControls(true)
        mMap.addMapListener(this)
        viewProgress(status = true)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        getPosition()

        getLocations()

        binding.ivUpdateUbication.setOnClickListener {
            viewProgress(status = true)
            getLocations()
        }

        binding.ivMiUbication.setOnClickListener {
            viewProgress(status = true)
            getPosition()
            getLocations()
        }

        return view
    }

    private fun viewBottomSheet() {
        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun viewMenuLateral() {
        drawerLayout = binding.drawerLayout

//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (requireActivity() is AppCompatActivity) {
            (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        navigationView = binding.navView

        val navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true

                    true
                }

                R.id.nav_ventas -> {
                    val intent = Intent(requireContext(), CreateOrder::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                else -> false
            }
            true
        }
    }

    private fun getPosition() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
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
                viewProgress(status = false)
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

        token = requireActivity().intent.getStringExtra("token").toString()

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
                permissionGrantedListener?.onLocationPermissionGranted()
            }
        }
    }

    interface OnLocationPermissionGrantedListener {
        fun onLocationPermissionGranted()
    }

    private var permissionGrantedListener: OnLocationPermissionGrantedListener? = null

    fun setOnLocationPermissionGrantedListener(listener: OnLocationPermissionGrantedListener) {
        permissionGrantedListener = listener
    }

    private fun createProgressDialog(): AlertDialog {
        val dialogViewOrders = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog, null)
        return AlertDialog.Builder(context)
            .setView(dialogViewOrders)
            .create()
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        return false
    }

    override fun onGpsStatusChanged(event: Int) {}

    override fun onResponse(call: Call<List<Location>>, response: Response<List<Location>>) {
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
                        marker.icon = resources.getDrawable(R.drawable.location)
                        txtEstado = "OK"
                    }
                    "ENTREGADO" -> {
                        marker.icon = resources.getDrawable(R.drawable.alert_location)
                        txtEstado = "OK"
                    }
                    "SIN PEDIDO" -> {
                        marker.icon = resources.getDrawable(R.drawable.alert_location)
                        txtEstado = "OK"
                    }
                    else -> marker.icon = resources.getDrawable(R.drawable.location)
                }


                marker.setOnMarkerClickListener { marker, mapView ->
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    createSelect(binding.spinner)
                    binding.markerTitle.text = location.localname
                    binding.markerDescription.text = location.descripttion
                    binding.markerEmail.text = location.email
                    binding.markerPhone.text = location.phone
                    binding.markerCategory.text = location.category
                    binding.markerStatus.text = location.status
                    binding.buttonVerMas.text = txtEstado
                    binding.buttonVerMas.setOnClickListener {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    true
                }
                mMap.overlays.add(marker)
            }
            viewProgress(status = false)
        } else {}
    }

    private fun createSelect(spinner: Spinner?) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.adapter = adapter
    }

    private fun viewProgress(status: Boolean) {

        if (status) {
            progressBar.show()
        }
        if (!status) {
            progressBar.dismiss()
        }
    }

    override fun onFailure(call: Call<List<Location>>, t: Throwable) {}

}