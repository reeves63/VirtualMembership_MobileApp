package com.example.uts_pagisore.Message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uts_pagisore.R

class MessagesAdapter(
    private val messages: List<Message>,
    private val clickListener: (Message) -> Unit
) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageIcon: ImageView = view.findViewById(R.id.img_message_icon)
        val messageTitle: TextView = view.findViewById(R.id.tv_message_title)
        val messageDescription: TextView = view.findViewById(R.id.tv_message_description)
        val messageTime: TextView = view.findViewById(R.id.tv_message_time)

        fun bind(message: Message, clickListener: (Message) -> Unit) {
            messageTitle.text = message.title
            messageDescription.text = message.description
            messageTime.text = message.time

            // Optional: Set an icon based on the message type or other logic
            messageIcon.setImageResource(R.drawable.bird_icon) // Replace as needed

            itemView.setOnClickListener { clickListener(message) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_messages, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position], clickListener)
    }

    override fun getItemCount(): Int = messages.size
}

