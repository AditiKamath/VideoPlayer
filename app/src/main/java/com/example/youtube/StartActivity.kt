package com.example.youtube

import android.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_start.*



class StartActivity : AppCompatActivity() {
    companion object{
       private const val RC_SIGN_IN =120
    }
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.youtube.R.layout.activity_start)
        //Initializing the firebase auth
        mAuth = FirebaseAuth.getInstance()

        goto_login.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        })
        phone_login.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, PhoneLogin::class.java)
            startActivity(intent)
        })
        val gso : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).let {
            it.requestIdToken("39938289362-ol8piec2r7h9gl5k84u5t6sreqltf2h3.apps.googleusercontent.com")
            it.requestEmail()
            it.build()
        }

        //Creating a google sign in client
         googleSignInClient = GoogleSignIn.getClient(this, gso)

        login_with_google.setOnClickListener{
           signIn()
        }


    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onStart() {
        super.onStart()
        var user = FirebaseAuth.getInstance().currentUser;
        if (user!=null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

                val task : Task<GoogleSignInAccount>? = GoogleSignIn.getSignedInAccountFromIntent(data)
                val exception = task?.exception
            if (task != null) {
                if(task.isSuccessful) {

                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        val account : GoogleSignInAccount = task.getResult(ApiException::class.java)!!
                        Log.d("Account id", "firebaseAuthWithGoogle:" + account.id)
                            firebaseAuthWithGoogle(account!!)
                    } catch (e: ApiException) {
                        // Google Sign In failed, update UI appropriately
                        Log.w("Error", "Google sign in failed", e)
                        // ...
                    }
                }else{
                    Log.w("Error", exception.toString())
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential : AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Success", "signInWithCredential:success")
                    val intent=Intent(this,HomeActivity::class.java)
                    startActivity(intent)
                     applicationContext.toast("Sign in success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Error", "signInWithCredential:failure", task.exception)
                    // ...
                    applicationContext.toast(task.exception?.message!!)

                }
                // ...
            }
    }

}