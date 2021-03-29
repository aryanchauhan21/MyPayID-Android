package com.leotarius.mypayid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.leotarius.mypayid.Utils.UtilFunctions
import kotlinx.android.synthetic.main.card_element.*
import kotlinx.android.synthetic.main.login.*

class LoginActivity: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        auth = FirebaseAuth.getInstance()

        register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if(UtilFunctions.isValidEmail(email)){
                error.visibility = View.GONE

                // now log the user in
                logUserIn(email, password)

            } else {
                error.visibility = View.VISIBLE
            }
        }
    }

    private fun logUserIn(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    // redirect user to home screen
                    if(auth.currentUser?.isEmailVerified!!){
                        startActivity(Intent(baseContext, MainActivity::class.java))
                    } else{
                        startActivity(Intent(baseContext, VerifyEmailActivity::class.java))
                    }

                    // finish this activity so user cant press back button to get here without logout
                    finish()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Wrong Email/Password combination", Toast.LENGTH_SHORT).show()
                }
        }
    }

}