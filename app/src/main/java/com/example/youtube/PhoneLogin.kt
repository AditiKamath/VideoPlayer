package com.example.youtube

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_phone_login.*
import kotlinx.android.synthetic.main.activity_verify_phone.*
import kotlinx.android.synthetic.main.activity_verify_phone.code_edit_text
import kotlinx.android.synthetic.main.activity_verify_phone.cpp
import kotlinx.android.synthetic.main.activity_verify_phone.phone_edit_text
import kotlinx.android.synthetic.main.activity_verify_phone.phone_layout
import kotlinx.android.synthetic.main.activity_verify_phone.send_code
import kotlinx.android.synthetic.main.activity_verify_phone.verification_layout
import java.util.concurrent.TimeUnit

class PhoneLogin : AppCompatActivity() {
    private lateinit var mAuth : FirebaseAuth
    private lateinit var phoneCallbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)
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
        phoneCallbacks = object :   PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signIn(credential);
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                if (p0 != null) {
                    if (p1 != null) {
                        super.onCodeSent(p0, p1)
                    }
                }
                verificationId = p0.toString()
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun signIn(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener{
                task: Task<AuthResult> ->
        if (task.isSuccessful) {
           Toast.makeText(this,"Logged in Successfully",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,HomeActivity::class.java))
        }
    }
    }
    private fun authenticate () {

        val verifiNo = code_edit_text.text.toString()

        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, verifiNo)

        signIn(credential)

    }


}