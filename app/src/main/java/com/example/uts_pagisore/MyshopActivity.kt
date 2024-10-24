package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.Toast

class MyShopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myshop)

        // Penanganan tombol kembali
        val buttonBack: ImageButton = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish() // Menutup aktivitas dan kembali ke aktivitas sebelumnya
        }

        // Penanganan tombol edit
        val buttonEditShop: ImageButton = findViewById(R.id.buttonEditShop)
        buttonEditShop.setOnClickListener {
            Toast.makeText(this, "Edit Shop clicked", Toast.LENGTH_SHORT).show()
        }

        // Penanganan tombol kamera
        val buttonCamera: ImageButton = findViewById(R.id.buttonCamera)
        buttonCamera.setOnClickListener {
            // Membuka aplikasi kamera
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data")
            Toast.makeText(this, "Picture taken successfully", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 1001
    }
}