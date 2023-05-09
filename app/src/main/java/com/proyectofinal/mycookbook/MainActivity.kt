package com.proyectofinal.mycookbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btLogin: Button = findViewById(R.id.btLogin)
        val etRegistrate: TextView = findViewById(R.id.tvRegistro)


        auth = FirebaseAuth.getInstance()

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
                    Toast.makeText(this, "Registro correcto.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al intentar registrarse. ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}