package com.leotarius.mypayid.Models;

public class PaymentMethodPack {
    private String key;
    private PaymentMethod paymentMethod;

    public PaymentMethodPack(String key, PaymentMethod paymentMethod) {
        this.key = key;
        this.paymentMethod = paymentMethod;
    }

    public PaymentMethodPack() {
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getKey() {
        return key;
    }
}
