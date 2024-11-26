package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uts_pagisore.Membership.Membership
import com.example.uts_pagisore.Membership.MembershipActivity
import com.example.uts_pagisore.Membership.MembershipAdapter
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var membershipRecyclerView: RecyclerView
    private val memberships = mutableListOf<Membership>()
    private lateinit var adapter: MembershipAdapter

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi RecyclerView untuk daftar membership
        membershipRecyclerView = view.findViewById(R.id.recyclerViewMemberships)
        membershipRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MembershipAdapter(memberships)
        membershipRecyclerView.adapter = adapter

        // Tombol "My Shop"
        val buttonMyShop = view.findViewById<MaterialButton>(R.id.buttonMyShop)
        buttonMyShop.setOnClickListener {
            checkUserShops()
        }

        // Tombol "Membership"
        val buttonMembership = view.findViewById<Button>(R.id.buttonMembership)
        buttonMembership.setOnClickListener {
            val intent = Intent(requireContext(), MembershipActivity::class.java)
            startActivity(intent)
        }

        // Muat daftar membership
        loadMembershipShops()

        return view
    }

    private fun loadMembershipShops() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val membershipShopIds = snapshot.get("membershipShops") as? List<String> ?: emptyList()

                if (membershipShopIds.isEmpty()) {
                    memberships.clear()
                    adapter.notifyDataSetChanged()
                    return@addSnapshotListener
                }

                db.collection("shops")
                    .whereIn("id", membershipShopIds)
                    .get()
                    .addOnSuccessListener { documents ->
                        memberships.clear()
                        for (document in documents) {
                            val name = document.getString("name") ?: "Unknown Shop"
                            val points = document.getLong("points")?.toInt() ?: 0
                            val logoUrl = document.getString("profileImageUrl") ?: ""
                            memberships.add(Membership(name, points, logoUrl))
                        }
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        // Handle error saat mengambil data toko
                    }
            }
        }
    }

    private fun checkUserShops() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("shops")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        val intent = Intent(requireContext(), RegisterMyShopActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(requireContext(), MyShopListActivity::class.java)
                        startActivity(intent)
                    }
                }
                .addOnFailureListener {
                    // Handle error Firebase (misalnya menambahkan log atau toast)
                }
        }
    }
}
