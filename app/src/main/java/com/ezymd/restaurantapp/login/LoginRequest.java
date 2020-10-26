package com.ezymd.restaurantapp.login;

import org.jetbrains.annotations.Nullable;

public class LoginRequest {
    private String first_name = "";
    private String last_name = "";
    private String email = "";
    private String id = "";
    private String image_url = "";
    private String mobileNo = "";
    private String otp = "";
    private Boolean isSocialLogin = false;
    private Boolean isError = false;
    @Nullable
    public String errorMessage="";

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Boolean getError() {
        return isError;
    }

    public void setError(Boolean error) {
        isError = error;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Boolean getSocialLogin() {
        return isSocialLogin;
    }

    public void setSocialLogin(Boolean socialLogin) {
        isSocialLogin = socialLogin;
    }
}
