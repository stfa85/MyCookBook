package com.proyectofinal.mycookbook

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RecetasActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recetas)

        listView = findViewById(R.id.lvRecetas)
        val userId = intent.getStringExtra("userId")
        if (userId != null) {
            obtenerRecetasUsuario(userId)
        } else {
            obtenerTodasRecetas()
        }
    }

    private fun obtenerRecetasUsuario(userId: String) {
        val recetasCollectionRef = FirebaseFirestore.getInstance().collection("Recetas")
        val query = recetasCollectionRef.whereEqualTo("userId", userId)

        query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(this@RecetasActivity, "Error al obtener las recetas", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            val recetasList = mutableListOf<String>()

            for (document in snapshot?.documents!!) {
                val nombrePlato = document.getString("nombrePlato")
                if (nombrePlato != null) {
                    recetasList.add(nombrePlato)
                }
            }

            val adapter = ArrayAdapter(this@RecetasActivity, android.R.layout.simple_list_item_1, recetasList)
            listView.adapter = adapter

            listView.setOnItemClickListener { _, _, position, _ ->
                val receta = snapshot.documents[position]
                abrirMostrarReceta(receta)
            }
        }
    }


    private fun obtenerTodasRecetas() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val recetas = db.collection("Recetas").get().await()
                withContext(Dispatchers.Main) {
                    mostrarRecetas(recetas.documents)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RecetasActivity,"Error al obtener las recetas: ${e.message}",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarRecetas(recetas: List<DocumentSnapshot>) {
        val listaRecetas = recetas.mapNotNull { doc ->
            doc.getString("nombrePlato")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaRecetas)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val receta = recetas[position]
            abrirMostrarReceta(receta)
        }
    }

    private fun abrirMostrarReceta(receta: DocumentSnapshot) {
        val nombrePlato = receta.getString("nombrePlato")

        if (nombrePlato != null) {
            val intent = Intent(this@RecetasActivity, MostrarRecetaActivity::class.java)
            intent.putExtra("Recetas", receta.data?.toHashMap())
            startActivity(intent)
        } else {
            Toast.makeText(this@RecetasActivity, "La receta no existe", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Map<String, Any>?.toHashMap(): HashMap<String, Any> {
        val hashMap = HashMap<String, Any>()
        this?.let { map ->
            for ((key, value) in map) {
                hashMap[key] = value
            }
        }
        return hashMap
    }
}
