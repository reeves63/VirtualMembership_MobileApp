package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.uts_pagisore.Message.CreateMessageActivity
import com.example.uts_pagisore.databinding.ActivityShopDetailBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ShopDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopDetailBinding
    private val db = FirebaseFirestore.getInstance()
    private var shopId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shopId = intent.getStringExtra("SHOP_ID")

        if (shopId == null) {
            Toast.makeText(this, "Error: Shop ID not found!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRealtimeUpdates()

        loadShopDetails()

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.buttonEditShopInfo.setOnClickListener {
            val intent = Intent(this, MyShopActivity::class.java)
            intent.putExtra("SHOP_ID", shopId)
            startActivity(intent)
        }

        binding.buttonScanQR.setOnClickListener {
            val intent = Intent(this, ScanQRActivity::class.java)
            intent.putExtra("SHOP_ID", shopId)
            startActivity(intent)
        }

        binding.buttonCreateAnnouncement.setOnClickListener {
            val intent = Intent(this, CreateMessageActivity::class.java)
            intent.putExtra("SHOP_ID", shopId)
            startActivity(intent)
        }

    }

    private fun loadShopDetails() {
        shopId?.let { id ->
            db.collection("shops").document(id).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        binding.textShopName.text = document.getString("name")
                        binding.textShopDescription.text = document.getString("description")
                        binding.textShopLocation.text = document.getString("location")
                        binding.textShopCategories.text = document.getString("categories")
                        binding.textPointConversionRate.text =
                            "1 Point = Rp ${document.getDouble("pointConversionRate")?.toInt() ?: 0}"

                        val profileImageUrl = document.getString("profileImageUrl")
                        profileImageUrl?.let {
                            Glide.with(this)
                                .load(it)
                                .placeholder(R.drawable.profile_picture_placeholder)
                                .into(binding.shopProfileImage)
                        }
                    } else {
                        Toast.makeText(this, "Shop not found!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load shop details.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupRealtimeUpdates() {
        shopId?.let { id ->
            db.collection("shops").document(id)
                .addSnapshotListener { document, e ->
                    if (e != null) {
                        Toast.makeText(this, "Failed to listen for updates: ${e.message}", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }

                    if (document != null && document.exists()) {
                        updateUIWithShopDetails(document)
                    }
                }
        }
    }


    private fun updateUIWithShopDetails(document: DocumentSnapshot) {
        binding.textShopName.text = document.getString("name")
        binding.textShopDescription.text = document.getString("description")
        binding.textShopLocation.text = document.getString("location")
        binding.textShopCategories.text = document.getString("categories")
        binding.textPointConversionRate.text =
            "1 Point = Rp ${document.getDouble("pointConversionRate")?.toInt() ?: 0}"

        val profileImageUrl = document.getString("profileImageUrl")
        profileImageUrl?.let {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.profile_picture_placeholder)
                .into(binding.shopProfileImage)
        }
    }


    override fun onResume() {
        super.onResume()
        setupRealtimeUpdates()
    }
}
