package com.example.uts_pagisore.Membership

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uts_pagisore.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MembershipActivity : AppCompatActivity() {

    private lateinit var adapter: MembershipAdapter
    private val allMemberships = mutableListOf<Membership>()
    private val displayedMemberships = mutableListOf<Membership>() // Data yang ditampilkan
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership)

        // Inisialisasi RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewMembership)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MembershipAdapter(displayedMemberships)
        recyclerView.adapter = adapter

        // Tombol untuk kembali ke halaman sebelumnya
        val buttonBack: ImageButton = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish() // Menutup activity dan kembali ke halaman sebelumnya
        }

        // Tombol Show More
        val buttonShowMore: Button = findViewById(R.id.buttonShowMore)
        buttonShowMore.setOnClickListener {
            displayAllMemberships()
            buttonShowMore.visibility = View.GONE // Sembunyikan tombol setelah semua data ditampilkan
        }

        // Load data membership toko
        loadMembershipData(buttonShowMore)
    }

    private fun loadMembershipData(buttonShowMore: Button) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User belum login.", Toast.LENGTH_SHORT).show()
            return
        }

        // Ambil data membershipShops dari Firestore
        db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Gagal memuat data membership.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val shopIds = snapshot.get("membershipShops") as? List<String> ?: emptyList()

                    if (shopIds.isEmpty()) {
                        allMemberships.clear()
                        displayedMemberships.clear()
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this, "Belum ada membership yang terdaftar.", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }

                    // Ambil data toko berdasarkan shopIds
                    db.collection("shops")
                        .whereIn("id", shopIds)
                        .get()
                        .addOnSuccessListener { documents ->
                            allMemberships.clear()
                            for (document in documents) {
                                val name = document.getString("name") ?: "Toko Tidak Dikenal"
                                val points = document.getLong("points")?.toInt() ?: 0
                                val logoUrl = document.getString("profileImageUrl") ?: ""
                                allMemberships.add(Membership(name, points, logoUrl))
                            }

                            updateDisplayedMemberships(buttonShowMore)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Gagal memuat data toko.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }

    private fun updateDisplayedMemberships(buttonShowMore: Button) {
        displayedMemberships.clear()

        // Tampilkan maksimal 5 membership terlebih dahulu
        val maxDisplayCount = 5
        displayedMemberships.addAll(allMemberships.take(maxDisplayCount))
        adapter.notifyDataSetChanged()

        // Tampilkan tombol Show More jika ada lebih dari 5 membership
        if (allMemberships.size > maxDisplayCount) {
            buttonShowMore.visibility = View.VISIBLE
        } else {
            buttonShowMore.visibility = View.GONE
        }
    }

    private fun displayAllMemberships() {
        // Tampilkan seluruh membership
        displayedMemberships.clear()
        displayedMemberships.addAll(allMemberships)
        adapter.notifyDataSetChanged()
    }
}
