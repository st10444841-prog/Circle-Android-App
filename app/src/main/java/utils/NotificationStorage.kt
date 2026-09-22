package com.lindokuhle.circle.utils

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class CircleNotification(
    val message: String,
    val type: String
)

class NotificationStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "CircleNotifications",
        Context.MODE_PRIVATE
    )

    fun addNotification(
        message: String,
        type: String
    ) {

        val notifications = getNotifications().toMutableList()

        notifications.add(
            CircleNotification(
                message = message,
                type = type
            )
        )

        val jsonArray = JSONArray()

        for (notification in notifications) {

            val jsonObject = JSONObject()

            jsonObject.put(
                "message",
                notification.message
            )

            jsonObject.put(
                "type",
                notification.type
            )

            jsonArray.put(jsonObject)
        }

        preferences.edit()
            .putString(
                "notifications",
                jsonArray.toString()
            )
            .apply()
    }

    fun getNotifications(): List<CircleNotification> {

        val savedNotifications = preferences.getString(
            "notifications",
            ""
        )

        if (savedNotifications.isNullOrEmpty()) {
            return emptyList()
        }

        return try {

            val jsonArray = JSONArray(savedNotifications)

            val notifications = mutableListOf<CircleNotification>()

            for (i in 0 until jsonArray.length()) {

                val jsonObject = jsonArray.getJSONObject(i)

                notifications.add(
                    CircleNotification(
                        message = jsonObject.getString("message"),
                        type = jsonObject.getString("type")
                    )
                )
            }

            notifications

        } catch (e: Exception) {

            emptyList()
        }
    }

    fun getNotificationCount(): Int {
        return getNotifications().size
    }

    fun clearNotifications() {

        preferences.edit()
            .remove("notifications")
            .apply()
    }
}