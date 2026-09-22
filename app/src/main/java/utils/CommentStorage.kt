package com.lindokuhle.circle.utils

import android.content.Context
import org.json.JSONArray

class CommentStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "CircleComments",
        Context.MODE_PRIVATE
    )

    fun saveComment(
        postIndex: Int,
        comment: String
    ) {

        val key = "comments_$postIndex"

        val comments = getComments(postIndex).toMutableList()

        comments.add(comment)

        val jsonArray = JSONArray()

        for (item in comments) {
            jsonArray.put(item)
        }

        preferences.edit()
            .putString(
                key,
                jsonArray.toString()
            )
            .apply()
    }

    fun getComments(postIndex: Int): List<String> {

        val key = "comments_$postIndex"

        val savedComments = preferences.getString(
            key,
            ""
        )

        if (savedComments.isNullOrEmpty()) {
            return emptyList()
        }

        return try {

            val jsonArray = JSONArray(savedComments)

            val comments = mutableListOf<String>()

            for (i in 0 until jsonArray.length()) {

                comments.add(
                    jsonArray.getString(i)
                )
            }

            comments

        } catch (e: Exception) {

            emptyList()
        }
    }

    fun getCommentCount(postIndex: Int): Int {

        return getComments(postIndex).size
    }
}