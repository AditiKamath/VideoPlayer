package com.example.youtube

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()
        val signIn_msg: TextView = findViewById(R.id.signIn_msg)
        signIn_msg.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        })

        register_button.setOnClickListener(View.OnClickListener {
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

            registerUser(email, password)
        })
    }

    private fun registerUser(email: String, password: String) {
        progress_bar.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            progress_bar.visibility = View.GONE
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