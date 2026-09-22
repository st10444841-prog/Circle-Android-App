package com.lindokuhle.circle.utils

import android.content.Context

class FriendsStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "CircleFriends",
        Context.MODE_PRIVATE
    )

    fun getFriendCount(): Int {

        return preferences.getInt(
            "friendCount",
            3
        )
    }
}