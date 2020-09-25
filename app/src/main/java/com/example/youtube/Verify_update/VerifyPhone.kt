package com.example.youtube.Verify_update

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.youtube.R
import com.example.youtube.fragment.ProfileFragment
import com.example.youtube.toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_verify_phone.*
import java.util.concurrent.TimeUnit

class VerifyPhone : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private var verificationId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)
        mAuth = FirebaseAuth.getInstance()
        phone_layout.visibility = View.VISIBLE
        verification_layout.visibility = View.INVISIBLE
        send_code.setOnClickListener(View.OnClickListener {
            val phone = phone_edit_text.text.toString().trim()
            if (phone.isEmpty() || phone.length != 10) {
                phone_edit_text.error = "Phone Number required"
                phone_edit_text.requestFocus()
                return@OnClickListener
            }
            val phoneNumber = "+" + cpp.selectedCountryCode + phone
            PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, phoneCallbacks)

            phone_layout.visibility = View.INVISIBLE
            verification_layout.visibility = View.VISIBLE


        })
        verify_button.setOnClickListener(View.OnClickListener {
            val code = code_edit_text.text.toString().trim()
            if (code.isEmpty()) {
                code_edit_text.error = "Code required"
                code_edit_text.requestFocus()
                return@OnClickListener
            }
            verificationId?.let {
                val credential = PhoneAuthProvider.getCredential(it, code)
                addPhoneNumber(credential)
            }
        })
    }


    private val phoneCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            phoneAuthCredential?.let {
                addPhoneNumber(it)
            }
        }

        override fun onVerificationFailed(exception: FirebaseException) {
            applicationContext?.toast(exception?.message!!)
        }

    }


    fun onCodeSent(token: PhoneAuthProvider.ForceResendingToken?, verificationId: String?) {

        // Save verification ID and resending token so we can use them later
        //     super.onCodeSent(verificationId,token)
        this.verificationId = verificationId

        // ...
    }


    private fun addPhoneNumber(phoneAuthCredential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().currentUser?.updatePhoneNumber(phoneAuthCredential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    applicationContext?.toast("Phone Number Added")
                    val intent = Intent(this, ProfileFragment::class.java)
                    startActivity(intent)
                } else {
                    applicationContext?.toast(task.exception?.message!!)
                }
            }
    }

}


