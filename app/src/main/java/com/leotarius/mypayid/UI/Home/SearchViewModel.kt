package com.leotarius.mypayid.UI.Home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.leotarius.mypayid.Models.PaymentMethod
import com.leotarius.mypayid.Models.PaymentMethodPack
import com.leotarius.mypayid.Repository.Repository

class SearchViewModel : ViewModel() {
    private val TAG = "SearchViewModel"
    val methodList = MutableLiveData<ArrayList<PaymentMethodPack>>()
    private val repository = Repository()

    fun getMethodList(queryType: Int, query: String){
        repository.getQueryResult(queryType, query).observeForever {
            val newMethodList = ArrayList<PaymentMethodPack>()
            for (pack in it) {
                newMethodList.add(PaymentMethodPack(pack.key, pack.value))
            }
            methodList.postValue(newMethodList)
            Log.d(TAG, "getMethodList: ${newMethodList.toString()}")
        }
    }
}