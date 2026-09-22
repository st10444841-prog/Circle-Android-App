package com.lindokuhle.circle

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.lindokuhle.circle.utils.PostStorage

class CreatePostActivity : AppCompatActivity() {

    private lateinit var etPostContent: EditText
    private lateinit var btnAddPhoto: Button
    private lateinit var btnCreatePost: Button
    private lateinit var ivSelectedPhoto: ImageView

    private lateinit var postStorage: PostStorage

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
                // Some providers may not support persistent permissions.
            }

            selectedImageUri = uri

            ivSelectedPhoto.setImageURI(uri)
            ivSelectedPhoto.visibility = ImageView.VISIBLE

            Toast.makeText(
                this,
                "Photo selected successfully!",
                Toast.LENGTH_SHORT
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

        postStorage = PostStorage(this)

        btnAddPhoto.setOnClickListener {
            openImagePicker()
        }

        btnCreatePost.setOnClickListener {
            createPost()
        }
    }

    private fun openImagePicker() {

        imagePicker.launch(
            arrayOf("image/*")
        )
    }

    private fun createPost() {

        val postContent = etPostContent.text.toString().trim()

        if (postContent.isEmpty()) {
            etPostContent.error = "Please write something before publishing"
            etPostContent.requestFocus()
            return
        }

        postStorage.savePost(
            postContent,
            selectedImageUri?.toString()
        )

        Toast.makeText(
            this,
            "Post published successfully!",
            Toast.LENGTH_SHORT
        ).show()

        etPostContent.text.clear()

        selectedImageUri = null
        ivSelectedPhoto.setImageDrawable(null)
        ivSelectedPhoto.visibility = ImageView.GONE
    }
}