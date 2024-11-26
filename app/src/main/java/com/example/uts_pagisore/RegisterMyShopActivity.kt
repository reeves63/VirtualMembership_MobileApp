package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterMyShopActivity : AppCompatActivity() {

    private lateinit var editShopName: EditText
    private lateinit var editShopDescription: EditText
    private lateinit var shopLocation: EditText
    private lateinit var shopCategories: EditText
    private lateinit var registerButton: Button
    private lateinit var cancelButton: Button
    private lateinit var buttonBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_myshop)

        // Initialize input and buttons
        editShopName = findViewById(R.id.editShopName)
        editShopDescription = findViewById(R.id.editShopDescription)
        shopLocation = findViewById(R.id.editShopLocation)
        shopCategories = findViewById(R.id.editShopCategories)
        registerButton = findViewById(R.id.buttonRegister)
        cancelButton = findViewById(R.id.buttonCancel)
        buttonBack = findViewById(R.id.buttonBack)

        // Register button click
        registerButton.setOnClickListener {
            registerShop()
        }

        // Cancel button click - navigate to Home
        cancelButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // Back button click - navigate to the previous screen
        buttonBack.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya
        }
    }

    private fun registerShop() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        if (userId != null) {
            val shop = hashMapOf(
                "name" to editShopName.text.toString(),
                "description" to editShopDescription.text.toString(),
                "location" to shopLocation.text.toString(),
                "categories" to shopCategories.text.toString(),
                "userId" to userId
            )

            db.collection("shops").add(shop)
                .addOnSuccessListener {
                    Toast.makeText(this, "Shop registered successfully", Toast.LENGTH_SHORT).show()
                    // Navigate to MyShopList after registration
                    val intent = Intent(this, MyShopListActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to register shop", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
