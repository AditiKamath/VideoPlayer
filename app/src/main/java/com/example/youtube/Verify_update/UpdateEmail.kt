package com.example.youtube.Verify_update

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.example.youtube.R
import com.example.youtube.StartActivity
import com.example.youtube.fragment.ProfileFragment
import com.example.youtube.toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_update_email.*

class UpdateEmail : AppCompatActivity() {
   private val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_email)
        layout_password.visibility = View.VISIBLE
        email_layout.visibility  = View.GONE
        button_authenticate.setOnClickListener{
            val password = update_email_password.text.toString().trim()
            if(password.isEmpty()){
                update_email_password.error = "Password required"
                update_email_password.requestFocus()
                return@setOnClickListener
            }
            progressbar.visibility = View.VISIBLE
            currentUser?.let {user->
                val credential  = EmailAuthProvider.getCredential(user.email!!,password)
                user.reauthenticate(credential).addOnCompleteListener{
                    task->
                    progressbar.visibility = View.GONE
                    if(task.isSuccessful){
                         layout_password.visibility = View.GONE
                        email_layout.visibility  = View.VISIBLE
                    }else if (task.exception is FirebaseAuthInvalidCredentialsException){
                        update_email_password.error = "Invalid Password"
                        update_email_password.requestFocus()
                    }else{
                        applicationContext?.toast(task.exception?.message!!)

                    }

                }
            }

        }
        button_update.setOnClickListener{
            val email  = user_email.text.toString().trim()
            if (email.isEmpty()) {
                user_email.error = "Email Required"
                user_email.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                user_email.error = "Valid Email Required"
                user_email.requestFocus()
              return@setOnClickListener
            }
            progressbar.visibility = View.VISIBLE
            currentUser?.let{
                user->
                user.updateEmail(email).addOnCompleteListener{
                   task->
                    progressbar.visibility = View.GONE
                    if(task.isSuccessful){
                        applicationContext.toast("Email has been updates")
                        val intent = Intent(this, ProfileFragment::class.java)
                        startActivity(intent)
                    }else{
                        applicationContext?.toast(task.exception?.message!!)
                    }
                }
            }
        }
    }

}