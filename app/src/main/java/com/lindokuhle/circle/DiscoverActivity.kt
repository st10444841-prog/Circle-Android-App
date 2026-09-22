package com.lindokuhle.circle

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.lindokuhle.circle.utils.FollowingStorage
import com.lindokuhle.circle.utils.NotificationStorage

class DiscoverActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText

    private lateinit var btnFollowThabo: Button
    private lateinit var btnFollowLebo: Button
    private lateinit var btnFollowSipho: Button

    private lateinit var thaboContainer: LinearLayout
    private lateinit var leboContainer: LinearLayout
    private lateinit var siphoContainer: LinearLayout

    private lateinit var followingStorage: FollowingStorage
    private lateinit var notificationStorage: NotificationStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover)

        etSearch = findViewById(R.id.etSearch)

        btnFollowThabo = findViewById(R.id.btnFollowThabo)
        btnFollowLebo = findViewById(R.id.btnFollowLebo)
        btnFollowSipho = findViewById(R.id.btnFollowSipho)

        thaboContainer = btnFollowThabo.parent as LinearLayout
        leboContainer = btnFollowLebo.parent as LinearLayout
        siphoContainer = btnFollowSipho.parent as LinearLayout

        followingStorage = FollowingStorage(this)
        notificationStorage = NotificationStorage(this)

        loadFollowingStatus()

        btnFollowThabo.setOnClickListener {
            toggleFollow("Thabo M.", btnFollowThabo)
        }

        btnFollowLebo.setOnClickListener {
            toggleFollow("Lebo K.", btnFollowLebo)
        }

        btnFollowSipho.setOnClickListener {
            toggleFollow("Sipho N.", btnFollowSipho)
        }

        etSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                filterPeople(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun loadFollowingStatus() {

        if (followingStorage.isFollowing("Thabo M.")) {
            btnFollowThabo.text = "Following"
        } else {
            btnFollowThabo.text = "Follow"
        }

        if (followingStorage.isFollowing("Lebo K.")) {
            btnFollowLebo.text = "Following"
        } else {
            btnFollowLebo.text = "Follow"
        }

        if (followingStorage.isFollowing("Sipho N.")) {
            btnFollowSipho.text = "Following"
        } else {
            btnFollowSipho.text = "Follow"
        }
    }

    private fun toggleFollow(
        person: String,
        button: Button
    ) {

        if (followingStorage.isFollowing(person)) {

            followingStorage.setFollowing(
                person,
                false
            )

            followingStorage.decreaseFollowing()

            button.text = "Follow"

        } else {

            followingStorage.setFollowing(
                person,
                true
            )

            followingStorage.increaseFollowing()

            notificationStorage.addNotification(
                "You are now following $person.",
                "follow"
            )

            button.text = "Following"
        }
    }

    private fun filterPeople(searchText: String) {

        val search = searchText.trim().lowercase()

        val showThabo =
            search.isEmpty() || "thabo m.".contains(search)

        val showLebo =
            search.isEmpty() || "lebo k.".contains(search)

        val showSipho =
            search.isEmpty() || "sipho n.".contains(search)

        thaboContainer.visibility =
            if (showThabo) View.VISIBLE else View.GONE

        leboContainer.visibility =
            if (showLebo) View.VISIBLE else View.GONE

        siphoContainer.visibility =
            if (showSipho) View.VISIBLE else View.GONE
    }
}