package com.ezymd.restaurantapp.font;

import android.util.DisplayMetrics;

public class Sizes {
    DisplayMetrics displayMetric;

    public Sizes(DisplayMetrics displayMetrics) {
        displayMetric = displayMetrics;
    }

    public float getLoginNormalTextSize() {
        return (float) ((50.63 * displayMetric.widthPixels) / (1080 * displayMetric.scaledDensity));
    }

    public float getLoginNormalTextSizeLand() {
        return (float) ((50.63 * displayMetric.heightPixels) / (1080 * displayMetric.scaledDensity));
    }

    public float getLoginMediumTextSize() {
        return (float) ((40.50 * displayMetric.widthPixels) / (1080 * displayMetric.scaledDensity));
    }

    public float getUserNameSize() {
        return (float) ((47.25 * displayMetric.widthPixels) / (1080 * displayMetric.scaledDensity));
    }

    public float getGradeTextSize() {
        return (float) ((45.39 * displayMetric.widthPixels) / (1080 * displayMetric.scaledDensity));
    }

    public float getGradeTextSizeLand() {
        return (float) ((45.39 * displayMetric.heightPixels) / (1080 * displayMetric.scaledDensity));
    }

    public float getLoginMiddleTextSize() {
        return (float) ((37.13 * displayMetric.widthPixels) / (1080 * displayMetric.scaledDensity));
    }

    public float getLoginSmallTextSize() {
        return (float) ((30.38 * displayMetric.widthPixels) / (1080 * displayMetric.scaledDensity));
    }

    public float getRowTextSize() {
        return (float) ((33.75 * displayMetric.widthPixels) / (1080 * displayMetric.scaledDensity));
    }

    public float getDragDropTextSize() {
        return (float) ((60.75 * displayMetric.widthPixels) / (1080 * displayMetric.scaledDensity));
    }

    public int getMediumPadding() {
        return (int) ((36.00 * displayMetric.widthPixels) / 1080);
    }

    public int getMediumPaddingLand() {
        return (int) ((36.00 * displayMetric.heightPixels) / 1080);
    }

    public int getSmallPadding() {
        return (int) ((18.00 * displayMetric.widthPixels) / 1080);
    }

    public int getSmallPaddingLand() {
        return (int) ((18.00 * displayMetric.heightPixels) / 1080);
    }

    public int getNormalPadding() {
        return (int) ((55.00 * displayMetric.widthPixels) / 1080);
    }

    public int getPadding() {
        return (int) ((75.00 * displayMetric.widthPixels) / 1080);
    }

    public int getPaddingLand() {
        return (int) ((75.00 * displayMetric.heightPixels) / 1080);
    }

    public float getFontSize(float size) {
        return (size * displayMetric.widthPixels) / (1080 * displayMetric.scaledDensity);
    }

    public float getFontSizeLand(float size) {
        return (size * displayMetric.heightPixels) / (1080 * displayMetric.scaledDensity);
    }


    public int getSize(int size) {
        return (size * displayMetric.widthPixels) / 1080;
    }
    public int getHeight(int size) {
        return (size * displayMetric.heightPixels) / 1920;
    }
    public int getSizeLand(int size) {
        return (size * displayMetric.heightPixels) / 1080;
    }
}



