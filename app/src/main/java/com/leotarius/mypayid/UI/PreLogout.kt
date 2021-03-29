package com.leotarius.mypayid.UI

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.leotarius.mypayid.LoginActivity
import com.leotarius.mypayid.R

class PreLogout: Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_temporary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logoutUser()
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        navigateToLoginScreen()
    }

    private fun navigateToLoginScreen() {
        startActivity(Intent(context, LoginActivity::class.java))
        activity?.finish()
    }
}