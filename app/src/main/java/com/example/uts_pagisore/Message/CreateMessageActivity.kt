package com.example.uts_pagisore.Message

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uts_pagisore.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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

        // Run operation in background thread
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // First, get all members of this shop
                val memberships = db.collection("shops")
                    .document(shopId)
                    .collection("memberships")
                    .get()
                    .await()

                if (memberships.isEmpty) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CreateMessageActivity, "No members found for this shop", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Format time to be saved as timestamp
                val currentTime = System.currentTimeMillis()

                val message = hashMapOf(
                    "title" to title,
                    "description" to description,
                    "time" to currentTime, // Timestamp format
                    "shopId" to shopId
                )

                // Add message to messages collection
                val messageRef = db.collection("messages").add(message).await()

                // For each member, create reference to this message
                for (membership in memberships) {
                    val userId = membership.id

                    // Add message reference to user's messages
                    db.collection("users")
                        .document(userId)
                        .collection("receivedMessages")
                        .document(messageRef.id)
                        .set(message)
                        .await()
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateMessageActivity, "Message sent successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateMessageActivity, "Error sending message: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
