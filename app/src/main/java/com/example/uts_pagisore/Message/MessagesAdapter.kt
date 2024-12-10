package com.example.uts_pagisore.Message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uts_pagisore.R

class MessagesAdapter(
    private var messages: List<Message>,
    private val onItemClick: (Message) -> Unit
) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val shopIconImageView: ImageView = view.findViewById(R.id.img_shop_icon)
        private val shopNameTextView: TextView = view.findViewById(R.id.textShopName) // Pastikan ID ini benar
        private val titleTextView: TextView = view.findViewById(R.id.textTitle)
        private val descriptionTextView: TextView = view.findViewById(R.id.textDescription)
        private val timeTextView: TextView = view.findViewById(R.id.textTime)

        fun bind(message: Message, onItemClick: (Message) -> Unit) {
            // Set shop name
            shopNameTextView.text = message.shopName // Bind shopName

            // Set message title
            titleTextView.text = message.title

            // Set message description
            descriptionTextView.text = message.description

            // Set message time
            timeTextView.text = message.time

            // Set shop icon (you can customize this based on your requirements)
            shopIconImageView.setImageResource(R.drawable.profilikon)

            // Set click listener
            itemView.setOnClickListener { onItemClick(message) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_messages, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position], onItemClick)
    }

    override fun getItemCount() = messages.size

    fun updateMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}
