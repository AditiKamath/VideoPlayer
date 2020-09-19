package com.example.youtube.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.youtube.R
import com.example.youtube.RegisterActivity
import com.example.youtube.VerifyPhone
import com.example.youtube.toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {

private val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentUser?.let { user->
                user_name.setText(user.displayName)
               user_email.text = user.email
            user_phone.text = if(user.phoneNumber.isNullOrEmpty()) "Please Add Number" else user.phoneNumber
            if(user.isEmailVerified){
                not_verified.visibility = View.INVISIBLE
            }else{
                not_verified.visibility = View.VISIBLE
            }
        }
        save_changes.setOnClickListener(View.OnClickListener {
            val name = user_name.text.toString().trim()
            if(name.isEmpty()){
                user_name.error = "name required"
                user_name.requestFocus()
                return@OnClickListener
            }
            val update = UserProfileChangeRequest.Builder().setDisplayName(name).build()
            progress_bar.visibility = View.VISIBLE
            currentUser?.updateProfile(update)?.addOnCompleteListener(){    task->
                progress_bar.visibility = View.INVISIBLE
                if(task.isSuccessful){
                    context?.toast("Profile Updated")
                }else{
                    context?.toast(task.exception?.message!!)
                }

            }

        })
        not_verified.setOnClickListener(View.OnClickListener {
                currentUser?.sendEmailVerification()?.addOnCompleteListener(OnCompleteListener {
                    if(it.isSuccessful){
                        context?.toast("Verification email sent")
                    }else{
                        context?.toast(it.exception?.message!!)
                    }
                })
        })
        user_phone.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ProfileFragment.context, VerifyPhone::class.java)
            startActivity(intent)
        })
    }



}