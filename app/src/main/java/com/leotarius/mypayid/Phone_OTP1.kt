package com.leotarius.mypayid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.phone_otp1.*

class Phone_OTP1: AppCompatActivity() {
    private val TAG = "Phone_OTP1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.phone_otp1);


        var phone: String

        next.setOnClickListener {
            Log.d(TAG, "onCreate: clicked")

            phone = phone_number.text.toString()
            if(phone.length!=10) {
                error.visibility=View.VISIBLE
            } else{
                error.visibility=View.GONE
                phone = "+91" + phone_number.text.toString()
                val intent = Intent(this, Phone_OTP2::class.java)
                intent.putExtra("phone", phone)
                Log.d(TAG, "onCreate: starting next activity" + phone)
                startActivity(intent)
            }
        }

    }
}