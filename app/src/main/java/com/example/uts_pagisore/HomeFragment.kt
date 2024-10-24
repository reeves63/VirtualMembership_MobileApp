package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MembershipAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewMemberships)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inisialisasi tombol MyShop dengan tipe MaterialButton
        val buttonMyShop: MaterialButton = view.findViewById(R.id.buttonMyShop)

        // Navigasi ke MyShopActivity menggunakan Intent
        buttonMyShop.setOnClickListener {
            val intent = Intent(requireContext(), MyShopActivity::class.java)
            startActivity(intent)
        }

        val buttonMembership: MaterialButton = view.findViewById(R.id.buttonMembership)

        buttonMembership.setOnClickListener {
            val intent = Intent(requireContext(), MembershipActivity::class.java)
            startActivity(intent)
        }

        // Dummy data untuk adapter
        val memberships = listOf(
            Membership("FAZ CAFE & BALLS", "2022/08/13", R.drawable.logo_faz_cafe),
            Membership("Syahri's Wash", "2022/07/23", R.drawable.logo_syahris_wash),
            Membership("Frost Gaming x Pool", "2022/07/13", R.drawable.logo_frost_gaming)
        )

        adapter = MembershipAdapter(memberships)
        recyclerView.adapter = adapter
    }
}