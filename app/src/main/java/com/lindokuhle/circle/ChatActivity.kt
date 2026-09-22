package com.lindokuhle.circle

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lindokuhle.circle.utils.ChatStorage

class ChatActivity : AppCompatActivity() {

    private lateinit var tvChatTitle: TextView
    private lateinit var tvChatMessages: TextView
    private lateinit var etMessage: EditText
    private lateinit var btnSendMessage: Button

    private lateinit var chatStorage: ChatStorage

    private var friendName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        tvChatTitle = findViewById(R.id.tvChatTitle)
        tvChatMessages = findViewById(R.id.tvChatMessages)
        etMessage = findViewById(R.id.etMessage)
        btnSendMessage = findViewById(R.id.btnSendMessage)

        chatStorage = ChatStorage(this)

        friendName = intent.getStringExtra("friendName") ?: ""

        if (friendName.isNotEmpty()) {
            tvChatTitle.text = "Chat with $friendName"
        }

        loadMessages()

        btnSendMessage.setOnClickListener {
            sendMessage()
        }
    }

    private fun loadMessages() {

        val messages = chatStorage.getMessages(friendName)

        if (messages.isEmpty()) {

            tvChatMessages.text = "No messages yet."

        } else {

            tvChatMessages.text = messages.joinToString(
                separator = "\n\n"
            ) { message ->
                "You: $message"
            }
        }
    }

    private fun sendMessage() {

        val message = etMessage.text.toString().trim()

        if (message.isEmpty()) {
            etMessage.error = "Please enter a message"
            etMessage.requestFocus()
            return
        }

        chatStorage.saveMessage(
            friendName,
            message
        )

        loadMessages()

        etMessage.text.clear()

        Toast.makeText(
            this,
            "Message sent",
            Toast.LENGTH_SHORT
        ).show()
    }
}