package com.ezymd.restaurantapp.customviews;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;


/**
 * Created by travijuu on 13/04/17.
 */

public class DefaultOnEditorActionListener implements TextView.OnEditorActionListener {

    QuantityPicker layout;

    public DefaultOnEditorActionListener(QuantityPicker layout) {
        this.layout = layout;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            try {
                int value = Integer.parseInt(v.getText().toString());

                layout.setValue(value);

                if (layout.getValue() == value) {
                    layout.getValueChangedListener().valueChanged(value, ActionEnum.MANUAL);
                    return false;
                }
            } catch (NumberFormatException e) {
                layout.refresh();
            }
        }
        return true;
    }
}
