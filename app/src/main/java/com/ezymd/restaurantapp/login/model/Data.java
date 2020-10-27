package com.ezymd.restaurantapp.login.model;

public class Data {
    private String access_token;

    private User user;

    public String getAccess_token ()
    {
        return access_token;
    }

    public void setAccess_token (String access_token)
    {
        this.access_token = access_token;
    }

    public User getUser ()
    {
        return user;
    }

    public void setUser (User user)
    {
        this.user = user;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [access_token = "+access_token+", user = "+user+"]";
    }
}
