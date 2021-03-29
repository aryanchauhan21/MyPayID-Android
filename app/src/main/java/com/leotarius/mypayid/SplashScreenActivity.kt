package com.leotarius.mypayid

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.leotarius.mypayid.Utils.UtilFunctions
import kotlinx.android.synthetic.main.splash_screen.*

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        val user = FirebaseAuth.getInstance().currentUser

        UtilFunctions.delay(0.5f, UtilFunctions.DelayCallback {
            if(user == null){
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                if(user.isEmailVerified) startActivity(Intent(this, MainActivity::class.java))
                else{
                    startActivity(Intent(baseContext, VerifyEmailActivity::class.java))
                }
            }
            finish()
        })

    }
}