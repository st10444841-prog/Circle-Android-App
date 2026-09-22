package com.lindokuhle.circle

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lindokuhle.circle.utils.FriendsStorage
import com.lindokuhle.circle.utils.FollowingStorage
import com.lindokuhle.circle.utils.PostStorage
import com.lindokuhle.circle.utils.ProfileStorage

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileUsername: TextView
    private lateinit var tvPostCount: TextView
    private lateinit var tvFriendCount: TextView
    private lateinit var tvFollowingCount: TextView

    private lateinit var btnEditProfile: Button
    private lateinit var btnSettings: Button
    private lateinit var btnLogout: Button

    private lateinit var profileStorage: ProfileStorage
    private lateinit var postStorage: PostStorage
    private lateinit var friendsStorage: FriendsStorage
    private lateinit var followingStorage: FollowingStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvProfileName = findViewById(R.id.tvProfileName)
        tvProfileUsername = findViewById(R.id.tvProfileUsername)
        tvPostCount = findViewById(R.id.tvPostCount)
        tvFriendCount = findViewById(R.id.tvFriendCount)
        tvFollowingCount = findViewById(R.id.tvFollowingCount)

        btnEditProfile = findViewById(R.id.btnEditProfile)
        btnSettings = findViewById(R.id.btnSettings)
        btnLogout = findViewById(R.id.btnLogout)

        profileStorage = ProfileStorage(this)
        postStorage = PostStorage(this)
        friendsStorage = FriendsStorage(this)
        followingStorage = FollowingStorage(this)

        loadProfile()

        btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun loadProfile() {

        val name = profileStorage.getName()
        val username = profileStorage.getUsername()
        val postCount = postStorage.getPostCount()
        val friendCount = friendsStorage.getFriendCount()
        val followingCount = followingStorage.getFollowingCount()

        tvProfileName.text = name
        tvProfileUsername.text = "@$username"
        tvPostCount.text = postCount.toString()
        tvFriendCount.text = friendCount.toString()
        tvFollowingCount.text = followingCount.toString()
    }

    private fun logoutUser() {

        Toast.makeText(
            this,
            "You have been logged out.",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this, LoginActivity::class.java)

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}