package com.example.uts_pagisore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts_pagisore.databinding.FragmentMessagesBinding

class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using ViewBinding
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val view = binding.root

        // Setup RecyclerView with the correct ID and click listener
        binding.rvMessageList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMessageList.adapter = MessagesAdapter(getMessagesList()) { message ->
            // When a message is clicked, start the MessagesDetail activity with the message data
            val intent = Intent(requireContext(), MessagesDetail::class.java)
            intent.putExtra("MESSAGE_TITLE", message.title)
            intent.putExtra("MESSAGE_DESCRIPTION", message.description)
            intent.putExtra("MESSAGE_TIME", message.time)
            intent.putExtra("MESSAGE_CONTENT", "Detailed content for ${message.title}") // You can customize the content if needed
            intent.putExtra("MESSAGE_ICON", R.drawable.bird_icon) // Optionally pass an icon if needed
            startActivity(intent)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Dummy data for messages
    private fun getMessagesList(): List<Message> {
        return listOf(
            Message("Frost Gaming x Pool", "PROMO WEEKDAYS for VIP ROOM", "3 hours ago"),
            Message("Syahrin's Wash", "DISKON 50% untuk Mahasiswa", "5 hours ago"),
            Message("December Coffee", "PROMO Tuesday Special\n2 Matcha Latte hanya Rp30.000", "7 hours ago")
        )
    }
}
