package com.example.uts_pagisore.Message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uts_pagisore.ItemMessageBinding
import com.example.uts_pagisore.R

class MessagesAdapter(
    private var messages: List<Message>,
    private val onItemClick: (Message) -> Unit
) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message, onItemClick: (Message) -> Unit) {
            binding.textTitle.text = message.title
            binding.textDescription.text = message.description
            binding.textTime.text = message.time
            binding.root.setOnClickListener { onItemClick(message) }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemMessageBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
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