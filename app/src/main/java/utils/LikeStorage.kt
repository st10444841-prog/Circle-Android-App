package com.lindokuhle.circle.utils

import android.content.Context

class LikeStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "CircleLikes",
        Context.MODE_PRIVATE
    )

    fun getLikes(postIndex: Int): Int {
        return preferences.getInt(
            "likes_$postIndex",
            0
        )
    }

    fun isLiked(postIndex: Int): Boolean {
        return preferences.getBoolean(
            "liked_$postIndex",
            false
        )
    }

    fun likePost(postIndex: Int) {

        val currentLikes = getLikes(postIndex)

        preferences.edit()
            .putInt(
                "likes_$postIndex",
                currentLikes + 1
            )
            .putBoolean(
                "liked_$postIndex",
                true
            )
            .apply()
    }

    fun unlikePost(postIndex: Int) {

        val currentLikes = getLikes(postIndex)

        if (currentLikes > 0) {

            preferences.edit()
                .putInt(
                    "likes_$postIndex",
                    currentLikes - 1
                )
                .putBoolean(
                    "liked_$postIndex",
                    false
                )
                .apply()
        }
    }
}