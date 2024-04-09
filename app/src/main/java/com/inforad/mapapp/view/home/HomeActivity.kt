package com.inforad.mapapp.view.home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.inforad.mapapp.R
import com.inforad.mapapp.databinding.ActivityHomeBinding
import com.inforad.mapapp.view.maps.MapFragment
import com.inforad.mapapp.view.orders.create.CreateOrderFragment
import com.inforad.mapapp.view.orders.list.ListOrderFragment

class HomeActivity : AppCompatActivity(), MapFragment.OnLocationPermissionGrantedListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var activeFragment: Fragment
    private lateinit var fragmentManager: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNav()
    }

    override fun onLocationPermissionGranted() {
        recreate()
    }

    private fun setupBottomNav() {
        fragmentManager = supportFragmentManager
        val mapFragment = MapFragment()
        val createOrderFragment = CreateOrderFragment()
        val listOrderFragment = ListOrderFragment()
        activeFragment = mapFragment
        fragmentManager.beginTransaction().add(R.id.hostFragment, listOrderFragment, ListOrderFragment::class.java.name).hide(listOrderFragment).commit()
        fragmentManager.beginTransaction().add(R.id.hostFragment, createOrderFragment, CreateOrderFragment::class.java.name).hide(createOrderFragment).commit()
        fragmentManager.beginTransaction().add(R.id.hostFragment, mapFragment, MapFragment::class.java.name).commit()
        binding.bottomNav.setOnNavigationItemSelectedListener{
            when(it.itemId) {
                R.id.home -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(mapFragment).commit()
                    activeFragment = mapFragment
                    true
                }
                R.id.map -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(mapFragment).commit()
                    activeFragment = mapFragment
                    true
                }
                R.id.createsale -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(createOrderFragment).commit()
                    activeFragment = createOrderFragment
                    true
                }
                R.id.mysale -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(listOrderFragment).commit()
                    activeFragment = listOrderFragment
                    true
                }
                else -> {
                    false
                }
            }
        }
    }
}