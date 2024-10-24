package com.example.uts_pagisore

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MembershipActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership)

        // Inisialisasi RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewMembership)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Dummy data untuk adapter
        val memberships = listOf(
            Membership("FAZ CAFE & BALLS", "2022/08/13", R.drawable.logo_faz_cafe),
            Membership("Syahri's Wash", "2022/07/23", R.drawable.logo_syahris_wash),
            Membership("Frost Gaming x Pool", "2022/07/13", R.drawable.logo_frost_gaming)
        )

        val adapter = MembershipAdapter(memberships)
        recyclerView.adapter = adapter

        // Tombol untuk kembali ke halaman sebelumnya
        val buttonBack: ImageButton = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish() // Menutup activity dan kembali ke halaman sebelumnya
        }
    }
}