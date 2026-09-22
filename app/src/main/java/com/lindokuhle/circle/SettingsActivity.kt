package com.lindokuhle.circle

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnNotifications: Button
    private lateinit var btnPrivacy: Button
    private lateinit var btnChangePassword: Button
    private lateinit var btnDarkMode: Button
    private lateinit var btnAbout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        btnNotifications = findViewById(R.id.btnNotifications)
        btnPrivacy = findViewById(R.id.btnPrivacy)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        btnDarkMode = findViewById(R.id.btnDarkMode)
        btnAbout = findViewById(R.id.btnAbout)

        btnNotifications.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        btnPrivacy.setOnClickListener {
            val intent = Intent(this, PrivacyActivity::class.java)
            startActivity(intent)
        }

        btnChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        btnDarkMode.setOnClickListener {
            toggleDarkMode()
        }

        btnAbout.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
    }

    private fun toggleDarkMode() {

        val currentMode = AppCompatDelegate.getDefaultNightMode()

        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {

            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )

            Toast.makeText(
                this,
                "Light mode enabled",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )

            Toast.makeText(
                this,
                "Dark mode enabled",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}