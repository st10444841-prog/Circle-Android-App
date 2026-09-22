package com.lindokuhle.circle.utils

import android.content.Context

class ProfileStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "CircleProfile",
        Context.MODE_PRIVATE
    )

    fun saveProfile(name: String, username: String) {

        preferences.edit()
            .putString("name", name)
            .putString("username", username)
            .apply()
    }

    fun getName(): String {
        return preferences.getString(
            "name",
            "Lindokuhle Maseko"
        ) ?: "Lindokuhle Maseko"
    }

    fun getUsername(): String {
        return preferences.getString(
            "username",
            "lindokuhle"
        ) ?: "lindokuhle"
    }
}