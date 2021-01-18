package com.ezymd.restaurantapp.notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.ezymd.restaurantapp.BaseActivity;
import com.ezymd.restaurantapp.EzymdApplication;
import com.ezymd.restaurantapp.R;
import com.ezymd.restaurantapp.customviews.SnapTextView;
import com.ezymd.restaurantapp.font.Sizes;
import com.ezymd.restaurantapp.utils.OnSwipeTouchListener;

public class NotificationActivity extends BaseActivity {
    Sizes size;
    Context mActivity;
    //int type;
    boolean isCancel = false;
    private FrameLayout frameLayout, cardView, frameimageAnim;
    private AppCompatImageView imageAnim, image;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 26)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notification_alert);
        this.setFinishOnTouchOutside(false);
        getWindow().getAttributes().gravity = Gravity.TOP;
        getWindow().getAttributes().width = getResources().getDisplayMetrics().widthPixels;

        size = new Sizes(getResources().getDisplayMetrics());

        cardView = findViewById(R.id.cardView);
        imageAnim = findViewById(R.id.imageAnim);
        frameimageAnim = findViewById(R.id.frameimageAnim);
        image = findViewById(R.id.image);
        cardView.setVisibility(View.GONE);
        ObjectAnimator animX = ObjectAnimator.ofFloat(frameimageAnim,
                View.TRANSLATION_Y, -size.getSize(550), 0);
        animX.setDuration(300);
        animX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                frameimageAnim.clearAnimation();
                cardView.postDelayed(() -> {
                    frameimageAnim.setVisibility(View.GONE);
                    scaleView(cardView);
                }, 300);
            }
        });
        animX.start();


        frameLayout = findViewById(R.id.frameLayout);
        mActivity = NotificationActivity.this;

       // type = Integer.parseInt(getIntent().getStringExtra("type"));
        SnapTextView header = findViewById(R.id.header);
        SnapTextView msg = findViewById(R.id.msg);
        SnapTextView checkNow = findViewById(R.id.ok);
        checkNow.setVisibility(View.GONE);

        imageAnim.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.blue_circle));
        imageAnim.setImageResource(R.drawable.ic_location);
        image.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.blue_circle));
        image.setImageResource(R.drawable.ic_location);
        Activity mLastActivity = ((EzymdApplication) getApplicationContext()).getLastForegroundActivity();
            /*if (type == ActivityType.CHAT_MESSAGE_NOTIFICATION && mLastActivity != null && !mLastActivity.isFinishing() && (mLastActivity instanceof ChatActivity)) {
                finish();
                return;
            }
*/

        checkNow.postDelayed(() -> {
            if (!isCancel) {
                onBackPressed(false);
            }
        }, 8000);
        OnSwipeTouchListener touchListener = new OnSwipeTouchListener(mActivity) {
            @Override
            public void onTapConfirmed() {
                onBackPressed(true);
            }

            @Override
            public void onSwipeTop() {
                onBackPressed(false);
            }
        };
        touchListener.setConfirmTap(true);
        cardView.setOnTouchListener(touchListener);
    }

    public void scaleView(View v) {
        cardView.setVisibility(View.VISIBLE);
        Animation anim = new ScaleAnimation(
                0.3f, 1f, // Start and end values for the X axis scaling
                1f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(150);
        v.startAnimation(anim);
    }


    public void onBackPressed(boolean isClicked) {
        if (isCancel)
            return;
        try {
            isCancel = true;
            cardView.animate().translationY(-frameLayout.getHeight()).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    try {
                        NotificationActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    NotificationActivity.super.onBackPressed();
                    overridePendingTransition(0, 0);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
    }
}
