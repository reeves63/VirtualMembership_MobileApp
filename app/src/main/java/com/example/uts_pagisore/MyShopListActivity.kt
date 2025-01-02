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

        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recyclerViewShops)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ShopAdapter(shopsList) { shop ->
            val intent = Intent(this, ShopDetailActivity::class.java)
            intent.putExtra("SHOP_ID", shop.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        addShopButton = findViewById(R.id.buttonAddShop)
        addShopButton.setOnClickListener {
            val intent = Intent(this, RegisterMyShopActivity::class.java)
            startActivity(intent)
        }

        loadShops()
    }

    private fun loadShops() {
        if (userId == null) {
            showToast("User not logged in.")
            return
        }

        db.collection("shops")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                shopsList.clear()
                for (document in documents) {
                    val shop = document.toObject(Shop::class.java).apply {
                        id = document.id
                    }
                    shopsList.add(shop)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                showToast("Failed to load shops. Please try again.")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
