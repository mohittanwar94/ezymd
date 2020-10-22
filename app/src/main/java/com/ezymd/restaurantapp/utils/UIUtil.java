package com.ezymd.restaurantapp.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.transition.Slide;


/**
 * Created by VTSTN-27 on 10/16/2020.
 */

public class UIUtil {

    /**
     * Supresses instantiation
     */
    private UIUtil() {
        throw new AssertionError();
    }

    public static float convertDpToPx(Context context, float dp) {
        Resources res = context.getResources();

        return dp * (res.getDisplayMetrics().densityDpi / 160f);
    }

    /**
     * Show keyboard and focus to given EditText
     *
     * @param context Context
     * @param target  EditText to focus
     */
    public static void showKeyboard(Context context, EditText target) {
        if (context == null || target == null) {
            return;
        }

        InputMethodManager imm = getInputMethodManager(context);

        imm.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Show keyboard and focus to given EditText.
     * Use this method if target EditText is in Dialog.
     *
     * @param dialog Dialog
     * @param target EditText to focus
     */
    public static void showKeyboardInDialog(Dialog dialog, EditText target) {
        if (dialog == null || target == null) {
            return;
        }

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        target.requestFocus();
    }

    /**
     * hide keyboard
     *
     * @param context Context
     * @param target  View that currently has focus
     */
    public static void hideKeyboard(Context context, View target) {
        if (context == null || target == null) {
            return;
        }

        InputMethodManager imm = getInputMethodManager(context);
        imm.hideSoftInputFromWindow(target.getWindowToken(), 0);
    }

    /**
     * hide keyboard
     *
     * @param activity Activity
     */
    public static void hideKeyboard(Activity activity) {
        View view = activity.getWindow().getDecorView();

        if (view != null) {
            hideKeyboard(activity, view);
        }
    }

    private static InputMethodManager getInputMethodManager(Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    public static void clickHandled(View it) {
        it.setClickable(false);
        it.postDelayed(() -> {
            it.setClickable(true);
        }, 150);
    }

    public static void clickAlpha(View it) {
        it.setClickable(false);
        it.setAlpha(0.5f);
        it.postDelayed(() -> {
            it.setClickable(true);
            it.setAlpha(1f);
        }, 150);
    }

    public static Slide slideTransition() {
        Slide slide = new Slide(Gravity.END);
        slide.setDuration(300);
        slide.setInterpolator(new AccelerateDecelerateInterpolator());
        return slide;
    }

    public static class SimpleAnimationListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {


        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    public static void applyRippleAnimation(View view, float scale, int repeatCount, int repeatMode, int duration) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", scale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", scale);
        alpha.setDuration(duration);
        scaleX.setDuration(duration);
        scaleY.setDuration(duration);
        alpha.setRepeatCount(repeatCount);
        alpha.setRepeatMode(repeatMode);
        scaleX.setRepeatCount(repeatCount);
        scaleX.setRepeatMode(repeatMode);
        scaleY.setRepeatCount(repeatCount);
        scaleY.setRepeatMode(repeatMode);
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleDown.playTogether(alpha, scaleX, scaleY);
        scaleDown.start();
    }

    public static boolean isHeadsetOn(Context mContext) {
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (am == null)
            return false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return am.isWiredHeadsetOn() || am.isBluetoothScoOn() || am.isBluetoothA2dpOn();
        } else {
            AudioDeviceInfo[] devices = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (int i = 0; i < devices.length; i++) {
                AudioDeviceInfo device = devices[i];
                if (device.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET
                        || device.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                        || device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                        || device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
                    return true;
                }
            }
        }
        return false;
    }
}