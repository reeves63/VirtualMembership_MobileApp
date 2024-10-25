package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class ShopDetailActivity : AppCompatActivity() {

    private lateinit var shopName: TextView
    private lateinit var shopDescription: TextView
    private lateinit var shopLocation: TextView
    private lateinit var shopCategories: TextView
    private lateinit var shopProfileImage: ImageView
    private lateinit var buttonEditShopInfo: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_detail)

        // Inisialisasi view dari layout XML
        shopProfileImage = findViewById(R.id.shopProfileImage)
        shopName = findViewById(R.id.textShopName)
        shopDescription = findViewById(R.id.textShopDescription)
        shopLocation = findViewById(R.id.textShopLocation)
        shopCategories = findViewById(R.id.textShopCategories)
        buttonEditShopInfo = findViewById(R.id.buttonEditShopInfo)

        // Tombol back manual
        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish()
        }

        // Mendapatkan SHOP_ID dari intent
        val shopId = intent.getStringExtra("SHOP_ID")
        if (shopId != null) {
            loadShopDetails(shopId)
        } else {
            Toast.makeText(this, "Shop ID is null", Toast.LENGTH_SHORT).show()
        }

    }

    private fun loadShopDetails(shopId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("shops").document(shopId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    shopName.text = document.getString("name")
                    shopDescription.text = document.getString("description")
                    shopLocation.text = document.getString("location")
                    shopCategories.text = document.getString("categories")

                    // Load gambar profil toko jika ada
                    val profileImageUrl = document.getString("profileImageUrl")
                    if (profileImageUrl != null) {
                        Glide.with(this)
                            .load(profileImageUrl)
                            .into(shopProfileImage)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load shop details", Toast.LENGTH_SHORT).show()
            }
    }
}