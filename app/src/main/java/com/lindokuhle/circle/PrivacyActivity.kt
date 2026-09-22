package com.lindokuhle.circle

import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PrivacyActivity : AppCompatActivity() {

    private lateinit var switchPrivateAccount: Switch
    private lateinit var switchShowActivity: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)

        switchPrivateAccount = findViewById(R.id.switchPrivateAccount)
        switchShowActivity = findViewById(R.id.switchShowActivity)

        switchPrivateAccount.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                Toast.makeText(
                    this,
                    "Your account is now private",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Your account is now public",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        switchShowActivity.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                Toast.makeText(
                    this,
                    "Your activity is visible",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Your activity is hidden",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}