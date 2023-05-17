package com.proyectofinal.mycookbook

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val ivFotoUser: ImageView = findViewById(R.id.ivFotoUser)
        val btMisRecetas: Button = findViewById(R.id.btMisRecetas)
        val btCrear: Button = findViewById(R.id.btCrear)
        val btSalir: Button = findViewById(R.id.btSalir)


        val fotoUsuario = ivFotoUser


       btMisRecetas.setOnClickListener {
            abrirMisRecetas()
        }

        btCrear.setOnClickListener {
            abrirCrearReceta()
        }

        btSalir.setOnClickListener {
            cerrarSesion()
        }
    }


    private fun abrirMisRecetas() {
        val intent = Intent(this, MisRecetasActivity::class.java)
        startActivity(intent)
    }


    private fun abrirCrearReceta() {
        val intent = Intent(this, CrearRecetaActivity::class.java)
        startActivity(intent)
    }

    private fun cerrarSesion() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
