package com.example.youtube

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.youtube.Verify_update.PasswordResetActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login2.*


class Login : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
        val register_msg = findViewById<TextView>(R.id.register_msg)
        // intent to open new activity from the fragment
        register_msg.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        })
        mAuth = FirebaseAuth.getInstance()
        login_button.setOnClickListener(View.OnClickListener {
            val email = edit_text_email.text.toString().trim()
            val password = edit_text_password.text.toString().trim()
            if (email.isEmpty()) {
                edit_text_email.error = "Email Required"
                edit_text_email.requestFocus()
                return@OnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edit_text_email.error = "Valid Email Required"
                edit_text_email.requestFocus()
                return@OnClickListener
            }
            if (password.isEmpty() || password.length < 6) {
                edit_text_password.error = "6 char password req"
                edit_text_password.requestFocus()
                return@OnClickListener
            }
            loginUser(email, password)


        })
    forgot_password.setOnClickListener{
        val intent = Intent(this, PasswordResetActivity::class.java)
        startActivity(intent)
    }
    }

    private fun loginUser(email: String, password: String) {
        progress_bar.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            progress_bar.visibility = View.INVISIBLE
            if (task.isSuccessful) {
                login()
            } else {
                task.exception?.message.let {
                    if (it != null) {
                        toast(it)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.currentUser?.let { login() }
    }

}