package com.lindokuhle.circle

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lindokuhle.circle.utils.CommentStorage
import com.lindokuhle.circle.utils.LikeStorage
import com.lindokuhle.circle.utils.PostStorage
import com.lindokuhle.circle.utils.NotificationStorage

class HomeActivity : AppCompatActivity() {

    private lateinit var postsContainer: LinearLayout
    private lateinit var postStorage: PostStorage
    private lateinit var likeStorage: LikeStorage

    private lateinit var commentStorage: CommentStorage

    private lateinit var notificationStorage: NotificationStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        postsContainer = findViewById(R.id.postsContainer)

        postStorage = PostStorage(this)
        likeStorage = LikeStorage(this)
        commentStorage = CommentStorage(this)
        notificationStorage = NotificationStorage(this)
        loadPosts()

        val btnDiscover = findViewById<Button>(R.id.btnDiscover)
        val btnCreatePost = findViewById<Button>(R.id.btnCreatePost)
        val btnFriends = findViewById<Button>(R.id.btnFriends)
        val btnProfile = findViewById<Button>(R.id.btnProfile)

        btnDiscover.setOnClickListener {
            startActivity(Intent(this, DiscoverActivity::class.java))
        }

        btnCreatePost.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }

        btnFriends.setOnClickListener {
            startActivity(Intent(this, FriendsActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadPosts()
    }

    private fun loadPosts() {

        postsContainer.removeAllViews()

        val posts = postStorage.getPosts()

        if (posts.isEmpty()) {

            val emptyText = TextView(this)

            emptyText.text = "No posts yet."
            emptyText.textSize = 16f
            emptyText.setTextColor(Color.DKGRAY)
            emptyText.setPadding(16, 16, 16, 16)

            postsContainer.addView(emptyText)

            return
        }

        val inflater = LayoutInflater.from(this)

        for (originalIndex in posts.indices.reversed()) {

            val post = posts[originalIndex]

            val postView = inflater.inflate(
                R.layout.item_post,
                postsContainer,
                false
            )

            val tvPostName = postView.findViewById<TextView>(
                R.id.tvPostName
            )

            val tvPostUsername = postView.findViewById<TextView>(
                R.id.tvPostUsername
            )

            val tvPostContent = postView.findViewById<TextView>(
                R.id.tvPostContent
            )

            val ivPostImage = postView.findViewById<ImageView>(
                R.id.ivPostImage
            )

            val btnLike = postView.findViewById<Button>(
                R.id.btnLike
            )

            val btnComment = postView.findViewById<Button>(
                R.id.btnComment
            )
            val btnDeletePost = postView.findViewById<Button>(
                R.id.btnDeletePost
            )
            val btnEditPost = postView.findViewById<Button>(
                R.id.btnEditPost
            )
            val commentCount = commentStorage.getCommentCount(
                originalIndex
            )

            btnComment.text = "💬 Comment ($commentCount)"

            val commentsContainer = postView.findViewById<LinearLayout>(
                R.id.commentsContainer
            )

            val savedComments = commentStorage.getComments(originalIndex)

            if (savedComments.isNotEmpty()) {

                commentsContainer.removeAllViews()

                for (comment in savedComments) {

                    val commentText = TextView(this)

                    commentText.text = "Lindokuhle: $comment"
                    commentText.textSize = 18f
                    commentText.setTextColor(Color.RED)
                    commentText.setPadding(12, 12, 12, 12)

                    commentsContainer.addView(commentText)
                }
            }

            tvPostName.text = "Lindokuhle Maseko"
            tvPostUsername.text = "@lindokuhle"
            tvPostContent.text = post.content

            if (!post.imageUri.isNullOrEmpty()) {

                try {

                    ivPostImage.setImageURI(
                        Uri.parse(post.imageUri)
                    )

                    ivPostImage.visibility = ImageView.VISIBLE

                } catch (e: SecurityException) {

                    ivPostImage.setImageDrawable(null)
                    ivPostImage.visibility = ImageView.GONE
                }

            } else {

                ivPostImage.visibility = ImageView.GONE
            }

            updateLikeButton(
                btnLike,
                originalIndex
            )

            btnLike.setOnClickListener {

                if (likeStorage.isLiked(originalIndex)) {

                    likeStorage.unlikePost(originalIndex)

                } else {

                    likeStorage.likePost(originalIndex)

                    notificationStorage.addNotification(
                        "Lindokuhle liked your post.",
                        "like"
                    )
                }

                updateLikeButton(
                    btnLike,
                    originalIndex
                )
            }

            btnComment.setOnClickListener {

                showCommentDialog(originalIndex)
            }
            btnDeletePost.setOnClickListener {

                postStorage.deletePost(
                    originalIndex
                )

                loadPosts()

                android.widget.Toast.makeText(
                    this,
                    "Post deleted successfully!",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
            btnEditPost.setOnClickListener {

                showEditPostDialog(
                    originalIndex,
                    post.content
                )
            }

            postsContainer.addView(postView)
        }
    }

    private fun updateLikeButton(
        button: Button,
        postIndex: Int
    ) {

        val likes = likeStorage.getLikes(postIndex)
        val liked = likeStorage.isLiked(postIndex)

        if (liked) {

            button.text = "♥ Liked ($likes)"

        } else {

            button.text = "♡ Like ($likes)"
        }
    }
    private fun showCommentDialog(postIndex: Int) {

        val commentInput = android.widget.EditText(this)

        commentInput.hint = "Write a comment..."
        commentInput.setPadding(24, 16, 24, 16)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Add Comment")
            .setView(commentInput)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Post") { _, _ ->

                val comment = commentInput.text.toString().trim()

                if (comment.isNotEmpty()) {

                    commentStorage.saveComment(
                        postIndex,
                        comment
                    )

                    notificationStorage.addNotification(
                        "Lindokuhle commented on your post.",
                        "comment"
                    )

                    loadPosts()

                    android.widget.Toast.makeText(
                        this,
                        "Comment added successfully!",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()

                } else {

                    android.widget.Toast.makeText(
                        this,
                        "Please write a comment",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .create()

        dialog.show()
    }
    private fun loadComments(
        container: LinearLayout,
        postIndex: Int
    ) {

        container.removeAllViews()

        val comments = commentStorage.getComments(
            postIndex
        )

        if (comments.isEmpty()) {
            return
        }

        for (comment in comments) {

            val commentText = TextView(this)

            commentText.text = "Lindokuhle: $comment"
            commentText.textSize = 14f
            commentText.setTextColor(
                Color.DKGRAY
            )

            commentText.setPadding(
                12,
                8,
                12,
                8
            )

            container.addView(
                commentText
            )
        }
    }
    private fun showEditPostDialog(
        postIndex: Int,
        currentContent: String
    ) {

        val editText = android.widget.EditText(this)

        editText.setText(currentContent)
        editText.setSelection(editText.text.length)
        editText.setPadding(24, 16, 24, 16)

        val dialog =
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Edit Post")
                .setView(editText)
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .setPositiveButton(
                    "Save"
                ) { _, _ ->

                    val newContent =
                        editText.text.toString().trim()

                    if (newContent.isNotEmpty()) {

                        postStorage.updatePost(
                            postIndex,
                            newContent
                        )

                        loadPosts()

                        android.widget.Toast.makeText(
                            this,
                            "Post updated successfully!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        android.widget.Toast.makeText(
                            this,
                            "Post cannot be empty",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .create()

        dialog.show()
    }
}