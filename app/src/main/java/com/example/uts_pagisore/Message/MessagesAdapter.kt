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
        private val shopNameTextView: TextView = view.findViewById(R.id.textShopName)
        private val titleTextView: TextView = view.findViewById(R.id.textTitle)
        private val descriptionTextView: TextView = view.findViewById(R.id.textDescription)
        private val timeTextView: TextView = view.findViewById(R.id.textTime)

        fun bind(message: Message, onItemClick: (Message) -> Unit) {
            shopNameTextView.text = message.shopName
            titleTextView.text = message.title
            descriptionTextView.text = message.description
            timeTextView.text = message.time
            shopIconImageView.setImageResource(R.drawable.profilikon)
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
