package com.example.uts_pagisore

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MessagesDetail : AppCompatActivity() {

    private lateinit var messageTitle: TextView
    private lateinit var messageDescription: TextView
    private lateinit var messageTime: TextView
    private lateinit var messageContent: TextView
    private lateinit var messageIcon: ImageView
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_detail)

        // Initialize the views
        messageTitle = findViewById(R.id.tv_message_title)
        messageDescription = findViewById(R.id.tv_message_description)
        messageTime = findViewById(R.id.tv_message_time)
        messageContent = findViewById(R.id.tv_message_content)
        messageIcon = findViewById(R.id.img_message_icon)
        backButton = findViewById(R.id.btn_back)

        // Set up the back button
        backButton.setOnClickListener {
            finish() // Close the activity and return to the previous screen
        }

        // Receive the data passed from MessagesFragment
        messageTitle.text = intent.getStringExtra("MESSAGE_TITLE")
        messageDescription.text = intent.getStringExtra("MESSAGE_DESCRIPTION")
        messageTime.text = intent.getStringExtra("MESSAGE_TIME")
        messageContent.text = intent.getStringExtra("MESSAGE_CONTENT")

        // Optionally, if messageIcon is passed through intent as a drawable ID:
        val iconResId = intent.getIntExtra("MESSAGE_ICON", R.drawable.bird_icon)
        messageIcon.setImageResource(iconResId)
    }
}
