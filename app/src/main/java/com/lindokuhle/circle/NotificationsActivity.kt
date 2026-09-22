package com.lindokuhle.circle

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lindokuhle.circle.utils.CircleNotification
import com.lindokuhle.circle.utils.NotificationStorage

class NotificationsActivity : AppCompatActivity() {

    private lateinit var notificationStorage: NotificationStorage
    private lateinit var notificationsContainer: LinearLayout
    private lateinit var tvNotificationStatus: TextView
    private lateinit var btnClearNotifications: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        notificationStorage = NotificationStorage(this)

        notificationsContainer = findViewById(
            R.id.notificationsContainer
        )

        tvNotificationStatus = findViewById(
            R.id.tvNotificationStatus
        )

        btnClearNotifications = findViewById(
            R.id.btnClearNotifications
        )

        loadNotifications()

        btnClearNotifications.setOnClickListener {

            notificationStorage.clearNotifications()

            loadNotifications()

            Toast.makeText(
                this,
                "Notifications cleared",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotifications()
    }

    private fun loadNotifications() {

        notificationsContainer.removeAllViews()

        val notifications =
            notificationStorage.getNotifications()

        if (notifications.isEmpty()) {

            tvNotificationStatus.text =
                "You have no notifications."

            return
        }

        tvNotificationStatus.text =
            "${notifications.size} notification(s)"

        for (notification in notifications.asReversed()) {

            val notificationView =
                createNotificationView(notification)

            notificationsContainer.addView(
                notificationView
            )
        }
    }

    private fun createNotificationView(
        notification: CircleNotification
    ): TextView {

        val textView = TextView(this)

        textView.text = notification.message
        textView.textSize = 16f
        textView.setTextColor(Color.DKGRAY)

        textView.setPadding(
            16,
            20,
            16,
            20
        )

        textView.setBackgroundResource(
            R.drawable.post_card_background
        )

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(
            0,
            0,
            0,
            12
        )

        textView.layoutParams = params

        return textView
    }
}