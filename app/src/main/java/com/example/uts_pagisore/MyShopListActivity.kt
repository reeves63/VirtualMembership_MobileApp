package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyShopListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShopAdapter
    private val shopsList = mutableListOf<Shop>()
    private lateinit var addShopButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myshop_list)

        // Tombol Back Manual
        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish() // Kembali ke activity sebelumnya
        }

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recyclerViewShops)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ShopAdapter(shopsList) { shop ->
            // Pindah ke ShopDetailActivity dengan SHOP_ID
            val intent = Intent(this, ShopDetailActivity::class.java)
            intent.putExtra("SHOP_ID", shop.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Inisialisasi tombol "Add Shop"
        addShopButton = findViewById(R.id.buttonAddShop)
        addShopButton.setOnClickListener {
            // Arahkan user ke halaman pendaftaran toko baru
            val intent = Intent(this, RegisterMyShopActivity::class.java)
            startActivity(intent)
        }

        // Load daftar toko
        loadShops()
    }

    private fun loadShops() {
        if (userId == null) {
            // User tidak login, tampilkan pesan error
            showToast("User not logged in.")
            return
        }

        // Query Firestore untuk mendapatkan daftar toko
        db.collection("shops")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                // Kosongkan daftar untuk menghindari duplikasi
                shopsList.clear()
                for (document in documents) {
                    val shop = document.toObject(Shop::class.java).apply {
                        id = document.id // Set ID dokumen
                    }
                    shopsList.add(shop)
                }
                adapter.notifyDataSetChanged() // Perbarui RecyclerView
            }
            .addOnFailureListener {
                showToast("Failed to load shops. Please try again.")
            }
    }

    // Fungsi untuk menampilkan pesan toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
