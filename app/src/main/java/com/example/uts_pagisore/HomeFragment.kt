package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.uts_pagisore.MembershipActivity
import com.example.uts_pagisore.R
import com.example.uts_pagisore.RegisterMyShopActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
// Inisialisasi tombol MyShop
        val buttonMyShop =
            view.findViewById<MaterialButton>(R.id.buttonMyShop)
// Ketika tombol MyShop diklik
        buttonMyShop.setOnClickListener {
            checkUserShops()
        }
// Kembali menambahkan buttonMembership
        val buttonMembership: MaterialButton =
            view.findViewById(R.id.buttonMembership)
// Ketika tombol Membership diklik, alihkan ke MembershipActivity
        buttonMembership.setOnClickListener {
            val intent = Intent(requireContext(),
                MembershipActivity::class.java)
            startActivity(intent)
        }
        return view
    }
    private fun checkUserShops() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        if (userId != null) {
            db.collection("shops")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
// Jika user belum memiliki toko, alihkan ke halaman registrasi toko
                        val intent = Intent(requireContext(),
                            RegisterMyShopActivity::class.java)
                        startActivity(intent)
                    } else {
// Jika user sudah memiliki toko, alihkan ke halaman daftar toko
                        val intent = Intent(requireContext(),
                            MyShopListActivity::class.java)
                        startActivity(intent)
                    }
                }
                .addOnFailureListener { exception ->
// Handle error Firebase (misalnya menambahkan log atau toast)
                }
        } else {
// Jika user belum login (mungkin bisa arahkan ke halaman login)
        }
    }
}