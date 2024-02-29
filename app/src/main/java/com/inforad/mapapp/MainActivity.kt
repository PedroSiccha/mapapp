package com.inforad.mapapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.inforad.mapapp.databinding.ActivityMainBinding
import com.inforad.mapapp.view.maps.MapsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnNext.setOnClickListener {
            validateLogin(binding.etEmail.text.toString().trim(), binding.etPassword.text.toString().trim())
        }
    }

    private fun validateLogin(email: String, password: String) {
        Log.e("EMAIL", email)
        Log.e("Password", password)
        if (email == "pedro" && password == "123456") {
            Toast.makeText(applicationContext, "Bienvenido Pedro", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
            return
        }
        Toast.makeText(applicationContext, "Datos de usuario incorrecto", Toast.LENGTH_SHORT).show()
    }
}