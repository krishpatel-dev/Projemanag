package com.krishhh.projemanag.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.krishhh.projemanag.R
import com.krishhh.projemanag.databinding.ActivitySignInBinding
import com.krishhh.projemanag.firebase.FirestoreClass
import com.krishhh.projemanag.models.User

class SignInActivity : BaseActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        // Add click event for sign-in button and call the function to sign in.
        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }
    }

    // A function for actionBar Setup.
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarSignInActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarSignInActivity.setNavigationOnClickListener { onBackPressed() }
    }

    // A function for Sign-In using the registered user using the email and password.
    private fun signInRegisteredUser() {
        // Here we get the text from editText and trim the space
        val email: String = binding.etEmailSignin.text.toString().trim { it <= ' ' }
        val password: String = binding.etPasswordSignin.text.toString().trim { it <= ' ' }

        if (validateForm(email, password)) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Sign-In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                    // Calling the Firestore Class signInUser function to get the data of user from database.
                        FirestoreClass().loadUserData(this@SignInActivity)

                    } else {
                        hideProgressDialog()

                        Toast.makeText(
                            this@SignInActivity,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    // A function to validate the entries of a user.
    private fun validateForm(email: String, password: String): Boolean {
        return if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Please enter email.")
            false
        } else if (TextUtils.isEmpty(password)) {
            showErrorSnackBar("Please enter password.")
            false
        } else {
            true
        }
    }

    // A function to get the user details from the firestore database after authentication.
    fun signInSuccess(user: User) {

        hideProgressDialog()

        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        this.finish()
    }

}

