
package com.lindokuhle.circle

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

import com.lindokuhle.circle.utils.CommentStorage
import com.lindokuhle.circle.utils.LikeStorage
import com.lindokuhle.circle.utils.PostStorage
import com.lindokuhle.circle.utils.NotificationStorage

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class HomeActivity : AppCompatActivity() {

    private lateinit var postsContainer: LinearLayout
    private lateinit var postStorage: PostStorage
    private lateinit var likeStorage: LikeStorage
    private lateinit var commentStorage: CommentStorage
    private lateinit var notificationStorage: NotificationStorage

    private var cloudPosts: List<DocumentSnapshot> = emptyList()
    private var postsListener: ListenerRegistration? = null

    private val cloudLikeListeners = mutableListOf<ListenerRegistration>()
    private val cloudCommentListeners = mutableListOf<ListenerRegistration>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        postsContainer = findViewById(R.id.postsContainer)

// Load Daily Inspiration from the REST API.
        loadDailyInspiration()

// Refresh the inspiration when the button is clicked.
        val btnRefreshInspiration =
            findViewById<Button>(R.id.btnRefreshInspiration)

        btnRefreshInspiration.setOnClickListener {
            loadDailyInspiration()
        }

        postStorage = PostStorage(this)
        likeStorage = LikeStorage(this)
        commentStorage = CommentStorage(this)
        notificationStorage = NotificationStorage(this)

        loadPosts()
        listenForCloudPosts()

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

    // Load both cloud posts and existing local posts.
    private fun loadPosts() {

        postsContainer.removeAllViews()

        displayCloudPosts()

        val posts = postStorage.getPosts()

        if (posts.isEmpty() && cloudPosts.isEmpty()) {
            val emptyText = TextView(this)
            emptyText.text = "No posts yet."
            emptyText.textSize = 16f
            emptyText.setTextColor(Color.DKGRAY)
            emptyText.setPadding(16, 16, 16, 16)
            postsContainer.addView(emptyText)
            return
        }

        val inflater = LayoutInflater.from(this)

        // Display local posts.
        for (originalIndex in posts.indices.reversed()) {

            val post = posts[originalIndex]

            val postView = inflater.inflate(
                R.layout.item_post,
                postsContainer,
                false
            )

            val tvPostName =
                postView.findViewById<TextView>(R.id.tvPostName)

            val tvPostUsername =
                postView.findViewById<TextView>(R.id.tvPostUsername)

            val tvPostContent =
                postView.findViewById<TextView>(R.id.tvPostContent)

            val ivPostImage =
                postView.findViewById<ImageView>(R.id.ivPostImage)

            val btnLike =
                postView.findViewById<Button>(R.id.btnLike)

            val btnComment =
                postView.findViewById<Button>(R.id.btnComment)

            val btnDeletePost =
                postView.findViewById<Button>(R.id.btnDeletePost)

            val btnEditPost =
                postView.findViewById<Button>(R.id.btnEditPost)

            val commentsContainer =
                postView.findViewById<LinearLayout>(R.id.commentsContainer)

            val commentCount = commentStorage.getCommentCount(originalIndex)

            btnComment.text = "💬 Comment ($commentCount)"

            val savedComments = commentStorage.getComments(originalIndex)

            commentsContainer.removeAllViews()

            for (comment in savedComments) {
                val commentText = TextView(this)
                commentText.text = "Lindokuhle: $comment"
                commentText.textSize = 14f
                commentText.setTextColor(Color.DKGRAY)
                commentText.setPadding(12, 8, 12, 8)
                commentsContainer.addView(commentText)
            }

            tvPostName.text = "Lindokuhle Maseko"
            tvPostUsername.text = "@lindokuhle"
            tvPostContent.text = post.content

            if (!post.imageUri.isNullOrEmpty()) {
                try {
                    ivPostImage.setImageURI(Uri.parse(post.imageUri))
                    ivPostImage.visibility = ImageView.VISIBLE
                } catch (e: SecurityException) {
                    ivPostImage.setImageDrawable(null)
                    ivPostImage.visibility = ImageView.GONE
                }
            } else {
                ivPostImage.visibility = ImageView.GONE
            }

            // Local likes.
            updateLikeButton(btnLike, originalIndex)

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

                updateLikeButton(btnLike, originalIndex)
            }

            // Local comments.
            btnComment.setOnClickListener {
                showCommentDialog(originalIndex)
            }

            // Delete a local post.
            btnDeletePost.setOnClickListener {

                AlertDialog.Builder(this)
                    .setTitle("Delete Post")
                    .setMessage("Are you sure you want to delete this post?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete") { _, _ ->

                        postStorage.deletePost(originalIndex)
                        loadPosts()

                        Toast.makeText(
                            this,
                            "Post deleted successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .show()
            }

            // Edit a local post.
            btnEditPost.setOnClickListener {
                showEditPostDialog(originalIndex, post.content)
            }

            postsContainer.addView(postView)
        }
    }

    // Listen for Firebase cloud posts.
    private fun listenForCloudPosts() {

        postsListener = FirebaseFirestore.getInstance()
            .collection("posts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Toast.makeText(
                        this,
                        "Could not load cloud posts: ${error.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addSnapshotListener
                }

                cloudPosts = snapshot?.documents ?: emptyList()
                loadPosts()
            }
    }

    // Display cloud posts, likes and comments.
    private fun displayCloudPosts() {

        cloudLikeListeners.forEach { it.remove() }
        cloudLikeListeners.clear()

        cloudCommentListeners.forEach { it.remove() }
        cloudCommentListeners.clear()

        val inflater = LayoutInflater.from(this)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        for (document in cloudPosts) {

            val postView = inflater.inflate(
                R.layout.item_post,
                postsContainer,
                false
            )

            val tvPostName =
                postView.findViewById<TextView>(R.id.tvPostName)

            val tvPostUsername =
                postView.findViewById<TextView>(R.id.tvPostUsername)

            val tvPostContent =
                postView.findViewById<TextView>(R.id.tvPostContent)

            val ivPostImage =
                postView.findViewById<ImageView>(R.id.ivPostImage)

            val btnLike =
                postView.findViewById<Button>(R.id.btnLike)

            val btnComment =
                postView.findViewById<Button>(R.id.btnComment)

            val btnDeletePost =
                postView.findViewById<Button>(R.id.btnDeletePost)

            val btnEditPost =
                postView.findViewById<Button>(R.id.btnEditPost)

            val commentsContainer =
                postView.findViewById<LinearLayout>(R.id.commentsContainer)

            val fullName = document.getString("fullName") ?: "Circle User"
            val username = document.getString("username") ?: "user"
            val content = document.getString("content") ?: ""
            val postOwnerId = document.getString("userId") ?: ""

            tvPostName.text = fullName
            tvPostUsername.text = "@$username"
            tvPostContent.text = content

            // Cloud photos will be connected separately.
            ivPostImage.visibility = ImageView.GONE

            btnLike.visibility = Button.VISIBLE
            btnComment.visibility = Button.VISIBLE
            commentsContainer.visibility = LinearLayout.VISIBLE

            // Only the owner can see Edit and Delete.
            if (postOwnerId == currentUserId && currentUserId != null) {
                btnDeletePost.visibility = Button.VISIBLE
                btnEditPost.visibility = Button.VISIBLE
            } else {
                btnDeletePost.visibility = Button.GONE
                btnEditPost.visibility = Button.GONE
            }

            // Reference to this post's likes.
            val likesRef = db.collection("posts")
                .document(document.id)
                .collection("likes")

            // Listen for like changes.
            val likeListener = likesRef.addSnapshotListener { snapshot, error ->

                if (error != null) return@addSnapshotListener

                val likesCount = snapshot?.size() ?: 0

                val isLiked = currentUserId != null &&
                        snapshot?.documents?.any { it.id == currentUserId } == true

                btnLike.text = if (isLiked) {
                    "♥ Liked ($likesCount)"
                } else {
                    "♡ Like ($likesCount)"
                }
            }

            cloudLikeListeners.add(likeListener)

            // Like or unlike a cloud post.
            btnLike.setOnClickListener {

                val userId = FirebaseAuth.getInstance().currentUser?.uid

                if (userId == null) {
                    Toast.makeText(
                        this,
                        "Please log in to like posts.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                btnLike.isEnabled = false

                val userLikeRef = likesRef.document(userId)

                userLikeRef.get()
                    .addOnSuccessListener { likeDocument ->

                        if (likeDocument.exists()) {

                            userLikeRef.delete()
                                .addOnSuccessListener {
                                    btnLike.isEnabled = true
                                }
                                .addOnFailureListener { error ->
                                    btnLike.isEnabled = true
                                    Toast.makeText(
                                        this,
                                        "Could not remove like: ${error.localizedMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                        } else {

                            val likeData = hashMapOf(
                                "userId" to userId,
                                "createdAt" to FieldValue.serverTimestamp()
                            )

                            userLikeRef.set(likeData)
                                .addOnSuccessListener {
                                    btnLike.isEnabled = true
                                }
                                .addOnFailureListener { error ->
                                    btnLike.isEnabled = true
                                    Toast.makeText(
                                        this,
                                        "Could not add like: ${error.localizedMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                    }
                    .addOnFailureListener { error ->
                        btnLike.isEnabled = true
                        Toast.makeText(
                            this,
                            "Could not check like: ${error.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }

            // Reference to this post's comments.
            val commentsRef = db.collection("posts")
                .document(document.id)
                .collection("comments")

            // Listen for new comments.
            val commentListener = commentsRef
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        Toast.makeText(
                            this,
                            "Could not load comments: ${error.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                        return@addSnapshotListener
                    }

                    commentsContainer.removeAllViews()

                    val commentDocuments = snapshot?.documents ?: emptyList()

                    btnComment.text = "💬 Comment (${commentDocuments.size})"

                    for (commentDocument in commentDocuments) {

                        val commenterName =
                            commentDocument.getString("fullName") ?: "Circle User"

                        val commentText =
                            commentDocument.getString("content") ?: ""

                        val commentView = TextView(this).apply {
                            text = "$commenterName: $commentText"
                            textSize = 14f
                            setPadding(12, 8, 12, 8)
                        }

                        commentsContainer.addView(commentView)
                    }
                }

            cloudCommentListeners.add(commentListener)

            // Open the cloud comment dialog.
            btnComment.setOnClickListener {
                showCloudCommentDialog(document.id)
            }

            // Delete a cloud post.
            btnDeletePost.setOnClickListener {

                AlertDialog.Builder(this)
                    .setTitle("Delete Post")
                    .setMessage("Are you sure you want to delete this post?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete") { _, _ ->

                        db.collection("posts")
                            .document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Post deleted successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { error ->
                                Toast.makeText(
                                    this,
                                    "Could not delete post: ${error.localizedMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                    .show()
            }

            // Edit a cloud post.
            btnEditPost.setOnClickListener {
                showCloudEditPostDialog(document.id, content)
            }

            postsContainer.addView(postView)
        }
    }

    // Add a cloud comment.
    private fun showCloudCommentDialog(postId: String) {

        val commentInput = EditText(this)
        commentInput.hint = "Write a comment..."
        commentInput.setPadding(24, 16, 24, 16)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Comment")
            .setView(commentInput)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Post", null)
            .create()

        dialog.setOnShowListener {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {

                    val comment = commentInput.text.toString().trim()

                    if (comment.isEmpty()) {
                        commentInput.error = "Please write a comment"
                        return@setOnClickListener
                    }

                    val user = FirebaseAuth.getInstance().currentUser

                    if (user == null) {
                        Toast.makeText(
                            this,
                            "Please log in to comment.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val db = FirebaseFirestore.getInstance()

                    db.collection("users")
                        .document(user.uid)
                        .get()
                        .addOnSuccessListener { userDocument ->

                            val commentData = hashMapOf(
                                "userId" to user.uid,
                                "fullName" to (
                                        userDocument.getString("fullName") ?: "Circle User"
                                        ),
                                "username" to (
                                        userDocument.getString("username") ?: "user"
                                        ),
                                "content" to comment,
                                "createdAt" to FieldValue.serverTimestamp()
                            )

                            db.collection("posts")
                                .document(postId)
                                .collection("comments")
                                .add(commentData)
                                .addOnSuccessListener {

                                    Toast.makeText(
                                        this,
                                        "Comment added successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    dialog.dismiss()
                                }
                                .addOnFailureListener { error ->
                                    Toast.makeText(
                                        this,
                                        "Could not add comment: ${error.localizedMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                        .addOnFailureListener { error ->
                            Toast.makeText(
                                this,
                                "Could not retrieve your profile: ${error.localizedMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
        }

        dialog.show()
    }

    // Edit a cloud post.
    private fun showCloudEditPostDialog(
        postId: String,
        currentContent: String
    ) {

        val editInput = EditText(this)
        editInput.setText(currentContent)
        editInput.setSelection(editInput.text.length)
        editInput.setPadding(24, 16, 24, 16)
        editInput.hint = "Edit your post..."

        AlertDialog.Builder(this)
            .setTitle("Edit Post")
            .setView(editInput)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->

                val updatedContent = editInput.text.toString().trim()

                if (updatedContent.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Post cannot be empty.",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    FirebaseFirestore.getInstance()
                        .collection("posts")
                        .document(postId)
                        .update("content", updatedContent)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Post updated successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { error ->
                            Toast.makeText(
                                this,
                                "Could not update post: ${error.localizedMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
            .show()
    }

    // Update local like button.
    private fun updateLikeButton(button: Button, postIndex: Int) {

        val likes = likeStorage.getLikes(postIndex)
        val liked = likeStorage.isLiked(postIndex)

        button.text = if (liked) {
            "♥ Liked ($likes)"
        } else {
            "♡ Like ($likes)"
        }
    }

    // Add a local comment.
    private fun showCommentDialog(postIndex: Int) {

        val commentInput = EditText(this)
        commentInput.hint = "Write a comment..."
        commentInput.setPadding(24, 16, 24, 16)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Comment")
            .setView(commentInput)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Post") { _, _ ->

                val comment = commentInput.text.toString().trim()

                if (comment.isNotEmpty()) {

                    commentStorage.saveComment(postIndex, comment)

                    notificationStorage.addNotification(
                        "Lindokuhle commented on your post.",
                        "comment"
                    )

                    loadPosts()

                    Toast.makeText(
                        this,
                        "Comment added successfully!",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "Please write a comment",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .create()

        dialog.show()
    }

    // Edit a local post.
    private fun showEditPostDialog(
        postIndex: Int,
        currentContent: String
    ) {

        val editText = EditText(this)

        editText.setText(currentContent)
        editText.setSelection(editText.text.length)
        editText.setPadding(24, 16, 24, 16)

        AlertDialog.Builder(this)
            .setTitle("Edit Post")
            .setView(editText)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->

                val newContent = editText.text.toString().trim()

                if (newContent.isNotEmpty()) {

                    postStorage.updatePost(postIndex, newContent)
                    loadPosts()

                    Toast.makeText(
                        this,
                        "Post updated successfully!",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "Post cannot be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }

    // Remove Firestore listeners when the activity closes.
    override fun onDestroy() {

        postsListener?.remove()

        cloudLikeListeners.forEach { it.remove() }
        cloudLikeListeners.clear()

        cloudCommentListeners.forEach { it.remove() }
        cloudCommentListeners.clear()

        super.onDestroy()
    }

    // Load inspiration from an external REST API.
    private fun loadDailyInspiration() {

        val quoteText = findViewById<TextView>(R.id.tvInspirationQuote)
        val authorText = findViewById<TextView>(R.id.tvInspirationAuthor)

        quoteText.text = "Loading inspiration..."
        authorText.text = ""

        Thread {

            var connection: HttpURLConnection? = null

            try {
                val url = URL("https://dummyjson.com/quotes/random")
                connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    val response = connection.inputStream.bufferedReader().use {
                        it.readText()
                    }

                    val jsonObject = JSONObject(response)

                    val quote = jsonObject.getString("quote")
                    val author = jsonObject.getString("author")

                    runOnUiThread {
                        quoteText.text = "“$quote”"
                        authorText.text = "— $author"
                    }

                } else {
                    throw Exception("Server response: $responseCode")
                }

            } catch (e: Exception) {

                runOnUiThread {
                    quoteText.text = "Unable to load inspiration. Please try again."
                    authorText.text = ""
                }

            } finally {
                connection?.disconnect()
            }

        }.start()
    }

}
