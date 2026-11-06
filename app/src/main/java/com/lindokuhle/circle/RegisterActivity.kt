
package com.lindokuhle.circle

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etFullName = findViewById(R.id.etFullName)
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btnRegister.setOnClickListener {
            registerUser()
        }

        tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun registerUser() {

        val fullName = etFullName.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (fullName.isEmpty()) {
            etFullName.error = "Please enter your full name"
            etFullName.requestFocus()
            return
        }

        if (username.isEmpty()) {
            etUsername.error = "Please enter a username"
            etUsername.requestFocus()
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Please enter your email"
            etEmail.requestFocus()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Please enter a valid email address"
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Please enter a password"
            etPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            etPassword.error = "Password must contain at least 6 characters"
            etPassword.requestFocus()
            return
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Please confirm your password"
            etConfirmPassword.requestFocus()
            return
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            etConfirmPassword.requestFocus()
            return
        }

        btnRegister.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val userId = auth.currentUser?.uid

                    if (userId == null) {
                        btnRegister.isEnabled = true
                        Toast.makeText(
                            this,
                            "Could not retrieve user details.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@addOnCompleteListener
                    }

                    val userProfile = hashMapOf(
                        "userId" to userId,
                        "fullName" to fullName,
                        "username" to username,
                        "email" to email,
                        "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                    )

                    db.collection("users")
                        .document(userId)
                        .set(userProfile)
                        .addOnSuccessListener {

                            Toast.makeText(
                                this,
                                "Registration successful!",
                                Toast.LENGTH_SHORT
                            ).show()

                            auth.signOut()

                            startActivity(
                                Intent(this, LoginActivity::class.java)
                            )
                            finish()
                        }
                        .addOnFailureListener { error ->

                            btnRegister.isEnabled = true

                            Toast.makeText(
                                this,
                                "Account created, but profile saving failed: ${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                } else {

                    btnRegister.isEnabled = true

                    Toast.makeText(
                        this,
                        task.exception?.localizedMessage
                            ?: "Registration failed.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
