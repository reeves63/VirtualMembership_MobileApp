package com.example.uts_pagisore.Membership

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uts_pagisore.R

class AddMembershipActivity : AppCompatActivity() {

    private lateinit var editTextShopID: EditText
    private lateinit var buttonAddMembership: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_membership)

        // Initialize views
        editTextShopID = findViewById(R.id.editTextShopID)
        buttonAddMembership = findViewById(R.id.buttonAddMembership)

        // Handle add membership button click
        buttonAddMembership.setOnClickListener {
            val shopID = editTextShopID.text.toString().trim()

            if (shopID.isNotEmpty()) {
                // Proceed with adding membership logic
                Toast.makeText(this, "Membership added for Shop ID: $shopID", Toast.LENGTH_SHORT).show()
                // You can now perform further operations like saving to a database or calling an API.
            } else {
                Toast.makeText(this, "Please enter a valid Shop ID", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
