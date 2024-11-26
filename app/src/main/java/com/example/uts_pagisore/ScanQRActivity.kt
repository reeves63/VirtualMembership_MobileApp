package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uts_pagisore.databinding.ActivityScanQrBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class ScanQRActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanQrBinding
    private val db = FirebaseFirestore.getInstance()
    private var shopId: String? = null
    private var scanMode: String = "ADD_MEMBERSHIP" // Default mode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil shopId dari Intent
        shopId = intent.getStringExtra("SHOP_ID")

        if (shopId == null) {
            Toast.makeText(this, "Shop ID not found!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Pilihan Mode
        binding.radioAddMembership.setOnClickListener {
            scanMode = "ADD_MEMBERSHIP"
        }
        binding.radioManagePoints.setOnClickListener {
            scanMode = "MANAGE_POINTS"
        }

        // Tombol Mulai Scan QR
        binding.buttonScanQr.setOnClickListener {
            startQrScanner()
        }

        // Tombol Back
        binding.buttonBack.setOnClickListener {
            finish()
        }
    }

    private fun startQrScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Arahkan kamera ke QR Code")
        integrator.setBeepEnabled(true)
        integrator.setOrientationLocked(true) // Kunci orientasi ke portrait
        integrator.setCaptureActivity(CustomCaptureActivity::class.java) // Tambahkan custom capture
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val scannedUserId = result.contents // Data dari QR Code

                when (scanMode) {
                    "ADD_MEMBERSHIP" -> addMembership(scannedUserId)
                    "MANAGE_POINTS" -> managePoints(scannedUserId, 100) // Contoh: Tambah 100 poin
                }
            } else {
                Toast.makeText(this, "Scan cancelled.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMembership(userId: String) {
        val membershipRef = db.collection("shops").document(shopId!!).collection("memberships")

        membershipRef.document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Toast.makeText(this, "User sudah menjadi membership.", Toast.LENGTH_SHORT).show()
                    finish() // Kembali ke Shop Detail
                } else {
                    val newMembership = hashMapOf("points" to 0)
                    membershipRef.document(userId).set(newMembership)
                        .addOnSuccessListener {
                            addShopToUserMembership(userId)
                            Toast.makeText(this, "User berhasil ditambahkan sebagai membership.", Toast.LENGTH_SHORT).show()
                            finish() // Kembali ke Shop Detail
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Gagal menambahkan user sebagai membership.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memeriksa membership.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addShopToUserMembership(userId: String) {
        val userRef = db.collection("users").document(userId)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val memberships = document.get("membershipShops") as? ArrayList<String> ?: arrayListOf()
                    if (!memberships.contains(shopId)) {
                        memberships.add(shopId!!)
                        userRef.update("membershipShops", memberships)
                    }
                } else {
                    val newUserData = hashMapOf(
                        "membershipShops" to listOf(shopId)
                    )
                    userRef.set(newUserData)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menambahkan toko ke user.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun managePoints(userId: String, pointsDelta: Int) {
        val membershipRef = db.collection("shops").document(shopId!!).collection("memberships").document(userId)

        membershipRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentPoints = document.getLong("points")?.toInt() ?: 0
                    val newPoints = currentPoints + pointsDelta

                    membershipRef.update("points", newPoints)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Poin berhasil diperbarui.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Gagal memperbarui poin.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "User belum menjadi membership.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memeriksa poin user.", Toast.LENGTH_SHORT).show()
            }
    }
}
