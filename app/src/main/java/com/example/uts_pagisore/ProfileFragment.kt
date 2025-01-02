package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.uts_pagisore.Auth.LoginActivity
import com.example.uts_pagisore.Profile.EditProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var editProfileButton: Button
    private lateinit var signOutButton: Button
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileGender: TextView
    private lateinit var profilePhone: TextView
    private lateinit var profileDOB: TextView
    private lateinit var profileImageView: ImageView

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editProfileButton = view.findViewById(R.id.buttonEditProfile)
        signOutButton = view.findViewById(R.id.buttonSignOut)
        profileName = view.findViewById(R.id.profileName)
        profileEmail = view.findViewById(R.id.profileEmail)
        profileGender = view.findViewById(R.id.profileGender)
        profilePhone = view.findViewById(R.id.profilePhone)
        profileDOB = view.findViewById(R.id.profileDOB)
        profileImageView = view.findViewById(R.id.profilePhoto)

        editProfileButton.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        signOutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfileData()
    }

    private fun loadProfileData() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        profileEmail.text = document.getString("email")
                        profileName.text = document.getString("fullName") ?: "Add Name"
                        profileGender.text = document.getString("gender") ?: "Add Gender"
                        profilePhone.text = document.getString("phoneNumber") ?: "Add Phone"
                        profileDOB.text = document.getString("dob") ?: "Add DOB"

                        val profileImageUrl = document.getString("profileImageUrl")
                        if (!profileImageUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.profile_picture_placeholder)
                                .into(profileImageView)
                        }
                    } else {
                        profileEmail.text = auth.currentUser?.email
                        profileName.text = "Add Name"
                        profileGender.text = "Add Gender"
                        profilePhone.text = "Add Phone"
                        profileDOB.text = "Add DOB"
                    }
                }
                .addOnFailureListener {
                    profileEmail.text = auth.currentUser?.email
                    profileName.text = "Error Loading Name"
                    profileGender.text = "Error Loading Gender"
                    profilePhone.text = "Error Loading Phone"
                    profileDOB.text = "Error Loading DOB"
                }
        }
    }
}
