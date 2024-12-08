package com.example.uts_pagisore.Message

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uts_pagisore.R
import com.google.firebase.firestore.FirebaseFirestore

class CreateMessageActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var btnSendMessage: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_message)

        editTextTitle = findViewById(R.id.et_message_title)
        editTextDescription = findViewById(R.id.et_message_description)
        btnSendMessage = findViewById(R.id.btn_send_message)

        btnSendMessage.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                saveMessageToFirestore(title, description)
            } else {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveMessageToFirestore(title: String, description: String) {
        val message = hashMapOf(
            "title" to title,
            "description" to description,
            "time" to System.currentTimeMillis()
        )

        // Save the message to Firestore
        db.collection("messages")
            .add(message)
            .addOnSuccessListener {
                Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show()
                finish()  // Close the activity and return to the previous screen
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
            }
    }
}
