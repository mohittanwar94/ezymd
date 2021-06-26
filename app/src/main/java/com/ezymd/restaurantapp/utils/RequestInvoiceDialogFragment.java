package com.ezymd.restaurantapp.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ezymd.restaurantapp.R;
import com.ezymd.restaurantapp.customviews.SnapTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Created by Mohit on 3/8/2018.
 */

public class RequestInvoiceDialogFragment extends DialogFragment implements View.OnClickListener {

    DialogInterface.OnShowListener onShowListener;
    private SnapTextView title, subTitle, error, proceed;
    private TextInputLayout inputLay;
    private TextInputEditText parent_name;
    private UserInfo userInfo;
    private String errorMsg;
    private boolean isError;
    private OnEmailUpdate onEmailUpdate;


    public RequestInvoiceDialogFragment() {
    }

    public static RequestInvoiceDialogFragment newInstance(String errorMsg, boolean isError) {
        RequestInvoiceDialogFragment frag = new RequestInvoiceDialogFragment();
        Bundle args = new Bundle();
        args.putString(JSONKeys.ERROR, errorMsg);
        args.putBoolean(JSONKeys.ERROR_CODE, isError);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogFadeAnimation;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
//                    ((ActivityDetails) getActivity()).setClickablePayButton();
                }
            });

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    if (onShowListener != null)
                        onShowListener.onShow(dialog);
                }
            });
        }
        return inflater.inflate(R.layout.fragment_dialog_email_invoice, container);
    }

    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userInfo = UserInfo.getInstance(getActivity());

        errorMsg = getArguments().getString(JSONKeys.ERROR);
        isError = getArguments().getBoolean(JSONKeys.ERROR_CODE);


        setGui(view);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.proceed) {
            if (parent_name.length() > 0 && parent_name.getText().toString().trim().length() > 0) {

                if (Patterns.EMAIL_ADDRESS.matcher(parent_name.getText().toString().trim()).matches()) {
                    view.setClickable(false);
                    addEmailOnServer(parent_name.getText().toString().trim());

                } else {
                    parent_name.requestFocus();
                    new ShowDialog(getActivity()).disPlayDialog(R.string.validemail, false, false);
                }
            } else {
                new ShowDialog(getActivity()).disPlayDialog(R.string.email_err, false, false);
                parent_name.requestFocus();
            }
        }
    }

    private void addEmailOnServer(String email) {
        if (onEmailUpdate != null) {
            onEmailUpdate.onClick(email);
        }
        dismissAllowingStateLoss();
    }


    private void setGui(View view) {
        subTitle = view.findViewById(R.id.subTitle);
        title = view.findViewById(R.id.title);
        proceed = view.findViewById(R.id.proceed);
        proceed.setOnClickListener(this);
        parent_name = view.findViewById(R.id.parent_name);
        error = view.findViewById(R.id.error);
        parent_name.setText(userInfo.getEmail());
        setData();

    }

    private void setData() {
        error.setVisibility(View.GONE);
        if (isError) {
            error.setVisibility(View.VISIBLE);
        }
        error.setText(errorMsg);


    }

    public void setError(String errormsg, boolean isError) {
        this.isError = isError;
        this.errorMsg = errormsg;
        error.setVisibility(View.VISIBLE);
        error.setText(errorMsg);

    }


    public void setOnShowListener(DialogInterface.OnShowListener onShowListener) {
        this.onShowListener = onShowListener;
    }

    public void setOnClickListener(OnEmailUpdate onShowListener) {
        this.onEmailUpdate = onShowListener;
    }
}