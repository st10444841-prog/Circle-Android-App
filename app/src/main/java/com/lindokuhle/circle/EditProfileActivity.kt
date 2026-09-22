package com.lindokuhle.circle

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lindokuhle.circle.utils.ProfileStorage

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etEditName: EditText
    private lateinit var etEditUsername: EditText
    private lateinit var btnSaveProfile: Button

    private lateinit var profileStorage: ProfileStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        etEditName = findViewById(R.id.etEditName)
        etEditUsername = findViewById(R.id.etEditUsername)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)

        profileStorage = ProfileStorage(this)

        loadProfile()

        btnSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadProfile() {

        etEditName.setText(profileStorage.getName())
        etEditUsername.setText(profileStorage.getUsername())
    }

    private fun saveProfile() {

        val name = etEditName.text.toString().trim()
        val username = etEditUsername.text.toString().trim()

        if (name.isEmpty()) {
            etEditName.error = "Please enter your name"
            etEditName.requestFocus()
            return
        }

        if (username.isEmpty()) {
            etEditUsername.error = "Please enter a username"
            etEditUsername.requestFocus()
            return
        }

        profileStorage.saveProfile(name, username)

        Toast.makeText(
            this,
            "Profile updated successfully!",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}