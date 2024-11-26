package com.example.uts_pagisore.Membership

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uts_pagisore.R

class MembershipAdapter(private var memberships: List<Membership>) :
    RecyclerView.Adapter<MembershipAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewLogo: ImageView = view.findViewById(R.id.imageViewLogo)
        val textViewName: TextView = view.findViewById(R.id.textViewName)
        val textViewPoints: TextView = view.findViewById(R.id.textViewPoints)

        fun bind(membership: Membership) {
            // Load logo from URL with Glide
            Glide.with(itemView.context)
                .load(membership.logoUrl)
                .placeholder(R.drawable.profile_picture_placeholder) // Default placeholder
                .error(R.drawable.profile_picture_placeholder) // Error placeholder
                .into(imageViewLogo)

            // Set name and points
            textViewName.text = membership.name.ifEmpty { "Unknown Shop" }
            textViewPoints.text = "Points: ${membership.points}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_membership, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(memberships[position])
    }

    override fun getItemCount() = memberships.size

    // Method to update data efficiently
    fun updateMemberships(newMemberships: List<Membership>) {
        if (memberships != newMemberships) {
            memberships = newMemberships
            notifyDataSetChanged()
        }
    }
}

// Data model for Membership
data class Membership(
    val name: String = "",
    val points: Int = 0,
    val logoUrl: String = "" // URL for shop logo
)
