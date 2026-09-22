package com.lindokuhle.circle.utils

import android.content.Context
import org.json.JSONArray

class ChatStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "CircleChats",
        Context.MODE_PRIVATE
    )

    fun saveMessage(friendName: String, message: String) {

        val messages = getMessages(friendName).toMutableList()

        messages.add(message)

        val jsonArray = JSONArray()

        for (item in messages) {
            jsonArray.put(item)
        }

        preferences.edit()
            .putString(
                "chat_$friendName",
                jsonArray.toString()
            )
            .apply()
    }

    fun getMessages(friendName: String): List<String> {

        val savedMessages = preferences.getString(
            "chat_$friendName",
            ""
        )

        if (savedMessages.isNullOrEmpty()) {
            return emptyList()
        }

        return try {

            val jsonArray = JSONArray(savedMessages)
            val messages = mutableListOf<String>()

            for (i in 0 until jsonArray.length()) {
                messages.add(jsonArray.getString(i))
            }

            messages

        } catch (e: Exception) {

            emptyList()
        }
    }
}