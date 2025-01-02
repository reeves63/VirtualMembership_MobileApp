package com.example.uts_pagisore.Membership

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uts_pagisore.R
import com.example.uts_pagisore.UserShopDetailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MembershipActivity : AppCompatActivity() {
    private lateinit var adapter: MembershipAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var noMembershipText: TextView
    private val allMemberships = mutableListOf<Membership>()
    private val displayedMemberships = mutableListOf<Membership>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership)

        setupViews()
        setupRecyclerView()
        loadMembershipData()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerViewMembership)
        progressBar = findViewById(R.id.progressBar)
        noMembershipText = findViewById(R.id.textNoMembership)

        findViewById<ImageButton>(R.id.buttonBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.buttonShowMore).setOnClickListener {
            displayAllMemberships()
        }
    }

    private fun setupRecyclerView() {
        adapter = MembershipAdapter(displayedMemberships) { shopId ->
            val intent = Intent(this, UserShopDetailActivity::class.java)
            intent.putExtra("SHOP_ID", shopId)
            startActivity(intent)
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MembershipActivity)
            adapter = this@MembershipActivity.adapter
        }
    }

    private fun loadMembershipData() {
        val userId = auth.currentUser?.uid ?: return
        showLoading(true)

        db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    showError("Failed to load memberships: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val shopIds = snapshot.get("membershipShops") as? List<String> ?: emptyList()
                    if (shopIds.isEmpty()) {
                        showEmptyState()
                        return@addSnapshotListener
                    }

                    loadDetailedMembershipData(shopIds, userId)
                } else {
                    showEmptyState()
                }
            }
    }

    private fun loadDetailedMembershipData(shopIds: List<String>, userId: String) {
        db.collection("shops")
            .whereIn(FieldPath.documentId(), shopIds)
            .orderBy("name")
            .get()
            .addOnSuccessListener { documents ->
                allMemberships.clear()
                var loadedCount = 0

                for (document in documents) {
                    val shopId = document.id
                    val name = document.getString("name") ?: "Unknown Shop"
                    val logoUrl = document.getString("profileImageUrl") ?: ""

                    db.collection("shops")
                        .document(shopId)
                        .collection("memberships")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { membershipDoc ->
                            loadedCount++
                            val points = membershipDoc.getLong("points")?.toInt() ?: 0
                            allMemberships.add(
                                Membership(
                                    name = name,
                                    points = points,
                                    logoUrl = logoUrl,
                                    shopId = shopId
                                )
                            )

                            if (loadedCount == documents.size()) {
                                allMemberships.sortBy { it.name }
                                updateDisplayedMemberships()
                            }
                        }
                        .addOnFailureListener { e ->
                            loadedCount++
                            showError("Error loading membership details: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                showError("Error loading shops: ${e.message}")
            }
    }



    private fun updateDisplayedMemberships() {
        showLoading(false)
        if (allMemberships.isEmpty()) {
            showEmptyState()
            return
        }

        displayedMemberships.clear()
        val initialDisplay = 5
        displayedMemberships.addAll(allMemberships.take(initialDisplay))

        findViewById<Button>(R.id.buttonShowMore).visibility =
            if (allMemberships.size > initialDisplay) View.VISIBLE else View.GONE

        adapter.updateMemberships(displayedMemberships)
        showMemberships()
    }

    private fun displayAllMemberships() {
        displayedMemberships.clear()
        displayedMemberships.addAll(allMemberships)
        adapter.updateMemberships(displayedMemberships)
        findViewById<Button>(R.id.buttonShowMore).visibility = View.GONE
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
        noMembershipText.visibility = View.GONE
    }

    private fun showEmptyState() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        noMembershipText.visibility = View.VISIBLE
    }

    private fun showMemberships() {
        recyclerView.visibility = View.VISIBLE
        noMembershipText.visibility = View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}