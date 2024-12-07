package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uts_pagisore.Membership.Membership
import com.example.uts_pagisore.Membership.MembershipActivity
import com.example.uts_pagisore.Membership.MembershipAdapter
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {
    private lateinit var membershipRecyclerView: RecyclerView
    private lateinit var noMembershipText: TextView
    private lateinit var loadingProgressBar: ProgressBar
    private val memberships = mutableListOf<Membership>()
    private lateinit var adapter: MembershipAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        membershipRecyclerView = view.findViewById(R.id.recyclerViewMemberships)
        noMembershipText = view.findViewById(R.id.textNoMembership)
        loadingProgressBar = view.findViewById(R.id.progressBar)

        membershipRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MembershipAdapter(memberships) { shopId ->
            val intent = Intent(requireContext(), UserShopDetailActivity::class.java)
            intent.putExtra("SHOP_ID", shopId)
            startActivity(intent)
        }
        membershipRecyclerView.adapter = adapter

        // Setup buttons
        setupButtons(view)

        // Load memberships
        loadMembershipShops()

        return view
    }

    private fun setupButtons(view: View) {
        // My Shop button
        val buttonMyShop = view.findViewById<MaterialButton>(R.id.buttonMyShop)
        buttonMyShop.setOnClickListener {
            checkUserShops()
        }

        // Membership button
        val buttonMembership = view.findViewById<Button>(R.id.buttonMembership)
        buttonMembership.setOnClickListener {
            startActivity(Intent(requireContext(), MembershipActivity::class.java))
        }
    }

    private fun loadMembershipShops() {
        val userId = auth.currentUser?.uid ?: return
        showLoading(true)

        // Listen to user's membership shops
        db.collection("users").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    showError("Error loading memberships: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val membershipShopIds = snapshot.get("membershipShops") as? List<String> ?: emptyList()
                    if (membershipShopIds.isEmpty()) {
                        showEmptyState()
                        return@addSnapshotListener
                    }

                    // Load shop details and membership points
                    loadShopDetailsWithPoints(membershipShopIds, userId)
                } else {
                    showEmptyState()
                }
            }
    }

    private fun loadShopDetailsWithPoints(shopIds: List<String>, userId: String) {
        db.collection("shops")
            .whereIn(FieldPath.documentId(), shopIds)
            .get()
            .addOnSuccessListener { documents ->
                memberships.clear()
                var loadedCount = 0

                for (document in documents) {
                    val shopId = document.id
                    val name = document.getString("name") ?: "Unknown Shop"
                    val logoUrl = document.getString("profileImageUrl") ?: ""

                    // Get membership points
                    db.collection("shops")
                        .document(shopId)
                        .collection("memberships")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { membershipDoc ->
                            loadedCount++
                            val points = membershipDoc.getLong("points")?.toInt() ?: 0
                            memberships.add(Membership(
                                name = name,
                                points = points,
                                logoUrl = logoUrl,
                                shopId = shopId
                            ))

                            // Update UI when all memberships are loaded
                            if (loadedCount == documents.size()) {
                                updateUI()
                            }
                        }
                        .addOnFailureListener { e ->
                            loadedCount++
                            showError("Error loading points: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                showError("Error loading shops: ${e.message}")
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
                        startActivity(Intent(requireContext(), RegisterMyShopActivity::class.java))
                    } else {
                        startActivity(Intent(requireContext(), MyShopListActivity::class.java))
                    }
                }
                .addOnFailureListener { e ->
                    showError("Error checking shops: ${e.message}")
                }
        }
    }

    private fun updateUI() {
        showLoading(false)
        if (memberships.isEmpty()) {
            showEmptyState()
        } else {
            showMemberships()
        }
        adapter.notifyDataSetChanged()
    }

    private fun showLoading(show: Boolean) {
        loadingProgressBar.visibility = if (show) View.VISIBLE else View.GONE
        membershipRecyclerView.visibility = if (show) View.GONE else View.VISIBLE
        noMembershipText.visibility = View.GONE
    }

    private fun showEmptyState() {
        showLoading(false)
        membershipRecyclerView.visibility = View.GONE
        noMembershipText.visibility = View.VISIBLE
    }

    private fun showMemberships() {
        membershipRecyclerView.visibility = View.VISIBLE
        noMembershipText.visibility = View.GONE
    }

    private fun showError(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}