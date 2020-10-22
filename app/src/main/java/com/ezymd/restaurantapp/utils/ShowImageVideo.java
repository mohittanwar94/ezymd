package com.ezymd.restaurantapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ezymd.restaurantapp.R;
import com.ezymd.restaurantapp.customviews.SnapViewPager;
import com.ezymd.restaurantapp.font.Sizes;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;


public class ShowImageVideo {
    private Context cxt;
    private Sizes fontSize;
    private UserInfo userInfo;

    public ShowImageVideo(Context c) {
        cxt = c;
        fontSize = new Sizes(c.getResources().getDisplayMetrics());
        userInfo = UserInfo.getInstance(cxt);
    }


    public Dialog Display(final ArrayList<String> images, int pos) {
        final Dialog dialog = new Dialog(cxt);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.image_popup_lay);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogFadeAnimation;
        LayoutParams params = dialog.getWindow().getAttributes();
        params.height = cxt.getResources().getDisplayMetrics().heightPixels;
        params.width = cxt.getResources().getDisplayMetrics().widthPixels;
        dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        dialog.setCanceledOnTouchOutside(false);

        final TextView header = dialog.findViewById(R.id.header);
        ImageView close = dialog.findViewById(R.id.close);
        header.setText((pos + 1) + " of " + images.size());

        SnapViewPager pager = dialog.findViewById(R.id.pager);
        pager.setOffscreenPageLimit(images.size());
        final CustomPagerAdapter1 pagerAdapter = new CustomPagerAdapter1(cxt, images);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(pos);
        pager.setPageMargin(fontSize.getMediumPadding());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int pos) {

                header.setText((pos + 1) + " of " + images.size());
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {


            }

            public void onPageScrollStateChanged(int arg0) {


            }
        });
        close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
        return dialog;
    }


    class CustomPagerAdapter1 extends PagerAdapter {

        Context mContext;
        ArrayList<String> imagesMedia;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter1(Context context, ArrayList<String> imagesMedia) {
            mContext = context;
            this.imagesMedia = imagesMedia;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return imagesMedia.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.images_row, container, false);

            final PhotoView img = itemView.findViewById(R.id.preview);
            img.setMaximumScale(6f);
            img.setMediumScale(3f);
            final ProgressBar bar = itemView.findViewById(R.id.progressBar1);
            bar.setVisibility(View.VISIBLE);
            String imgPath = imagesMedia.get(position);
            if (imgPath.startsWith("http") || new File(imgPath).exists()) {
                GlideApp.with(cxt.getApplicationContext()).load(imgPath).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        bar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        bar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(img);
            } else
                itemView.findViewById(R.id.contentRemoved).setVisibility(View.VISIBLE);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((FrameLayout) object);
        }

    }


}
