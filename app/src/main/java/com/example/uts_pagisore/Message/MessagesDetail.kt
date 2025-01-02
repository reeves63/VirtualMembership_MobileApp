package com.example.uts_pagisore.Message

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.uts_pagisore.R

class MessagesDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_detail)

        val shopNameTextView: TextView = findViewById(R.id.tv_shop_name)
        val messageTitleTextView: TextView = findViewById(R.id.tv_message_title)
        val messageDescriptionTextView: TextView = findViewById(R.id.tv_message_description)
        val messageTimeTextView: TextView = findViewById(R.id.tv_message_time)
        val backButton: ImageButton = findViewById(R.id.btn_back)

        backButton.setOnClickListener { finish() }

        intent.extras?.let { bundle ->
            shopNameTextView.text = bundle.getString("SHOP_NAME", "")
            messageTitleTextView.text = bundle.getString("MESSAGE_TITLE", "")
            messageDescriptionTextView.text = bundle.getString("MESSAGE_DESCRIPTION", "")
            messageTimeTextView.text = bundle.getString("MESSAGE_TIME", "")
        }
    }
}
