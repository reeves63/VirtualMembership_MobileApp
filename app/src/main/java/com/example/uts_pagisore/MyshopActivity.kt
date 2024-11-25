package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class MyShopActivity : AppCompatActivity() {
    private var isEditMode = false // Status mode edit
    private var originalShopName = "" // Nama toko sebelum mode edit
    private var originalShopLogo: ImageView? = null // Logo toko sebelum mode edit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myshop)

        // Initialize Back Button
        val buttonBack: ImageButton = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish()
        }

        // Initialize Shop Name TextView
        val shopNameTextView: TextView = findViewById(R.id.shopName)
        val shopName = intent.getStringExtra("SHOP_NAME")
        shopNameTextView.text = shopName ?: "Nama Toko Tidak Tersedia"

        // Initialize Edit Shop Button
        val buttonEditShop: ImageButton = findViewById(R.id.buttonEditShop)
        buttonEditShop.setOnClickListener {
            Toast.makeText(this, "Edit Shop clicked", Toast.LENGTH_SHORT).show()
        }

        // Initialize Shop Logo ImageView
        val shopLogo: ImageView = findViewById(R.id.shopLogo)
        // You can add further logic to load or change the shop logo if required

        // Initialize Camera Button
        val buttonCamera: ImageButton = findViewById(R.id.buttonCamera)
        buttonCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }
        }

        // Initialize Config Section (Point Rate and Shop ID)
        val pointRateTextView: TextView = findViewById(R.id.pointRate)
        val shopIDTextView: TextView = findViewById(R.id.shopID)

        val pointRateEditText = EditText(this).apply {
            visibility = View.GONE
            setText(pointRateTextView.text) // Ganti text = pointRateTextView.text dengan setText()
        }
        val shopIDEditText = EditText(this).apply {
            visibility = View.GONE
            setText(shopIDTextView.text) // Ganti text = shopIDTextView.text dengan setText()
        }

        // Add EditTexts programmatically to layout
        (pointRateTextView.parent as ViewGroup).addView(pointRateEditText)
        (shopIDTextView.parent as ViewGroup).addView(shopIDEditText)

        val buttonEditConfig: ImageButton = findViewById(R.id.buttonEditConfig)
        buttonEditConfig.setOnClickListener {
            if (isEditMode) {
                // Jika dalam mode edit, simpan perubahan dan kembali ke mode tampilan
                pointRateTextView.text = pointRateEditText.text
                shopIDTextView.text = shopIDEditText.text

                // Tampilkan TextView dan sembunyikan EditText
                pointRateTextView.visibility = View.VISIBLE
                shopIDTextView.visibility = View.VISIBLE
                pointRateEditText.visibility = View.GONE
                shopIDEditText.visibility = View.GONE

                // Ubah ikon menjadi ikon edit (jika ada ikon berbeda untuk mode edit dan simpan)
                buttonEditConfig.setImageResource(R.drawable.ic_edit) // ganti dengan ikon edit

                // Nonaktifkan mode edit
                isEditMode = false
                Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show()
            } else {
                // Jika tidak dalam mode edit, masuk ke mode edit

                // Salin teks dari TextView ke EditText
                pointRateEditText.setText(pointRateTextView.text)
                shopIDEditText.setText(shopIDTextView.text)

                // Sembunyikan TextView dan tampilkan EditText
                pointRateTextView.visibility = View.GONE
                shopIDTextView.visibility = View.GONE
                pointRateEditText.visibility = View.VISIBLE
                shopIDEditText.visibility = View.VISIBLE

                // Ubah ikon menjadi ikon simpan (jika ada ikon berbeda untuk mode edit dan simpan)
                buttonEditConfig.setImageResource(R.drawable.is_save) // ganti dengan ikon simpan

                // Aktifkan mode edit
                isEditMode = true
                Toast.makeText(this, "Edit mode enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data")
            Toast.makeText(this, "Picture taken successfully", Toast.LENGTH_SHORT).show()
            // You may set the bitmap to the shopLogo ImageView if needed
            // shopLogo.setImageBitmap(imageBitmap as Bitmap)
        }
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 1001
    }
}
