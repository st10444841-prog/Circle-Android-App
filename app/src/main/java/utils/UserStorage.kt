package com.lindokuhle.circle.utils

import android.content.Context

class UserStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "CircleUser",
        Context.MODE_PRIVATE
    )

    fun saveUser(
        fullName: String,
        username: String,
        email: String,
        password: String
    ) {

        preferences.edit()
            .putString("fullName", fullName)
            .putString("username", username)
            .putString("email", email)
            .putString("password", password)
            .apply()
    }

    fun getEmail(): String {
        return preferences.getString(
            "email",
            ""
        ) ?: ""
    }

    fun getPassword(): String {
        return preferences.getString(
            "password",
            ""
        ) ?: ""
    }

    fun getFullName(): String {
        return preferences.getString(
            "fullName",
            ""
        ) ?: ""
    }

    fun getUsername(): String {
        return preferences.getString(
            "username",
            ""
        ) ?: ""
    }

    fun hasUser(): Boolean {
        return getEmail().isNotEmpty()
    }
}