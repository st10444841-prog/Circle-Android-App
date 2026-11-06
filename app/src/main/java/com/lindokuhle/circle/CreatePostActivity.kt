
package com.lindokuhle.circle

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CreatePostActivity : AppCompatActivity() {

    private lateinit var etPostContent: EditText
    private lateinit var btnAddPhoto: Button
    private lateinit var btnCreatePost: Button
    private lateinit var ivSelectedPhoto: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var selectedImageUri: Uri? = null

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                // Some providers do not support persistent permissions.
            }

            selectedImageUri = uri
            ivSelectedPhoto.setImageURI(uri)
            ivSelectedPhoto.visibility = ImageView.VISIBLE

            Toast.makeText(
                this,
                "Photo selected. Cloud photo sharing is not set up yet.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        etPostContent = findViewById(R.id.etPostContent)
        btnAddPhoto = findViewById(R.id.btnAddPhoto)
        btnCreatePost = findViewById(R.id.btnCreatePost)
        ivSelectedPhoto = findViewById(R.id.ivSelectedPhoto)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btnAddPhoto.setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

        btnCreatePost.setOnClickListener {
            createPost()
        }
    }

    private fun createPost() {
        val postContent = etPostContent.text.toString().trim()

        if (postContent.isEmpty()) {
            etPostContent.error = "Please write something before publishing"
            etPostContent.requestFocus()
            return
        }

        if (selectedImageUri != null) {
            Toast.makeText(
                this,
                "For now, please publish a text-only post. Cloud photo sharing will be added separately.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(
                this,
                "Please log in before creating a post.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        btnCreatePost.isEnabled = false

        db.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {
                    btnCreatePost.isEnabled = true
                    Toast.makeText(
                        this,
                        "Your user profile could not be found.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnSuccessListener
                }

                val fullName = document.getString("fullName") ?: "Circle User"
                val username = document.getString("username") ?: "user"

                val post = hashMapOf(
                    "userId" to currentUser.uid,
                    "fullName" to fullName,
                    "username" to username,
                    "content" to postContent,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "likesCount" to 0
                )

                db.collection("posts")
                    .add(post)
                    .addOnSuccessListener {
                        btnCreatePost.isEnabled = true

                        etPostContent.text.clear()
                        selectedImageUri = null
                        ivSelectedPhoto.setImageDrawable(null)
                        ivSelectedPhoto.visibility = ImageView.GONE

                        Toast.makeText(
                            this,
                            "Post successfully saved to Firebase!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .addOnFailureListener { error ->
                        btnCreatePost.isEnabled = true

                        Toast.makeText(
                            this,
                            "Could not save post: ${error.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener { error ->
                btnCreatePost.isEnabled = true

                Toast.makeText(
                    this,
                    "Could not load your profile: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}
