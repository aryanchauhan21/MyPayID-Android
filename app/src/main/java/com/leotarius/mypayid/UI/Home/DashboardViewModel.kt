package com.leotarius.mypayid.UI.Home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.leotarius.mypayid.Models.PaymentMethod
import com.leotarius.mypayid.Models.PaymentMethodPack
import com.leotarius.mypayid.Repository.Repository

class DashboardViewModel : ViewModel() {
    private val TAG = "DashboardViewModel"
    val methodList = MutableLiveData<ArrayList<PaymentMethodPack>>()
    private val repository = Repository()
    public val isDeleted : MutableLiveData<Int> = MutableLiveData<Int>().apply{value = 0}


    fun getMethodList(){
        repository.getMethodMapFromDatabase().observeForever {
            val newMethodList = ArrayList<PaymentMethodPack>()
            for (pack in it) {
                newMethodList.add(PaymentMethodPack(pack.key, pack.value))
            }
            methodList.postValue(newMethodList)
            Log.d(TAG, "getMethodList: ${newMethodList.toString()}")
        }
    }

    fun deletePaymentMethod(localKey : String, globalKey: String){
        repository.deletePaymentMethod(localKey, globalKey).observeForever{
            isDeleted.postValue(it)
        }
    }

}