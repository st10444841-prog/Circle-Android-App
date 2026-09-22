package com.lindokuhle.circle

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class FriendsActivity : AppCompatActivity() {

    private lateinit var btnMessageThabo: Button
    private lateinit var btnMessageLebo: Button
    private lateinit var btnMessageSipho: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        btnMessageThabo = findViewById(R.id.btnMessageThabo)
        btnMessageLebo = findViewById(R.id.btnMessageLebo)
        btnMessageSipho = findViewById(R.id.btnMessageSipho)

        btnMessageThabo.setOnClickListener {
            openChat("Thabo M.")
        }

        btnMessageLebo.setOnClickListener {
            openChat("Lebo K.")
        }

        btnMessageSipho.setOnClickListener {
            openChat("Sipho N.")
        }
    }

    private fun openChat(friendName: String) {

        val intent = Intent(this, ChatActivity::class.java)

        intent.putExtra("friendName", friendName)

        startActivity(intent)
    }
}