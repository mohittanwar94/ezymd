package com.ezymd.restaurantapp.customviews;

import android.view.View;
import android.widget.EditText;


/**
 * Created by travijuu on 03/06/17.
 */

public class DefaultOnFocusChangeListener implements View.OnFocusChangeListener {

    QuantityPicker layout;

    public DefaultOnFocusChangeListener(QuantityPicker layout) {
        this.layout = layout;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText editText = (EditText) v;

        if (!hasFocus) {
            try {
                int value = Integer.parseInt(editText.getText().toString());
                layout.setValue(value);

                if (layout.getValue() == value) {
                    layout.getValueChangedListener().valueChanged(value, ActionEnum.MANUAL);
                } else {
                    layout.refresh();
                }
            } catch (NumberFormatException e) {
                layout.refresh();
            }
        }
    }
}
