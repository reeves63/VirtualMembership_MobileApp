package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts_pagisore.Message.Message
import com.example.uts_pagisore.Message.MessagesAdapter
import com.example.uts_pagisore.Message.MessagesDetail
import com.example.uts_pagisore.databinding.FragmentMessagesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class MessagesFragment : Fragment() {
    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var adapter: MessagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadMessages()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        adapter = MessagesAdapter(emptyList()) { message ->
            openMessageDetail(message)
        }
        binding.recyclerViewMessages.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@MessagesFragment.adapter
        }
    }

    private fun loadMessages() {
        val userId = auth.currentUser?.uid ?: return
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerViewMessages.visibility = View.GONE
        binding.tvNoMessages.visibility = View.GONE

        db.collection("users")
            .document(userId)
            .collection("receivedMessages")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                binding.progressBar.visibility = View.GONE
                if (e != null) {
                    Log.e("MessagesFragment", "Error getting messages", e)
                    showError("Error loading messages: ${e.message}")
                    return@addSnapshotListener
                }

                val messages = mutableListOf<Message>()
                for (doc in snapshots!!) {
                    val shopId = doc.getString("shopId") ?: ""
                    getShopName(shopId) { shopName ->
                        val message = Message(
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            time = formatTimestamp(doc.getLong("time") ?: 0),
                            shopId = shopId,
                            shopName = shopName,
                            id = doc.id
                        )
                        messages.add(message)

                        if (messages.size == snapshots.size()) {
                            updateUI(messages)
                        }
                    }
                }
            }
    }

    private fun getShopName(shopId: String, callback: (String) -> Unit) {
        db.collection("shops")
            .document(shopId)
            .get()
            .addOnSuccessListener { document ->
                val shopName = document.getString("name") ?: "Unknown Shop"
                callback(shopName)
            }
            .addOnFailureListener { e ->
                Log.e("MessagesFragment", "Error getting shop name", e)
                callback("Unknown Shop")
            }
    }

    private fun updateUI(messages: List<Message>) {
        if (messages.isEmpty()) {
            binding.recyclerViewMessages.visibility = View.GONE
            binding.tvNoMessages.visibility = View.VISIBLE
        } else {
            binding.recyclerViewMessages.visibility = View.VISIBLE
            binding.tvNoMessages.visibility = View.GONE
            adapter.updateMessages(messages)
        }
    }

    private fun openMessageDetail(message: Message) {
        val intent = Intent(requireContext(), MessagesDetail::class.java).apply {
            putExtra("SHOP_NAME", message.shopName)
            putExtra("MESSAGE_DESCRIPTION", message.description)
            putExtra("MESSAGE_TIME", message.time)
            putExtra("MESSAGE_CONTENT", "")
        }
        startActivity(intent)
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}