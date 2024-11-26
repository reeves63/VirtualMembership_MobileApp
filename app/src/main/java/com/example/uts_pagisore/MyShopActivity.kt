package com.example.uts_pagisore

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.uts_pagisore.databinding.ActivityMyshopBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MyShopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyshopBinding
    private val db = FirebaseFirestore.getInstance()
    private var shopId: String? = null
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyshopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Terima SHOP_ID dari intent
        shopId = intent.getStringExtra("SHOP_ID")

        if (shopId != null) {
            loadShopData()
        }

        // Back button click listener
        binding.buttonBack.setOnClickListener {
            finish()
        }

        // Save button click listener
        binding.buttonSave.setOnClickListener {
            saveShopData()
        }

        // Change picture button click listener
        binding.buttonChangePhoto.setOnClickListener {
            openGallery()
        }
    }

    private fun loadShopData() {
        shopId?.let { id ->
            db.collection("shops").document(id).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        binding.editShopName.setText(document.getString("name"))
                        binding.editShopDescription.setText(document.getString("description"))
                        binding.editShopLocation.setText(document.getString("location"))
                        binding.editShopCategories.setText(document.getString("categories"))
                        binding.editPointConversionRate.setText(
                            document.getDouble("pointConversionRate")?.toString()
                        )

                        val profileImageUrl = document.getString("profileImageUrl")
                        profileImageUrl?.let {
                            Glide.with(this)
                                .load(it)
                                .placeholder(R.drawable.profile_picture_placeholder)
                                .into(binding.shopProfileImage)
                        }
                    } else {
                        Toast.makeText(this, "Shop not found!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load shop data.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveShopData() {
        if (shopId != null) {
            val shopData = mapOf(
                "name" to binding.editShopName.text.toString(),
                "description" to binding.editShopDescription.text.toString(),
                "location" to binding.editShopLocation.text.toString(),
                "categories" to binding.editShopCategories.text.toString(),
                "pointConversionRate" to (binding.editPointConversionRate.text.toString().toDoubleOrNull() ?: 0.0)
            )

            db.collection("shops").document(shopId!!)
                .update(shopData)
                .addOnSuccessListener {
                    if (selectedImageUri != null) {
                        // Jika ada gambar baru, upload gambar
                        uploadShopImage(selectedImageUri!!, shopId!!)
                    } else {
                        // Jika tidak ada gambar baru
                        Toast.makeText(this, "Shop data saved successfully!", Toast.LENGTH_SHORT).show()
                        finishToShopDetail()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save shop data.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Shop ID not found.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadShopImage(uri: Uri, shopId: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("shopImages/$shopId.jpg")
        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    db.collection("shops").document(shopId)
                        .update("profileImageUrl", downloadUri.toString())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Image and data saved successfully!", Toast.LENGTH_SHORT).show()
                            finishToShopDetail()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update image URL.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            selectedImageUri = result.data?.data
            selectedImageUri?.let { uri ->
                binding.shopProfileImage.setImageURI(uri)
            }
        }
    }

    private fun finishToShopDetail() {
        val intent = Intent(this, ShopDetailActivity::class.java)
        intent.putExtra("SHOP_ID", shopId) // Pastikan SHOP_ID dikirim kembali
        startActivity(intent)
        finish()
    }
}
