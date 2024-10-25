package com.example.uts_pagisore

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var fullNameEditText: EditText
    private lateinit var dobTextView: TextView
    private lateinit var phoneNumberEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var changePictureButton: Button
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var radioMale: RadioButton
    private lateinit var radioFemale: RadioButton
    private lateinit var profileImageView: ImageView

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Bind UI elements
        fullNameEditText = findViewById(R.id.editFullName)
        dobTextView = findViewById(R.id.editDOB)
        phoneNumberEditText = findViewById(R.id.editPhoneNumber)
        genderRadioGroup = findViewById(R.id.genderGroup)
        radioMale = findViewById(R.id.radioMale)
        radioFemale = findViewById(R.id.radioFemale)
        saveButton = findViewById(R.id.buttonSave)
        changePictureButton = findViewById(R.id.buttonChangePhoto)
        profileImageView = findViewById(R.id.profilePhotoEdit)

        // Load current profile data if exists
        loadProfileData()

        // Set up DatePicker for date of birth
        dobTextView.setOnClickListener {
            showDatePickerDialog()
        }

        // Change profile picture button
        changePictureButton.setOnClickListener {
            requestImagePermissionOrOpenGallery()
        }

        // Save profile data when Save button is clicked
        saveButton.setOnClickListener {
            saveProfileData()
        }
    }

    // Date picker for DOB
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "$selectedYear/${selectedMonth + 1}/$selectedDay"
            dobTextView.text = formattedDate
        }, year, month, day)

        datePickerDialog.show()
    }

    // Load profile data from Firestore
    private fun loadProfileData() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val docRef = firestore.collection("users").document(userId)
            docRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    fullNameEditText.setText(document.getString("fullName"))
                    dobTextView.text = document.getString("dob")
                    phoneNumberEditText.setText(document.getString("phoneNumber"))
                    val gender = document.getString("gender")
                    if (gender == "Male") {
                        radioMale.isChecked = true
                    } else if (gender == "Female") {
                        radioFemale.isChecked = true
                    }
                    val profileImageUrl = document.getString("profileImageUrl")

                    // Load profile image if URL exists
                    profileImageUrl?.let {
                        Glide.with(this)
                            .load(it)
                            .placeholder(R.drawable.profile_picture_placeholder)
                            .circleCrop()
                            .into(profileImageView)
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to load profile data.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Save profile data to Firestore
    private fun saveProfileData() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid

            val fullName = fullNameEditText.text.toString()
            val dob = dobTextView.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()
            val selectedGenderId = genderRadioGroup.checkedRadioButtonId
            val gender = if (selectedGenderId == R.id.radioMale) "Male" else "Female"

            if (fullName.isEmpty()) {
                Toast.makeText(this, "Full Name cannot be empty", Toast.LENGTH_SHORT).show()
                return
            }

            val profileData = hashMapOf(
                "fullName" to fullName,
                "dob" to dob,
                "phoneNumber" to phoneNumber,
                "gender" to gender,
                "email" to user.email
            )

            firestore.collection("users").document(userId)
                .set(profileData)
                .addOnSuccessListener {
                    selectedImageUri?.let { uri ->
                        uploadProfileImage(uri, userId)
                    } ?: run {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Request permission for image selection or open gallery
    private fun requestImagePermissionOrOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Untuk Android 13+ gunakan READ_MEDIA_IMAGES
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openGallery()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) -> {
                    Toast.makeText(this, "Aplikasi membutuhkan izin untuk mengakses foto.", Toast.LENGTH_LONG).show()
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Untuk Android versi 6 hingga 12 gunakan READ_EXTERNAL_STORAGE
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openGallery()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    Toast.makeText(this, "Aplikasi membutuhkan izin untuk mengakses foto.", Toast.LENGTH_LONG).show()
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        } else {
            openGallery() // Untuk Android di bawah versi 6.0
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(this, "Izin ditolak untuk mengakses foto. Silakan aktifkan izin di Pengaturan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            selectedImageUri = result.data?.data
            selectedImageUri?.let { uri ->
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.profile_picture_placeholder)
                    .circleCrop()
                    .into(profileImageView)
            }
        }
    }

    // Upload profile image to Firebase Storage
    private fun uploadProfileImage(imageUri: Uri, userId: String) {
        val storageRef = storage.reference.child("profileImages/$userId.jpg")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    firestore.collection("users").document(userId)
                        .update("profileImageUrl", uri.toString())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile and image updated successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to save image URL to Firestore", Toast.LENGTH_SHORT).show()
                        }
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to retrieve download URL from Firebase Storage", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image to Firebase Storage", Toast.LENGTH_SHORT).show()
            }
    }
}
