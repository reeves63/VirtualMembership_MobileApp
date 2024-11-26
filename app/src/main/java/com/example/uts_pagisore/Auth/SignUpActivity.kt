package com.example.uts_pagisore.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.uts_pagisore.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvLogin: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Inisialisasi Firebase Auth dan Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Inisialisasi elemen UI
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnSignUp = findViewById(R.id.btn_sign_up)
        tvLogin = findViewById(R.id.tv_login)

        // Tombol Sign Up
        btnSignUp.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (!validateInputs(username, password)) return@setOnClickListener

            // Pengecekan apakah email sudah terdaftar di Firebase
            auth.fetchSignInMethodsForEmail(username)
                .addOnCompleteListener { task ->
                    val isNewUser = task.result?.signInMethods?.isEmpty() ?: true
                    if (isNewUser) {
                        // Lanjutkan dengan pendaftaran
                        createUserWithEmail(username, password)
                    } else {
                        // Email sudah terdaftar
                        Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Teks login untuk berpindah ke halaman login
        tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Tutup activity ini
        }
    }

    // Fungsi untuk memvalidasi input pengguna
    private fun validateInputs(username: String, password: String): Boolean {
        if (username.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty() || password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // Fungsi untuk membuat pengguna baru di Firebase Authentication
    private fun createUserWithEmail(username: String, password: String) {
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Pengguna berhasil dibuat di Firebase Authentication
                    val firebaseUser: FirebaseUser? = auth.currentUser
                    val userId = firebaseUser?.uid

                    if (userId != null) {
                        // Simpan data pengguna ke Firestore
                        saveUserProfile(userId, username)
                    }
                } else {
                    // Jika terjadi kesalahan
                    Toast.makeText(this, "Sign Up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Fungsi untuk menyimpan data pengguna ke Firestore
    private fun saveUserProfile(userId: String, username: String) {
        val userData = hashMapOf(
            "email" to username
        )

        db.collection("users").document(userId).set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "User profile created", Toast.LENGTH_SHORT).show()
                // Arahkan ke halaman login setelah sukses mendaftar
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Tutup activity ini
            }
            .addOnFailureListener { e ->
                // Jika terjadi kesalahan saat menulis ke Firestore
                Toast.makeText(this, "Error creating user profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
