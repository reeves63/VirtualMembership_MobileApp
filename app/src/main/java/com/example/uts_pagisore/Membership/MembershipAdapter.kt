package com.example.uts_pagisore.Membership

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uts_pagisore.R

class MembershipAdapter(
    private var memberships: List<Membership>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<MembershipAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewLogo: ImageView = view.findViewById(R.id.imageViewLogo)
        val textViewName: TextView = view.findViewById(R.id.textViewName)
        val textViewPoints: TextView = view.findViewById(R.id.textViewPoints)

        fun bind(membership: Membership, onItemClick: (String) -> Unit) {
            textViewName.text = membership.name.ifEmpty { "Unknown Shop" }
            textViewPoints.text = "Points: ${membership.points}"

            Glide.with(itemView.context)
                .load(membership.logoUrl)
                .placeholder(R.drawable.profile_picture_placeholder)
                .error(R.drawable.profile_picture_placeholder)
                .into(imageViewLogo)

            itemView.setOnClickListener {
                onItemClick(membership.shopId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_membership, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(memberships[position], onItemClick)
    }

    override fun getItemCount() = memberships.size

    fun updateMemberships(newMemberships: List<Membership>) {
        memberships = newMemberships
        notifyDataSetChanged()
    }
}