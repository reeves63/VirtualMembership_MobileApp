package com.example.uts_pagisore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class ManagePointsFragment : Fragment() {

    private lateinit var pointsEditText: EditText
    private lateinit var addButton: Button
    private lateinit var subtractButton: Button
    private var currentPoints: Int = 0 // default points

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_manage_points, container, false)

        // Initialize the view components
        pointsEditText = rootView.findViewById(R.id.pointsEditText)
        addButton = rootView.findViewById(R.id.addButton)
        subtractButton = rootView.findViewById(R.id.subtractButton)

        // Set the initial value for points
        pointsEditText.setText(currentPoints.toString())

        // Button to add points
        addButton.setOnClickListener {
            val pointsToAdd = pointsEditText.text.toString().toIntOrNull()
            if (pointsToAdd != null) {
                currentPoints += pointsToAdd
                updatePoints()
            } else {
                Toast.makeText(requireContext(), "Masukkan jumlah poin yang valid", Toast.LENGTH_SHORT).show()
            }
        }

        // Button to subtract points
        subtractButton.setOnClickListener {
            val pointsToSubtract = pointsEditText.text.toString().toIntOrNull()
            if (pointsToSubtract != null) {
                if (currentPoints >= pointsToSubtract) {
                    currentPoints -= pointsToSubtract
                    updatePoints()
                } else {
                    Toast.makeText(requireContext(), "Poin tidak cukup", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Masukkan jumlah poin yang valid", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }

    // Update the points display
    private fun updatePoints() {
        pointsEditText.setText(currentPoints.toString())
    }
}
