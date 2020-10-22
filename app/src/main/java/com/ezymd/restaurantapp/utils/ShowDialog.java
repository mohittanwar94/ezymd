package com.ezymd.restaurantapp.utils;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.ezymd.restaurantapp.BaseActivity;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;

public final class ShowDialog {

    private static WeakReference<Dialog> mDialogWeakReference;
    private Context context;
    private DialogInterface.OnDismissListener onDismissListener;

    public ShowDialog(Context con) {
        if (con == null)
            return;
        context = con;
        dismiss();
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public ShowDialog disPlayDialog(final int messages, final boolean noError, boolean isChangeColor) {
        ((BaseActivity) context).showError(noError, messages, new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (((BaseActivity) context).isFinishing() || ((BaseActivity) context).isDestroyed())
                        return;
                } else if (((BaseActivity) context).isFinishing())
                    return;
                if (onDismissListener != null)
                    onDismissListener.onDismiss(null);

            }
        });
        return this;

    }

    public ShowDialog disPlayDialog(final String messages, final boolean noError, boolean isChangeColor) {
        ((BaseActivity) context).showError(noError, messages, new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (((BaseActivity) context).isFinishing() || ((BaseActivity) context).isDestroyed())
                        return;
                } else if (((BaseActivity) context).isFinishing())
                    return;
                if (onDismissListener != null)
                    onDismissListener.onDismiss(null);
            }
        });
        return this;

    }


    public void dismiss() {
        try {
            if (mDialogWeakReference != null && mDialogWeakReference.get() != null && mDialogWeakReference.get().isShowing()) {
                mDialogWeakReference.get().dismiss();
                mDialogWeakReference = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }


}
