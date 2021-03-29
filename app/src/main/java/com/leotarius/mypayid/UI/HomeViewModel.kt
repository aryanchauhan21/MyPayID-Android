package com.leotarius.mypayid.UI

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.leotarius.mypayid.Models.PaymentMethod
import com.leotarius.mypayid.Repository.Repository

class HomeViewModel : ViewModel() {

    // isAdded may take 3 values -1,0,1
    // -1 = failure, 0 = progress, 1 = success
    val isAdded: MutableLiveData<Int> = MutableLiveData<Int>().apply{value = 0}

    private val repository = Repository()

    fun addMethodToDatabase(paymentMethod: PaymentMethod){
        repository.addMethodToDatabase(paymentMethod).observeForever(Observer {
            isAdded.postValue(it)
        })
    }

}