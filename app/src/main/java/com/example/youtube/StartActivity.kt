package com.example.youtube

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        goto_login.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        })
        phone_login.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, PhoneLogin::class.java)
            startActivity(intent)
        })
    }

    override fun onStart() {
        super.onStart()
        var user = FirebaseAuth.getInstance().currentUser;

        if (user!=null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}