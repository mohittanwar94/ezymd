package com.ezymd.restaurantapp.customviews;

import android.util.Log;


/**
 * Created by travijuu on 19/12/16.
 */

public class DefaultValueChangedListener implements ValueChangedListener {

    public void valueChanged(int value, ActionEnum action) {

        String actionText = action == ActionEnum.MANUAL ? "manually set" : (action == ActionEnum.INCREMENT ? "incremented" : "decremented");
        String message = String.format("NumberPicker is %s to %d", actionText, value);
        Log.v(this.getClass().getSimpleName(), message);
    }
}
