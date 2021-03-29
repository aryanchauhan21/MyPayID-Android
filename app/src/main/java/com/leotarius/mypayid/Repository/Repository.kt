package com.leotarius.mypayid.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.leotarius.mypayid.Models.PaymentMethod
import com.leotarius.mypayid.Models.User
import java.util.*
import kotlin.collections.HashMap

class Repository {

    val authUser = FirebaseAuth.getInstance().currentUser
    private val TAG = "Repository"

    fun getUserFromDatabase(): LiveData<User> {
        val dbUser: MutableLiveData<User> = MutableLiveData()
        val dbRef =
            FirebaseDatabase.getInstance().reference.child("users").child(authUser?.uid.toString())

        // now making the call or attaching the addValueEventListener which gives the value from reference on time
        // and whenever any changes are made to it.
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                dbUser.value = user
                Log.d(TAG, "onDataChange: got value = " + dbUser.value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "getUser:onCancelled", error.toException())
            }
        })
        return dbUser
    }

    fun addMethodToDatabase(paymentMethod: PaymentMethod): LiveData<Int> {
        val isAdded: MutableLiveData<Int> = MutableLiveData()

        // first i will add this method to the global method list.
        // then saving its push key i will add it to specific user methods.
        val globalDbRef = FirebaseDatabase.getInstance().reference.child("allPaymentMethods").push()
        globalDbRef.setValue(paymentMethod)
            .addOnSuccessListener {
                // Now we get global key for this method and save it in payment method
                // Then save that payment method to user specific payment methods
                val dbRef = FirebaseDatabase.getInstance().reference.child("users")
                    .child(authUser?.uid.toString()).child("paymentMethods").push()
                paymentMethod.globalKey = globalDbRef.key
                dbRef.setValue(paymentMethod)
                    .addOnSuccessListener { isAdded.postValue(1) }
                    .addOnFailureListener { isAdded.postValue(-1) }
            }
            .addOnFailureListener { isAdded.postValue(-1) }

        return isAdded
    }

    fun getMethodMapFromDatabase(): LiveData<HashMap<String, PaymentMethod>> {
        val dbRef =
            FirebaseDatabase.getInstance().reference.child("users").child(authUser?.uid.toString())
                .child("paymentMethods")
        val methodMap = MutableLiveData<HashMap<String, PaymentMethod>>()
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val methodsReceived = HashMap<String, PaymentMethod>()
                for (methodSnapshot in snapshot.children) {
                    methodSnapshot.getValue(PaymentMethod::class.java)?.let {
                        methodsReceived.put(
                            methodSnapshot.key as String,
                            it
                        )
                    }
                }
                methodMap.postValue(methodsReceived)
                Log.d(TAG, "onDataChange: ${methodsReceived.toString()}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "MethodList:onCancelled", error.toException())
            }
        })
        return methodMap
    }

    fun deletePaymentMethod(localKey: String, globalKey: String): LiveData<Int> {
        val isDeleted: MutableLiveData<Int> = MutableLiveData()
        // First we delete the method from global method list
        // then we delete it from user specific method list
        val globalDbRef =
            FirebaseDatabase.getInstance().reference.child("allPaymentMethods").child(globalKey)
        globalDbRef.removeValue()
            .addOnSuccessListener {
                val dbRef = FirebaseDatabase.getInstance().reference.child("users")
                    .child(authUser?.uid.toString()).child("paymentMethods").child(localKey)
                dbRef.removeValue()
                    .addOnSuccessListener { isDeleted.postValue(1) }
                    .addOnFailureListener { isDeleted.postValue(-1) }
            }
            .addOnFailureListener { isDeleted.postValue(-1) }
        return isDeleted
    }

    fun getQueryResult(queryType: Int, query: String): LiveData<HashMap<String, PaymentMethod>> {
        val methodMap = MutableLiveData<HashMap<String, PaymentMethod>>()
        val dbRef = FirebaseDatabase.getInstance().reference.child("allPaymentMethods")
        Log.d(TAG, "getQueryResult: $query")

        when(queryType){
            0 -> {
                dbRef.orderByChild("userName").startAt(query).endAt(query + "\uf8ff")
                    .addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val methodsReceived = HashMap<String, PaymentMethod>()
                            for (methodSnapshot in snapshot.children) {
                                methodSnapshot.getValue(PaymentMethod::class.java)?.let {
                                    methodsReceived.put(
                                        methodSnapshot.key as String,
                                        it
                                    )
                                }
                            }
                            methodMap.postValue(methodsReceived)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.w(TAG, "MethodList:onCancelled", error.toException())
                        }
                    })
            }

            1 -> {
                val methodsReceived = HashMap<String, PaymentMethod>()
                dbRef.orderByChild("phone").equalTo(query)
                    .addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (methodSnapshot in snapshot.children) {
                                methodSnapshot.getValue(PaymentMethod::class.java)?.let {
                                    methodsReceived.put(
                                        methodSnapshot.key as String,
                                        it
                                    )
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.w(TAG, "MethodList:onCancelled", error.toException())
                        }
                    })
                dbRef.orderByChild("phone").equalTo("+91$query")
                    .addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (methodSnapshot in snapshot.children) {
                                methodSnapshot.getValue(PaymentMethod::class.java)?.let {
                                    methodsReceived.put(
                                        methodSnapshot.key as String,
                                        it
                                    )
                                }
                            }
                            methodMap.postValue(methodsReceived)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.w(TAG, "MethodList:onCancelled", error.toException())
                        }
                    })
            }

            2 -> {
                // write for email
                var methodsReceived = HashMap<String, PaymentMethod>()
                val emailDbRef = FirebaseDatabase.getInstance().reference.child("users")
                emailDbRef.orderByChild("email").equalTo(query)
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            // There is only one entry in snapshot since email is unique for each user.
                            for (user_key_value in snapshot.children){
                                var user = user_key_value.getValue(User::class.java)
                                if (user != null) {
                                    if(user.paymentMethods!=null) methodsReceived = user.paymentMethods
                                }
                            }

                            methodMap.postValue(methodsReceived)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.w(TAG, "MethodList:onCancelled", error.toException())
                        }
                    })
            }

        }

        return methodMap
    }

}