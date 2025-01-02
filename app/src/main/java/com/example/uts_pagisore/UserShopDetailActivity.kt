package com.example.uts_pagisore

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.uts_pagisore.databinding.ActivityUserShopDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserShopDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserShopDetailBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var shopId: String? = null
    private var isMember = false
    private var points = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserShopDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shopId = intent.getStringExtra("SHOP_ID")
        if (shopId == null) {
            Toast.makeText(this, "Shop not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupClickListeners()
        loadShopDetails()
        checkMembershipStatus()
    }

    private fun setupClickListeners() {
        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.buttonJoinMembership.setOnClickListener {
            handleMembershipAction()
        }
    }

    private fun loadShopDetails() {
        shopId?.let { id ->
            db.collection("shops").document(id)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        with(binding) {
                            textShopName.text = document.getString("name")
                            textDescription.text = document.getString("description")
                            textLocation.text = document.getString("location")
                            textCategories.text = document.getString("categories")

                            val rate = document.getDouble("pointConversionRate") ?: 0.0
                            textPointValue.text = "1 Point = Rp ${rate.toInt()}"

                            val imageUrl = document.getString("profileImageUrl")
                            if (!imageUrl.isNullOrEmpty()) {
                                Glide.with(this@UserShopDetailActivity)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.profile_picture_placeholder)
                                    .into(imageShop)
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load shop details", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun checkMembershipStatus() {
        val userId = auth.currentUser?.uid
        if (userId != null && shopId != null) {
            db.collection("shops")
                .document(shopId!!)
                .collection("memberships")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    isMember = document.exists()
                    if (isMember) {
                        points = document.getLong("points")?.toInt() ?: 0
                    }
                    updateUI()
                }
        }
    }

    private fun updateUI() {
        with(binding) {
            if (isMember) {
                buttonJoinMembership.visibility = View.GONE
                layoutMemberInfo.visibility = View.VISIBLE
                textPoints.text = "Your Points: $points"
            } else {
                buttonJoinMembership.visibility = View.VISIBLE
                layoutMemberInfo.visibility = View.GONE
            }
        }
    }

    private fun handleMembershipAction() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        shopId?.let { id ->
            val membershipData = hashMapOf(
                "joinedDate" to System.currentTimeMillis(),
                "points" to 0,
                "status" to "active"
            )

            db.collection("shops").document(id)
                .collection("memberships")
                .document(userId)
                .set(membershipData)
                .addOnSuccessListener {
                    db.collection("users").document(userId)
                        .update("membershipShops", com.google.firebase.firestore.FieldValue.arrayUnion(id))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Successfully joined membership!", Toast.LENGTH_SHORT).show()
                            isMember = true
                            points = 0
                            updateUI()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update user data", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to join membership", Toast.LENGTH_SHORT).show()
                }
        }
    }
}