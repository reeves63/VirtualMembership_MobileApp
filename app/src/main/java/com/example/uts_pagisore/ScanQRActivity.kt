package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.uts_pagisore.databinding.ActivityScanQrBinding
import com.google.firebase.firestore.FieldValue
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
                    "MANAGE_POINTS" -> showManagePointsDialog(scannedUserId) // Panggil dialog input poin
                }
            } else {
                Toast.makeText(this, "Scan cancelled.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMembership(userId: String) {
        val shopId = shopId ?: return

        // 1. Tambahkan membership ke koleksi memberships shop
        val membershipRef = db.collection("shops").document(shopId).collection("memberships")

        // 2. Secara bersamaan, tambahkan shopId ke array membershipShops milik user
        val userRef = db.collection("users").document(userId)

        // Mulai transaksi batch untuk memastikan kedua operasi berhasil
        db.runBatch { batch ->
            // Tambah membership baru ke shop
            batch.set(membershipRef.document(userId), hashMapOf(
                "points" to 0,
                "joinedDate" to System.currentTimeMillis(),
                "status" to "active"
            ))

            // Update array membershipShops di dokumen user
            batch.update(userRef, "membershipShops", FieldValue.arrayUnion(shopId))
        }.addOnSuccessListener {
            Toast.makeText(this, "Membership berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Gagal menambahkan membership: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showManagePointsDialog(userId: String) {
        // Buat AlertDialog untuk memasukkan poin
        val builder = AlertDialog.Builder(this)
        val editText = EditText(this)
        editText.hint = "(positif untuk tambah, negatif untuk kurangi)"
        editText.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
        builder.setTitle("Masukkan Poin")
        builder.setView(editText)

        builder.setPositiveButton("OK") { dialog, _ ->
            val pointsInput = editText.text.toString()

            // Validasi input poin
            val pointsDelta = pointsInput.toIntOrNull()
            if (pointsDelta == null) {
                Toast.makeText(this, "Input poin tidak valid", Toast.LENGTH_SHORT).show()
            } else {
                managePoints(userId, pointsDelta)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun managePoints(userId: String, pointsDelta: Int) {
        val membershipRef = db.collection("shops").document(shopId!!).collection("memberships").document(userId)

        membershipRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentPoints = document.getLong("points")?.toInt() ?: 0
                    if (currentPoints + pointsDelta < 0) {
                        // Jika poin tidak cukup
                        Toast.makeText(this, "Poin tidak cukup", Toast.LENGTH_SHORT).show()
                    } else {
                        val newPoints = currentPoints + pointsDelta
                        membershipRef.update("points", newPoints)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Poin berhasil diperbarui.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Gagal memperbarui poin.", Toast.LENGTH_SHORT).show()
                            }
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
