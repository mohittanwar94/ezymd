package com.ezymd.restaurantapp.cart.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TransactionChargeModel implements Serializable {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Integer status;


    @SerializedName("amount")
    @Expose
    private Double amount;


    @SerializedName("transaction_charge")
    @Expose
    private Double transaction_charge;


    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getTransaction_charge() {
        return transaction_charge;
    }

    public void setTransaction_charge(Double transaction_charge) {
        this.transaction_charge = transaction_charge;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
