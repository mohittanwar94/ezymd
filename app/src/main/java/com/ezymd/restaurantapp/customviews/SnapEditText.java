package com.ezymd.restaurantapp.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatEditText;

import com.ezymd.restaurantapp.R;
import com.ezymd.restaurantapp.font.CustomTypeFace;


/**
 * Created by VTSTN-27 on 8/25/2016.
 */
public class SnapEditText extends AppCompatEditText {
    private OnBackListener mOnBackListener;
    private CopyPasteEditTextListener listeners;

    public SnapEditText(Context context) {
        super(context);
        applyCustomFont(context, null);
    }

    public SnapEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context, attrs);
    }

    public SnapEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }
    private Typeface selectTypeface(Context context, int fontFamily) {
        CustomTypeFace.createInstance(context);
        switch (fontFamily) {
            case 0:
                return CustomTypeFace.book;
            case 1:
                return CustomTypeFace.bold;
            case 2:
                return CustomTypeFace.medium;
            case 3:
                return CustomTypeFace.roman;
            default:
                break;
        }
        return CustomTypeFace.book;
    }
    public void applyCustomFont(Context context, AttributeSet attrs) {
       if (attrs != null) {
            TypedArray attributeArray = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.SnapEditText);
            try {
                int fontFamily = attributeArray.getInt(R.styleable.SnapEditText_textFontEdit, 0);
                if (!isInEditMode()) {
                    setTypeface(selectTypeface(context, fontFamily));
                }

                Drawable drawableLeft = null;
                Drawable drawableRight = null;
                Drawable drawableBottom = null;
                Drawable drawableTop = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawableLeft = attributeArray.getDrawable(R.styleable.SnapEditText_customDrawableLeft);
                    drawableRight = attributeArray.getDrawable(R.styleable.SnapEditText_customDrawableRight);
                    drawableBottom = attributeArray.getDrawable(R.styleable.SnapEditText_customDrawableBottom);
                    drawableTop = attributeArray.getDrawable(R.styleable.SnapEditText_customDrawableTop);
                } else {
                    final int drawableLeftId = attributeArray.getResourceId(R.styleable.SnapEditText_customDrawableLeft, -1);
                    final int drawableRightId = attributeArray.getResourceId(R.styleable.SnapEditText_customDrawableRight, -1);
                    final int drawableBottomId = attributeArray.getResourceId(R.styleable.SnapEditText_customDrawableBottom, -1);
                    final int drawableTopId = attributeArray.getResourceId(R.styleable.SnapEditText_customDrawableTop, -1);

                    if (drawableLeftId != -1)
                        drawableLeft = AppCompatResources.getDrawable(context, drawableLeftId);
                    if (drawableRightId != -1)
                        drawableRight = AppCompatResources.getDrawable(context, drawableRightId);
                    if (drawableBottomId != -1)
                        drawableBottom = AppCompatResources.getDrawable(context, drawableBottomId);
                    if (drawableTopId != -1)
                        drawableTop = AppCompatResources.getDrawable(context, drawableTopId);
                }
                setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
            } finally {
                attributeArray.recycle();
            }
        }
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mOnBackListener != null)
                mOnBackListener.onBackPressed(this);
        }
        return super.onKeyPreIme(keyCode, event);

    }

    public void setOnBackPressListerner(OnBackListener mOnBackListener) {
        this.mOnBackListener = mOnBackListener;
    }

    public interface OnBackListener {
        void onBackPressed(View v);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        boolean consumed = super.onTextContextMenuItem(id);
        switch (id) {
            case android.R.id.paste:
                onTextPaste();
                break;
        }
        return consumed;
    }


    public void addListener(CopyPasteEditTextListener listener) {
        listeners = listener;
    }

    public void onTextPaste() {
        if (listeners != null) {
            listeners.onUpdate();
        }
    }

}
