package com.example.youtube.Verify_update

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.youtube.R
import com.example.youtube.toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_password_reset.*


class PasswordResetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)
        button_reset.setOnClickListener{
            val email = edit_text_email.text.toString().trim()
            if(email.isEmpty()){
                edit_text_email.error = "Enter valid email"
                edit_text_email.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edit_text_email.error = "Valid Email Required"
                edit_text_email.requestFocus()
               return@setOnClickListener
            }
            progress_bar.visibility = View.VISIBLE
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener{
                task->
                progress_bar.visibility = View.GONE
                if(task.isSuccessful) {
                    this.toast("Check your email ")
                }else{
                    this.toast(task.exception?.message!!)
                }
            }
        }
    }
}