package com.example.youtube.Verify_update

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.youtube.R
import com.example.youtube.fragment.ProfileFragment
import com.example.youtube.toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePassword : AppCompatActivity() {
    private val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        layout_change_password.visibility = View.VISIBLE
        layout_new_password.visibility  = View.GONE
        button_authenticate.setOnClickListener{
            val password = password_change.text.toString().trim()
            if(password.isEmpty()){
                password_change.error = "Password required"
                password_change.requestFocus()
                return@setOnClickListener
            }
            progressbar.visibility = View.VISIBLE
            currentUser?.let {user->
                val credential  = EmailAuthProvider.getCredential(user.email!!,password)
                user.reauthenticate(credential).addOnCompleteListener{
                        task->
                    progressbar.visibility = View.GONE
                    if(task.isSuccessful){
                        layout_change_password.visibility = View.GONE
                        layout_new_password.visibility  = View.VISIBLE
                    }else if (task.exception is FirebaseAuthInvalidCredentialsException){
                        password_change.error = "Invalid Password"
                        password_change.requestFocus()
                    }else{
                        applicationContext?.toast(task.exception?.message!!)

                    }

                }
            }
        }
        button_update_password.setOnClickListener{
            val password = new_password_edit_text.text.toString().trim()
            if(password.isEmpty() || password.length<6){
                new_password_edit_text.error="Atleast 6 char password required"
                new_password_edit_text.requestFocus()
                return@setOnClickListener
            }
            if(password !=confirm_password_edit_text.text.toString().trim()){
                confirm_password_edit_text.error="Password didn't match"
                confirm_password_edit_text.requestFocus()
                return@setOnClickListener
            }
            currentUser?.let {user->
                progressbar.visibility = View.VISIBLE
                user.updatePassword(password).addOnCompleteListener{
                    task->
                    progressbar.visibility = View.GONE
                    if(task.isSuccessful){
                        applicationContext.toast("Password updated")
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