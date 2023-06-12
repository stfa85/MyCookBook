package com.proyectofinal.mycookbook

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID

class ModificarRecetaActivity : AppCompatActivity() {

    private lateinit var etNombrePlato: EditText
    private lateinit var etIngredientes: EditText
    private lateinit var etCantidadPersonas: EditText
    private lateinit var etTiempo: EditText
    private lateinit var etInstrucciones: EditText
    private lateinit var btModificar: Button
    private lateinit var btCargarFoto: Button
    private lateinit var ivFotoReceta: ImageView
    private lateinit var storageRef: StorageReference
    private var fotoUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_receta)

        etNombrePlato = findViewById(R.id.etNombrePlato)
        etIngredientes = findViewById(R.id.etIngredientes)
        etCantidadPersonas = findViewById(R.id.etCantidadPersonas)
        etTiempo = findViewById(R.id.etTiempo)
        etInstrucciones = findViewById(R.id.etInstrucciones)

        btModificar = findViewById(R.id.btModificar)
        btCargarFoto = findViewById(R.id.btCargarFoto)
        ivFotoReceta = findViewById(R.id.ivFotoReceta)

        btModificar.setOnClickListener { modificarReceta() }
        btCargarFoto.setOnClickListener { seleccionarFuente() }

        storageRef = FirebaseStorage.getInstance().reference
        val nombrePlato = intent.getStringExtra("nombrePlato")
        val ingredientes = intent.getStringExtra("ingredientes")
        val cantidadPersonas = intent.getStringExtra("cantidadPersonas")
        val tiempoEstimado = intent.getStringExtra("tiempo")
        val instrucciones = intent.getStringExtra("instrucciones")
        var fotoUrl = intent.getStringExtra("Foto")

        etNombrePlato.setText(nombrePlato)
        etIngredientes.setText(ingredientes)
        etCantidadPersonas.setText(cantidadPersonas)
        etTiempo.setText(tiempoEstimado)
        etInstrucciones.setText(instrucciones)

        if (fotoUrl != null) {
            Glide.with(this)
                .load(fotoUrl)
                .into(ivFotoReceta)
        }
    }

    private fun modificarReceta() {
        val nombrePlato = etNombrePlato.text.toString()
        val ingredientes = etIngredientes.text.toString()
        val cantidadPersonas = etCantidadPersonas.text.toString()
        val tiempoEstimado = etTiempo.text.toString()
        val instrucciones = etInstrucciones.text.toString()

        val receta = hashMapOf<String, Any>(
            "nombrePlato" to nombrePlato,
            "ingredientes" to ingredientes,
            "cantidadPersonas" to cantidadPersonas,
            "tiempo" to tiempoEstimado,
            "instrucciones" to instrucciones
        )

        val firestore = FirebaseFirestore.getInstance()
        val recetasCollectionRef = firestore.collection("Recetas")

        recetasCollectionRef
            .whereEqualTo("nombrePlato", nombrePlato)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (documentSnapshot in querySnapshot.documents) {
                    val recetaId = documentSnapshot.id
                    recetasCollectionRef.document(recetaId)
                        .update(receta)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Receta modificada.", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error modificando la receta.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener la receta.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun seleccionarFuente() {
        val opciones = arrayOf<CharSequence>("Cámara", "Galería")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccionar fuente de imagen")
        builder.setItems(opciones) { dialog, item ->
            when (item) {
                0 -> abrirCamara()
                1 -> abrirGaleria()
            }
        }
        builder.show()
    }

    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 1)
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 2)
    }

    private fun guardarImagen(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()

        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

        val uploadTask = imageRef.putBytes(byteArray)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                fotoUrl = downloadUri.toString()
                Glide.with(this).load(bitmap).into(ivFotoReceta)
            } else {
                Toast.makeText(this, "Error en la subida de la imagen.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                1 -> {
                    val imagen = data?.extras?.get("data") as? Bitmap
                    imagen?.let {
                        guardarImagen(it)
                    }
                }
                2 -> {
                    val imagenURL = data?.data
                    val imagen =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, imagenURL)
                    imagen?.let {
                        guardarImagen(it)
                    }
                }
            }
        }
    }
}