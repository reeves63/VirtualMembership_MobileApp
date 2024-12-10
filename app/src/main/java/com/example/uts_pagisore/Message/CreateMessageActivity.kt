package com.example.uts_pagisore.Message

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uts_pagisore.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateMessageActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var btnSendMessage: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var shopId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_message)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get shopId from intent
        shopId = intent.getStringExtra("SHOP_ID")

        if (shopId == null) {
            Toast.makeText(this, "Shop ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize views
        editTextTitle = findViewById(R.id.et_message_title)
        editTextDescription = findViewById(R.id.et_message_description)
        btnSendMessage = findViewById(R.id.btn_send_message)

        btnSendMessage.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                sendMessageToMembers(title, description)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendMessageToMembers(title: String, description: String) {
        val shopId = this.shopId ?: return

        // First, get all members of this shop
        db.collection("shops")
            .document(shopId)
            .collection("memberships")
            .get()
            .addOnSuccessListener { memberships ->
                val message = hashMapOf(
                    "title" to title,
                    "description" to description,
                    "time" to System.currentTimeMillis(),
                    "shopId" to shopId
                )

                // Add message to messages collection
                db.collection("messages")
                    .add(message)
                    .addOnSuccessListener { messageRef ->
                        // For each member, create reference to this message
                        for (membership in memberships) {
                            val userId = membership.id

                            // Add message reference to user's messages
                            db.collection("users")
                                .document(userId)
                                .collection("receivedMessages")
                                .document(messageRef.id)
                                .set(message)
                        }

                        Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error sending message: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error getting members: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}