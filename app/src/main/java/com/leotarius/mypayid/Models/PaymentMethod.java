package com.leotarius.mypayid.Models;

import com.google.gson.annotations.SerializedName;

public class PaymentMethod {

    @SerializedName("paymentProvider")
    String paymentProvider;

    @SerializedName("phone")
    String phone;

    @SerializedName("userName")
    String userName;

    @SerializedName("globalKey")
    String globalKey;

    public PaymentMethod() {
    }

    public PaymentMethod(String paymentProvider, String phone, String userName) {
        this.paymentProvider = paymentProvider;
        this.phone = phone;
        this.userName = userName;
        globalKey = null;
    }

    public String getGlobalKey() {
        return globalKey;
    }

    public void setGlobalKey(String globalKey) {
        this.globalKey = globalKey;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(String paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}