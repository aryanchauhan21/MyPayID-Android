package com.leotarius.mypayid

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.leotarius.mypayid.Models.User
import com.leotarius.mypayid.Repository.Repository
import java.util.*

class MainActivityViewModel: ViewModel(){
    val authUser = FirebaseAuth.getInstance().currentUser
    val dbref = FirebaseDatabase.getInstance().reference.child("users").child(authUser?.uid.toString())
    val repository = Repository()
    val dbUser: MutableLiveData<User> = MutableLiveData()


    fun getUser(): LiveData<User>?{
        return dbUser
    }

    // Now reading data of user object from database and setting it to dbUser live data object
    // which will trigger observable event in MainActivity
    fun getUserFromDatabase(){
        // updating this live data from live data from repository
        repository.getUserFromDatabase().observeForever(Observer {
            dbUser.postValue(it)
        })
    }


}