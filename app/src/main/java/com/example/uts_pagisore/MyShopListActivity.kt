package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.ImageButton

class MyShopListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShopAdapter
    private val shopsList = mutableListOf<Shop>()
    private lateinit var addShopButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myshop_list)

        // Menampilkan tombol up navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Tombol back manual
        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish() // Kembali ke activity sebelumnya
        }

        recyclerView = findViewById(R.id.recyclerViewShops)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ShopAdapter(shopsList) { shop ->
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

        loadShops()
    }

    private fun loadShops() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        if (userId != null) {
            db.collection("shops")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val shop = document.toObject(Shop::class.java)
                        shop.id = document.id
                        shopsList.add(shop)
                    }
                    adapter.notifyDataSetChanged()
                }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
