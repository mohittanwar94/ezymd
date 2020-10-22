package com.ezymd.restaurantapp.customviews;

import android.content.Context;
import android.util.AttributeSet;

import com.ezymd.restaurantapp.font.CustomTypeFace;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Created by VTSTN-27 on 18/10/2020.
 */
public class SnapTextInputEditText extends TextInputEditText {
    public SnapTextInputEditText(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public SnapTextInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    public SnapTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public void applyCustomFont(Context context) {
        if (!isInEditMode()) {
            CustomTypeFace.createInstance(context);
            setTypeface(CustomTypeFace.book);
        }
    }


}
