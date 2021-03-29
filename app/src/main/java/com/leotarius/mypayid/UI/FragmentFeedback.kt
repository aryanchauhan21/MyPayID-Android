package com.leotarius.mypayid.UI

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.leotarius.mypayid.R
import kotlinx.android.synthetic.main.fragment_feedback.*


class FragmentFeedback : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        card1.setOnClickListener {
            // requesting focus on edit text if card is clicked
            feedback_text.requestFocus()
        }

        send.setOnClickListener {
            send.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.grey))
            send.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.white)))
            send.isEnabled = false

            val dbRef = FirebaseDatabase.getInstance().reference.child("feedback").push()
            dbRef.setValue(feedback_text.text.toString())
                .addOnCompleteListener {
                    Toast.makeText(context, "Feedback sent!", Toast.LENGTH_SHORT).show()
                    send.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_yello))
                    send.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.black)))
                    send.isEnabled = true
                }
                .addOnCanceledListener {
                    Toast.makeText(context, "Some error occurred.", Toast.LENGTH_SHORT).show()
                    send.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_yello))
                    send.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.black)))
                    send.isEnabled = true
                }
        }
    }
}