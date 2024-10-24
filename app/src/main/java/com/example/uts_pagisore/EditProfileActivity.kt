package com.example.uts_pagisore

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {

    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var editSurname: EditText
    private lateinit var editName: EditText
    private lateinit var editDOB: EditText
    private lateinit var editPhoneNumber: EditText
    private lateinit var editEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)  // Atur layout di sini

        // Inisialisasi komponen
        saveButton = findViewById(R.id.buttonSave)
        cancelButton = findViewById(R.id.buttonCancel)
        editSurname = findViewById(R.id.editSurname)
        editName = findViewById(R.id.editName)
        editDOB = findViewById(R.id.editDOB)
        editPhoneNumber = findViewById(R.id.editPhoneNumber)
        editEmail = findViewById(R.id.editEmail)

        // Tombol Save
        saveButton.setOnClickListener {
            // Implementasikan logika penyimpanan perubahan profil
            saveProfileChanges()
        }

        // Tombol Cancel
        cancelButton.setOnClickListener {
            // Kembali ke activity sebelumnya
            finish()
        }
    }

    private fun saveProfileChanges() {
        // Implementasikan logika untuk menyimpan perubahan profil
        val newSurname = editSurname.text.toString()
        val newName = editName.text.toString()
        val newDOB = editDOB.text.toString()
        val newPhoneNumber = editPhoneNumber.text.toString()
        val newEmail = editEmail.text.toString()

        // Simpan perubahan ke database atau shared preferences, tergantung arsitektur aplikasi Anda
    }
}
