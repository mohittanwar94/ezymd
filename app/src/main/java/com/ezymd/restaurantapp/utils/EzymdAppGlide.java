package com.ezymd.restaurantapp.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

@GlideModule
public class EzymdAppGlide extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context)
                .setMemoryCacheScreens(3)
                .setBitmapPoolScreens(3)
                .build();
        builder.setMemoryCache(new LruResourceCache(calculator.getMemoryCacheSize()));
        int diskCacheSizeBytes = 1024 * 1024 * 30; // 30mb
//        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes * 2));
        builder.setBitmapPool(new LruBitmapPool(calculator.getBitmapPoolSize()));
        builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888));
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {

        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(UnsafeOkHttpClient.getUnsafeOkHttpClient());
        glide.getRegistry().replace(GlideUrl.class, InputStream.class, factory);
    }

}
