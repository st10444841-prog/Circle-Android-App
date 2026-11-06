
package com.lindokuhle.circle

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FriendsActivity : AppCompatActivity() {

    private lateinit var etFriendSearch: EditText
    private lateinit var searchResultsContainer: LinearLayout
    private lateinit var incomingRequestsContainer: LinearLayout
    private lateinit var friendsContainer: LinearLayout

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        etFriendSearch = findViewById(R.id.etFriendSearch)
        searchResultsContainer = findViewById(R.id.searchResultsContainer)
        incomingRequestsContainer = findViewById(R.id.incomingRequestsContainer)
        friendsContainer = findViewById(R.id.friendsContainer)

        etFriendSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                val searchText = s.toString().trim()

                if (searchText.length >= 2) {
                    searchUsers(searchText)
                } else {
                    searchResultsContainer.removeAllViews()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        loadIncomingRequests()
        loadFriends()
    }

    override fun onResume() {
        super.onResume()
        loadIncomingRequests()
        loadFriends()
    }

    private fun searchUsers(searchText: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("users")
            .orderBy("username")
            .startAt(searchText)
            .endAt(searchText + "\uf8ff")
            .limit(20)
            .get()
            .addOnSuccessListener { documents ->

                searchResultsContainer.removeAllViews()

                val matchingUsers = documents.documents.filter {
                    it.id != currentUserId
                }

                if (matchingUsers.isEmpty()) {
                    addMessage(searchResultsContainer, "No users found.")
                    return@addOnSuccessListener
                }

                matchingUsers.forEach { user ->
                    val fullName = user.getString("fullName") ?: "Circle User"
                    val username = user.getString("username") ?: "user"

                    val row = createRow("@$username ($fullName)")
                    val addButton = Button(this).apply {
                        text = "Add Friend"
                    }

                    row.addView(addButton)

                    addButton.setOnClickListener {
                        sendFriendRequest(user)
                        addButton.isEnabled = false
                        addButton.text = "Sent"
                    }

                    searchResultsContainer.addView(row)
                }
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this,
                    "Search failed: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }


    private fun sendFriendRequest(targetUser: DocumentSnapshot) {
        val currentUser = auth.currentUser ?: return

        val targetUserId = targetUser.id

        if (targetUserId == currentUser.uid) return

        db.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { myProfile ->

                if (!myProfile.exists()) {
                    Toast.makeText(
                        this,
                        "Your profile could not be found.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnSuccessListener
                }

                val request = hashMapOf(
                    "fromUserId" to currentUser.uid,
                    "toUserId" to targetUserId,
                    "fromName" to (myProfile.getString("fullName")
                        ?: "Circle User"),
                    "fromUsername" to (myProfile.getString("username")
                        ?: "user"),
                    "toName" to (targetUser.getString("fullName")
                        ?: "Circle User"),
                    "toUsername" to (targetUser.getString("username")
                        ?: "user"),
                    "status" to "pending",
                    "createdAt" to FieldValue.serverTimestamp()
                )

                db.collection("friendRequests")
                    .add(request)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Friend request saved successfully!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(
                            this,
                            "Could not send request: ${error.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this,
                    "Could not load your profile: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }


    private fun loadIncomingRequests() {
        val currentUserId = auth.currentUser?.uid ?: return

        incomingRequestsContainer.removeAllViews()

        db.collection("friendRequests")
            .whereEqualTo("toUserId", currentUserId)
            .get()
            .addOnSuccessListener { documents ->

                val pendingRequests = documents.documents.filter {
                    it.getString("status") == "pending"
                }

                if (pendingRequests.isEmpty()) {
                    addMessage(
                        incomingRequestsContainer,
                        "No pending friend requests."
                    )
                    return@addOnSuccessListener
                }

                pendingRequests.forEach { request ->

                    val name = request.getString("fromName")
                        ?: "Circle User"

                    val username = request.getString("fromUsername")
                        ?: "user"

                    val row = createRow("@$username ($name)")

                    val acceptButton = Button(this).apply {
                        text = "Accept"
                    }

                    val rejectButton = Button(this).apply {
                        text = "Reject"
                    }

                    row.addView(acceptButton)
                    row.addView(rejectButton)

                    acceptButton.setOnClickListener {
                        respondToRequest(request.id, "accepted")
                    }

                    rejectButton.setOnClickListener {
                        respondToRequest(request.id, "rejected")
                    }

                    incomingRequestsContainer.addView(row)
                }
            }
            .addOnFailureListener { error ->
                addMessage(
                    incomingRequestsContainer,
                    "Could not load requests: ${error.localizedMessage}"
                )
            }
    }

    private fun respondToRequest(requestId: String, response: String) {
        db.collection("friendRequests")
            .document(requestId)
            .update(
                mapOf(
                    "status" to response,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            .addOnSuccessListener {
                val message = if (response == "accepted") {
                    "Friend request accepted!"
                } else {
                    "Friend request rejected."
                }

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                loadIncomingRequests()
                loadFriends()
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this,
                    "Could not update request: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun loadFriends() {
        val currentUserId = auth.currentUser?.uid ?: return

        friendsContainer.removeAllViews()

        val friendDocuments = mutableMapOf<String, DocumentSnapshot>()
        var completedQueries = 0

        fun finishQuery(documents: List<DocumentSnapshot>) {
            documents.forEach { document ->
                if (document.getString("status") == "accepted") {
                    friendDocuments[document.id] = document
                }
            }

            completedQueries++

            if (completedQueries == 2) {
                renderFriends(friendDocuments.values.toList(), currentUserId)
            }
        }

        db.collection("friendRequests")
            .whereEqualTo("fromUserId", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                finishQuery(documents.documents)
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this,
                    "Could not load friends: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }

        db.collection("friendRequests")
            .whereEqualTo("toUserId", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                finishQuery(documents.documents)
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this,
                    "Could not load friends: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun renderFriends(
        requests: List<DocumentSnapshot>,
        currentUserId: String
    ) {
        friendsContainer.removeAllViews()

        if (requests.isEmpty()) {
            addMessage(friendsContainer, "You haven't added any friends yet.")
            return
        }

        requests.forEach { request ->

            val friendName: String
            val friendUsername: String

            if (request.getString("fromUserId") == currentUserId) {
                friendName = request.getString("toName") ?: "Circle User"
                friendUsername = request.getString("toUsername") ?: "user"
            } else {
                friendName = request.getString("fromName") ?: "Circle User"
                friendUsername = request.getString("fromUsername") ?: "user"
            }

            val row = createRow("@$friendUsername ($friendName)")

            val messageButton = Button(this).apply {
                text = "Message"
            }

            row.addView(messageButton)

            messageButton.setOnClickListener {
                openChat(friendName)
            }

            friendsContainer.addView(row)
        }
    }

    private fun createRow(label: String): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(12, 8, 12, 8)
            setBackgroundColor(android.graphics.Color.WHITE)
        }

        val nameView = TextView(this).apply {
            text = label
            textSize = 15f
            setTextColor(android.graphics.Color.rgb(31, 41, 55))
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        row.addView(nameView)

        row.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 12
        }

        return row
    }

    private fun addMessage(container: LinearLayout, message: String) {
        val textView = TextView(this).apply {
            text = message
            textSize = 14f
            setPadding(8, 8, 8, 16)
            setTextColor(android.graphics.Color.DKGRAY)
        }

        container.addView(textView)
    }

    private fun openChat(friendName: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("friendName", friendName)
        startActivity(intent)
    }
}
