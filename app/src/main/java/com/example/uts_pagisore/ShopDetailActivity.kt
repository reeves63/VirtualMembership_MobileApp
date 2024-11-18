package com.example.uts_pagisore

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class ShopDetailActivity : AppCompatActivity() {

    private lateinit var shopName: TextView
    private lateinit var shopDescription: TextView
    private lateinit var shopLocation: TextView
    private lateinit var shopCategories: TextView
    private lateinit var shopProfileImage: ImageView
    private lateinit var buttonEditShopInfo: Button
    private lateinit var buttonScanQR: Button

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
        buttonScanQR = findViewById(R.id.buttonScanQR)

        // Tombol back manual
        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish()
        }

        // Tombol Scan QR untuk membuka kamera
        buttonScanQR.setOnClickListener {
            checkCameraPermissionAndOpenCamera()
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

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivity(cameraIntent)
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 1001
    }
}
