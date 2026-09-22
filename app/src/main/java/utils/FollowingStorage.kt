package com.lindokuhle.circle.utils

import android.content.Context

class FollowingStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "CircleFollowing",
        Context.MODE_PRIVATE
    )

    fun getFollowingCount(): Int {

        return preferences.getInt(
            "followingCount",
            0
        )
    }

    fun increaseFollowing() {

        val currentCount = getFollowingCount()

        preferences.edit()
            .putInt(
                "followingCount",
                currentCount + 1
            )
            .apply()
    }

    fun decreaseFollowing() {

        val currentCount = getFollowingCount()

        if (currentCount > 0) {

            preferences.edit()
                .putInt(
                    "followingCount",
                    currentCount - 1
                )
                .apply()
        }
    }

    fun setFollowing(person: String, isFollowing: Boolean) {

        preferences.edit()
            .putBoolean(
                "following_$person",
                isFollowing
            )
            .apply()
    }

    fun isFollowing(person: String): Boolean {

        return preferences.getBoolean(
            "following_$person",
            false
        )
    }
}