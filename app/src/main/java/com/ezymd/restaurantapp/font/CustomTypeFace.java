package com.ezymd.restaurantapp.font;

import android.content.Context;
import android.graphics.Typeface;

public class CustomTypeFace {
    public static Typeface book, bold, medium, roman;
    private static CustomTypeFace customTypeFace;

    private CustomTypeFace(Context mContext) {
        book = Typeface.createFromAsset(mContext.getAssets(), "fonts/Avenirbook.ttf");
        bold = Typeface.createFromAsset(mContext.getAssets(), "fonts/Avenirheavy.ttf");
        medium = Typeface.createFromAsset(mContext.getAssets(), "fonts/Avenirmedium.ttf");
        roman = Typeface.createFromAsset(mContext.getAssets(), "fonts/Avenirroman.ttf");
    }

    public static void createInstance(Context context) {
        if (customTypeFace == null)
            customTypeFace = new CustomTypeFace(context);
    }
}
