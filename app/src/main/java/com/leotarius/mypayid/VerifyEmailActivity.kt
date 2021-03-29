package com.leotarius.mypayid

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.leotarius.mypayid.Utils.UtilFunctions
import kotlinx.android.synthetic.main.activity_verify_email.*

class VerifyEmailActivity : AppCompatActivity(){

    var count = 0
    var mailSent = false
    private val TAG = "VerifyEmailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)

        send_email.setOnClickListener {
            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
            mailSent = true
            send_email.visibility = View.GONE
            logout.visibility = View.GONE

            // Set a lottie animation
            lottie_anim.visibility = View.VISIBLE
            waiting_text.visibility = View.VISIBLE
        }

        logout.setOnClickListener {
            logoutUser()
        }

        refresh.setOnClickListener {
            refresh.isEnabled = false
            refresh.rippleColor = ColorStateList.valueOf(resources.getColor(R.color.grey))
            refresh.strokeColor = ColorStateList.valueOf(resources.getColor(R.color.grey))
//            val user = FirebaseAuth.getInstance().currentUser.em
//            val email = intent.extras?.get("email")
//            val password = intent.extras?.get("password")

            FirebaseAuth.getInstance().currentUser?.reload()
                ?.addOnSuccessListener {
                    Log.d(TAG, "onCreate: verification state: ${FirebaseAuth.getInstance().currentUser?.isEmailVerified!!}")
                    if (FirebaseAuth.getInstance().currentUser?.isEmailVerified!!) {
                        startActivity(Intent(baseContext, MainActivity::class.java))
                        finish()
                    } else{
                        refresh.isEnabled = true
                        refresh.rippleColor = ColorStateList.valueOf(resources.getColor(R.color.colorSecondary))
                        refresh.strokeColor = ColorStateList.valueOf(resources.getColor(R.color.colorSecondary))
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        count++
        if(count>1 && mailSent){
            refresh.visibility = View.VISIBLE
        }
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        navigateToLoginScreen()
    }

    private fun navigateToLoginScreen() {
        startActivity(Intent(baseContext, LoginActivity::class.java))
        finish()
    }
}
