package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var editProfileButton: Button
    private lateinit var signOutButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi tombol Edit Profile
        editProfileButton = view.findViewById(R.id.buttonEditProfile)
        signOutButton = view.findViewById(R.id.buttonSignOut)

        // Gunakan Intent untuk berpindah ke EditProfileActivity
        editProfileButton.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        // Implementasi Sign Out
        signOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            // Arahkan ke halaman login setelah Sign Out
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(requireContext(), "Signed Out", Toast.LENGTH_SHORT).show()
        }
    }
}
