package com.proyectofinal.mycookbook

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btLogin: Button = findViewById(R.id.btLogin)
        val etRegistrate: TextView = findViewById(R.id.tvRegistro)


        auth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()

        btLogin.setOnClickListener {
            login()
        }

        etRegistrate.setOnClickListener {
            registrar()
        }
    }

    private fun login() {
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPass: EditText = findViewById(R.id.etPass)
        val email = etEmail.text.toString()
        val password = etPass.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa los campos de Email y contraseña.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login correcto.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MenuActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Error al intentar iniciar sesión. ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun registrar() {
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPass: EditText = findViewById(R.id.etPass)
        val email = etEmail.text.toString()
        val password = etPass.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa los campos de Email y contraseña.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val id = auth.currentUser?.uid
                    val map = HashMap<String, Any>()
                    map["email"] = email
                    map["password"] = password
                    map["Foto"] = "https://firebasestorage.googleapis.com/v0/b/mycookbook-ab743.appspot.com/o/images%2F31ea7b66-2934-49b6-a26c-948ad84ef48f.jpg?alt=media&token=d4528053-9349-4787-b944-b34f69ce7b10"
                    if (id != null) {
                        mFirestore.collection("Usuarios").document(id)
                            .set(map)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registro correcto.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al guardar los datos.", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Error al intentar registrarse. ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}