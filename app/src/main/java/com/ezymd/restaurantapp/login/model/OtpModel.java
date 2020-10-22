package com.ezymd.restaurantapp.login.model;

public class OtpModel {
    private boolean isSended;
    private String msg="";

    public boolean isSended() {
        return isSended;
    }

    public void setSended(boolean sended) {
        isSended = sended;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
