package com.ezymd.restaurantapp.customviews;

import android.content.Context;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

public class SnapVerticalViewPager extends ViewPager {
    public SnapVerticalViewPager(@NonNull Context context) {
        this(context, null);
    }

    public SnapVerticalViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setPageTransformer(false, new VerticalTransform());
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        try {
            Class cls = this.getClass().getSuperclass();
            Field distanceField = cls.getDeclaredField("mFlingDistance");
            distanceField.setAccessible(true);
            distanceField.setInt(this, distanceField.getInt(this) / 40);
        } catch (Exception e) {
        }

        try {
            Class cls = this.getClass().getSuperclass();
            Field minVelocityField = cls.getDeclaredField("mMinimumVelocity");
            minVelocityField.setAccessible(true);
            minVelocityField.setInt(this, minVelocityField.getInt(this) / 25);
        } catch (Exception e) {
        }

        try {
            Class cls = this.getClass().getSuperclass();
            Field maxVelocityField = cls.getDeclaredField("mMaximumVelocity");
            maxVelocityField.setAccessible(true);
            maxVelocityField.setInt(this, maxVelocityField.getInt(this) * 10);
        } catch (Exception e) {
        }

        try {
            Class cls = this.getClass().getSuperclass();
            Field slopField = cls.getDeclaredField("mTouchSlop");
            slopField.setAccessible(true);
            slopField.setInt(this, slopField.getInt(this) / 10);
        } catch (Exception e) {
        }

        try {
            Class cls = this.getClass().getSuperclass();
            Field minHeightWidthRatioField = cls.getDeclaredField("minYXRatioForIntercept");
            minHeightWidthRatioField.setAccessible(true);
            minHeightWidthRatioField.setFloat(this, minHeightWidthRatioField.getFloat(this) * 8);
        } catch (Exception e) {
        }

        try {
            Class cls = this.getClass().getSuperclass();
            Field minHeightWidthRatioField = cls.getDeclaredField("minYXRatioForTouch");
            minHeightWidthRatioField.setAccessible(true);
            minHeightWidthRatioField.setInt(this, minHeightWidthRatioField.getInt(this) * 4);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return false;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return super.canScrollHorizontally(direction);
    }

    private MotionEvent swapTouchEvent(MotionEvent event) {
        float width = getWidth();
        float height = getHeight();

        float swappedX = (event.getY() / height) * width;
        float swappedY = (event.getX() / width) * height;

        event.setLocation(swappedX, swappedY);

        return event;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercept = super.onInterceptTouchEvent(swapTouchEvent(event));
        //If not intercept, touch event should not be swapped.
        swapTouchEvent(event);
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapTouchEvent(ev));
    }

    private class VerticalTransform implements PageTransformer {

        @Override
        public void transformPage(View view, float position) {
            view.setTranslationX(view.getWidth() * -position);
            float yPosition = position * view.getHeight();
            view.setTranslationY(yPosition);
        }
    }
}
