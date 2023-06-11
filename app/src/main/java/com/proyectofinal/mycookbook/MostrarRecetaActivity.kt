package com.proyectofinal.mycookbook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore


class MostrarRecetaActivity : AppCompatActivity() {
    private lateinit var ivFotoReceta: ImageView
    private lateinit var tvNombrePlato: TextView
    private lateinit var tvIngredientes: TextView
    private lateinit var tvCantidadPersonas: TextView
    private lateinit var tvInstrucciones: TextView
    private lateinit var tvTiempo: TextView
    private lateinit var btEliminar: Button
    private lateinit var btModificar: Button
    private lateinit var btMenu: Button
    private var wakeLock: PowerManager.WakeLock? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
            "MiApp::WakeLock"
        )

        setContentView(R.layout.activity_mostrar_receta)

        ivFotoReceta = findViewById(R.id.ivFotoReceta)
        tvNombrePlato = findViewById(R.id.tvNombrePlato)
        tvIngredientes = findViewById(R.id.tvIngredientes)
        tvCantidadPersonas = findViewById(R.id.tvCantidadPersonas)
        tvTiempo = findViewById(R.id.tvTiempo)
        tvInstrucciones = findViewById(R.id.tvInstrucciones)
        btEliminar = findViewById(R.id.btEliminar)
        btModificar = findViewById(R.id.btModificar)
        btMenu = findViewById(R.id.btMenu)

        val receta = intent.getSerializableExtra("Recetas") as HashMap<String, Any>?

        if (receta != null) {

            mostrarReceta(receta)
        } else {
            Toast.makeText(this, "La receta no existe", Toast.LENGTH_SHORT).show()
        }

        btEliminar.setOnClickListener {
                if (receta != null) {
                        borrarReceta(receta)
                }
        }

        btModificar.setOnClickListener {
            val intent = Intent(this, ModificarRecetaActivity::class.java)
            intent.putExtra("nombrePlato", tvNombrePlato.text.toString())
            intent.putExtra("ingredientes", tvIngredientes.text.toString())
            intent.putExtra("cantidadPersonas", tvCantidadPersonas.text.toString())
            intent.putExtra("tiempo", tvTiempo.text.toString())
            intent.putExtra("instrucciones", tvInstrucciones.text.toString())
            startActivity(intent)
        }

        btMenu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }
    private fun borrarReceta(receta: HashMap<String, Any>) {
        val nombrePlato = receta["nombrePlato"] as? String

        val confirmDialog = AlertDialog.Builder(this)
            .setTitle("Eliminar Receta")
            .setMessage("¿Estás seguro que deseas eliminar esta receta?")
            .setPositiveButton("Sí") { _, _ ->
                val recetasCollectionRef = FirebaseFirestore.getInstance().collection("Recetas")
                if (nombrePlato != null) {
                    recetasCollectionRef
                        .whereEqualTo("nombrePlato", nombrePlato)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            for (documentSnapshot in querySnapshot.documents) {
                                documentSnapshot.reference
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Receta eliminada correctamente", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error al eliminar la receta", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al obtener la receta", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .setNegativeButton("No", null)
            .create()

        confirmDialog.show()
    }
    private fun mostrarReceta(receta: HashMap<String, Any>) {
        val nombrePlato = receta["nombrePlato"] as? String
        val ingredientes = receta["ingredientes"] as? String
        val cantidadPersonas = receta["cantidadPersonas"] as? String
        val tiempoEstimado = receta["tiempo"] as? String
        val instrucciones = receta["instrucciones"] as? String

        if (nombrePlato != null) {
            tvNombrePlato.text = nombrePlato
        }
        if (ingredientes != null) {
            tvIngredientes.text = ingredientes
        }
        if (cantidadPersonas != null) {
            tvCantidadPersonas.text = cantidadPersonas.toString()
        }
        if (tiempoEstimado != null) {
            tvTiempo.text = tiempoEstimado.toString()
        }
        if (instrucciones != null) {
            tvInstrucciones.text = instrucciones
        }

        val fotoUrl = receta["foto"] as? String
        if (fotoUrl != null) {
            Glide.with(this)
                .load(fotoUrl)
                .into(ivFotoReceta)
        }
    }
    private fun obtenerWakeLock() {
        wakeLock?.acquire()
    }
    private fun liberarWakeLock() {
        wakeLock?.release()
    }
    override fun onResume() {
        super.onResume()
        obtenerWakeLock()
    }

    override fun onPause() {
        super.onPause()
        liberarWakeLock()
    }
}