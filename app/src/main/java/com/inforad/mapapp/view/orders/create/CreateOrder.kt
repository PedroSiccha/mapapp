package com.inforad.mapapp.view.orders.create

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.inforad.mapapp.R
import com.inforad.mapapp.databinding.ActivityCreateOrderBinding
import com.inforad.mapapp.databinding.ActivityMapsBinding

class CreateOrder : AppCompatActivity() {
    private lateinit var binding: ActivityCreateOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}