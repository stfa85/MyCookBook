package com.proyectofinal.mycookbook

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.proyectofinal.mycookbook.databinding.ActivityMenuBinding
import java.io.ByteArrayOutputStream
import java.util.UUID


class MenuActivity : AppCompatActivity() {
    private lateinit var ivFotoUser: ImageView
    private lateinit var btMisRecetas: Button
    private lateinit var btTodasRecetas: Button
    private lateinit var btCrear: Button
    private lateinit var btSalir: Button
    private lateinit var btCargarFoto: Button
    private lateinit var binding: ActivityMenuBinding

    private val storage = Firebase.storage
    private val storageRef = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ivFotoUser = findViewById(R.id.ivFotoUser)
        btMisRecetas = findViewById(R.id.btMisRecetas)
        btTodasRecetas =findViewById(R.id.btTodasRecetas)
        btCrear = findViewById(R.id.btCrear)
        btSalir = findViewById(R.id.btSalir)
        btCargarFoto = findViewById(R.id.btCargarFoto)

        binding.btCargarFoto.setOnClickListener { seleccionarFuente() }
        btMisRecetas.setOnClickListener { abrirMisRecetas() }
        btTodasRecetas.setOnClickListener{ abrirTodasRecetas() }
        btCrear.setOnClickListener { abrirCrearReceta() }
        btSalir.setOnClickListener {cerrarSesion()}
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
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                1 -> {
                    val imagen = data?.extras?.get("data") as? Bitmap
                    imagen?.let {
                        guardarImagen(it)
                        Glide.with(this).load(it).into(ivFotoUser)
                    }
                }

                2 -> {
                    val imagenURL = data?.data
                    val imagen =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, imagenURL)
                    imagen?.let {
                        guardarImagen(it)
                        Glide.with(this).load(it).into(ivFotoUser)
                    }
                }
            }
        }
    }

    private fun abrirMisRecetas() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val intent = Intent(this, RecetasActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }


    private fun abrirCrearReceta() {
        val intent = Intent(this, CrearRecetaActivity::class.java)
        startActivity(intent)
    }
    private fun abrirTodasRecetas() {
        val intent = Intent(this, RecetasActivity::class.java)
        startActivity(intent)

    }

    private fun cerrarSesion() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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
                guardarUrlEnFirestore(downloadUri.toString())
            } else {
                Toast.makeText(this, "Error en la subioda de la imagen.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarUrlEnFirestore(url: String) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let { currentUser ->
            val userRef = FirebaseFirestore.getInstance().collection("Usuarios").document(currentUser.uid)
            val data = hashMapOf<String, Any>("Foto" to url)
            userRef.update(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Foto actualizada en la Base de datos.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error al actualizar la foto en la Base de datos.", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }
    }

}