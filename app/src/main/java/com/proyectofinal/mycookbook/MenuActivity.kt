package com.proyectofinal.mycookbook

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val ivFotoUser: ImageView = findViewById(R.id.ivFotoUser)
        val btMisRecetas: Button = findViewById(R.id.btMisRecetas)
        val btBuscar: Button = findViewById(R.id.btBuscar)
        val btCrear: Button = findViewById(R.id.btCrear)
        val btSalir: Button = findViewById(R.id.btSalir)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        //val fotoUsuario = obtenerFotoUsuario()
        //ivFotoUser.setImageResource(fotoUsuario)


        btMisRecetas.setOnClickListener {
            abrirMisRecetas()
        }

        btBuscar.setOnClickListener {
            abrirBuscarRecetas()
        }

        btCrear.setOnClickListener {
            abrirCrearReceta()
        }

        btSalir.setOnClickListener {
            cerrarSesion()
        }
    }

    /*private fun obtenerFotoUsuario(): Int {
        return R.drawable.user_photo
    }*/

    private fun abrirMisRecetas() {
        val intent = Intent(this, MisRecetasActivity::class.java)
        startActivity(intent)
    }

    private fun abrirBuscarRecetas() {
        val intent = Intent(this, BuscarRecetasActivity::class.java)
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
