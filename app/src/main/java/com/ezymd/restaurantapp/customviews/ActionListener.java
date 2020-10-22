package com.ezymd.restaurantapp.customviews;

import android.view.View;
import android.widget.EditText;


/**
 * Created by travijuu on 26/05/16.
 */
public class ActionListener implements View.OnClickListener {

    QuantityPicker layout;
    ActionEnum action;
    EditText display;

    public ActionListener(QuantityPicker layout, EditText display, ActionEnum action) {
        this.layout = layout;
        this.action = action;
        this.display = display;
    }

    @Override
    public void onClick(View v) {
        try {
            int newValue = Integer.parseInt(this.display.getText().toString());

            if (!this.layout.valueIsAllowed(newValue)) {
                return;
            }

            this.layout.setValue(newValue);
        } catch (NumberFormatException e) {
            this.layout.refresh();
        }

        switch (this.action) {
            case INCREMENT:
                this.layout.increment();
                break;
            case DECREMENT:
                this.layout.decrement();
                break;
        }
    }
}