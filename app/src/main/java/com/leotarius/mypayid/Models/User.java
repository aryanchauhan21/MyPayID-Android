package com.leotarius.mypayid.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

public class User {

    @SerializedName("uid")
    String uid;

    @SerializedName("name")
    String name;

    @SerializedName("email")
    String email;

    @SerializedName("paymentMethods")
    HashMap<String, PaymentMethod> paymentMethods;

    public User(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        paymentMethods = new HashMap<>();
    }

    public HashMap<String, PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(HashMap<String, PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}