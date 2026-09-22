package com.lindokuhle.circle.utils

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Post(
    val content: String,
    val imageUri: String?
)

class PostStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "CirclePosts",
        Context.MODE_PRIVATE
    )

    fun savePost(
        post: String,
        imageUri: String? = null
    ) {

        val posts = getPosts().toMutableList()

        posts.add(
            Post(
                content = post,
                imageUri = imageUri
            )
        )

        val jsonArray = JSONArray()

        for (item in posts) {

            val jsonObject = JSONObject()

            jsonObject.put(
                "content",
                item.content
            )

            if (item.imageUri != null) {

                jsonObject.put(
                    "imageUri",
                    item.imageUri
                )

            } else {

                jsonObject.put(
                    "imageUri",
                    JSONObject.NULL
                )
            }

            jsonArray.put(jsonObject)
        }

        preferences.edit()
            .putString(
                "posts",
                jsonArray.toString()
            )
            .putString(
                "latestPost",
                post
            )
            .putInt(
                "postCount",
                posts.size
            )
            .apply()
    }

    fun getLatestPost(): String {

        return preferences.getString(
            "latestPost",
            ""
        ) ?: ""
    }

    fun getPostCount(): Int {

        return preferences.getInt(
            "postCount",
            getPosts().size
        )
    }

    fun getPosts(): List<Post> {

        val savedPosts = preferences.getString(
            "posts",
            ""
        )

        if (savedPosts.isNullOrEmpty()) {

            val oldLatestPost = preferences.getString(
                "latestPost",
                ""
            )

            return if (!oldLatestPost.isNullOrEmpty()) {

                listOf(
                    Post(
                        content = oldLatestPost,
                        imageUri = null
                    )
                )

            } else {

                emptyList()
            }
        }

        return try {

            val jsonArray = JSONArray(savedPosts)

            val posts = mutableListOf<Post>()

            for (i in 0 until jsonArray.length()) {

                val jsonObject =
                    jsonArray.getJSONObject(i)

                val content =
                    jsonObject.getString("content")

                val imageUri =
                    if (jsonObject.isNull("imageUri")) {
                        null
                    } else {
                        jsonObject.getString("imageUri")
                    }

                posts.add(
                    Post(
                        content = content,
                        imageUri = imageUri
                    )
                )
            }

            posts

        } catch (e: Exception) {

            emptyList()
        }
    }

    fun deletePost(postIndex: Int) {

        val posts = getPosts().toMutableList()

        if (
            postIndex < 0 ||
            postIndex >= posts.size
        ) {
            return
        }

        posts.removeAt(postIndex)

        val jsonArray = JSONArray()

        for (item in posts) {

            val jsonObject = JSONObject()

            jsonObject.put(
                "content",
                item.content
            )

            if (item.imageUri != null) {

                jsonObject.put(
                    "imageUri",
                    item.imageUri
                )

            } else {

                jsonObject.put(
                    "imageUri",
                    JSONObject.NULL
                )
            }

            jsonArray.put(jsonObject)
        }

        preferences.edit()
            .putString(
                "posts",
                jsonArray.toString()
            )
            .putInt(
                "postCount",
                posts.size
            )
            .apply()
    }

    fun updatePost(
        postIndex: Int,
        newContent: String
    ) {

        val posts = getPosts().toMutableList()

        if (
            postIndex < 0 ||
            postIndex >= posts.size
        ) {
            return
        }

        val existingPost = posts[postIndex]

        // Keep the existing photo URI when editing the text
        posts[postIndex] = Post(
            content = newContent,
            imageUri = existingPost.imageUri
        )

        val jsonArray = JSONArray()

        for (item in posts) {

            val jsonObject = JSONObject()

            jsonObject.put(
                "content",
                item.content
            )

            // Preserve the existing image URI
            if (!item.imageUri.isNullOrEmpty()) {

                jsonObject.put(
                    "imageUri",
                    item.imageUri
                )

            } else {

                jsonObject.put(
                    "imageUri",
                    JSONObject.NULL
                )
            }

            jsonArray.put(jsonObject)
        }

        preferences.edit()
            .putString(
                "posts",
                jsonArray.toString()
            )
            .putString(
                "latestPost",
                newContent
            )
            .putInt(
                "postCount",
                posts.size
            )
            .apply()
    }
}