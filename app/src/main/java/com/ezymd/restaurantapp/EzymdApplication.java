package com.ezymd.restaurantapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Process;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.MutableLiveData;

import com.ezymd.restaurantapp.details.model.ItemModel;
import com.ezymd.restaurantapp.filters.model.DataModel;
import com.ezymd.restaurantapp.utils.ConnectivityReceiver;
import com.ezymd.restaurantapp.utils.UserInfo;
import com.facebook.FacebookSdk;
import com.stripe.android.PaymentConfiguration;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Mohit on 10/17/20202.
 */
public class EzymdApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private Activity mLastForegroundActivity;
    @Nullable
    public final String networkErrorMessage = "it seems network is not available right now";
    public MutableLiveData<ArrayList<ItemModel>> cartData = new MutableLiveData<>();
    public MutableLiveData<DataModel> filterModel = new MutableLiveData<>();

    public static boolean isAppForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && processInfo.processName.equals(context.getApplicationContext().getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }


    public static boolean isAppRunning(final Context context) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(context.getApplicationContext().getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    private static EzymdApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        PaymentConfiguration.init(
                this,
                ServerConfig.PAYMENT_PUBLISHABLE_KEY,
                ServerConfig.PAYMENT_ACCOUNT_ID
        );
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mInstance = this;
        FacebookSdk.setAutoLogAppEventsEnabled(false);
        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        appSignatureHelper.getAppSignatures();
        if (getResources() == null) {
            Process.killProcess(Process.myPid());
        }

        UserInfo userInfo=UserInfo.getInstance(mInstance);
        if (userInfo.getDeviceID().length() < 1) {
            String deviceId = android.provider.Settings.System.getString(getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            userInfo.setDeviceID(deviceId == null ? "" + new ApplicationInfo().uid : deviceId);
        }

    }

    public static synchronized EzymdApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mLastForegroundActivity = activity;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        mLastForegroundActivity = activity;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public Activity getLastForegroundActivity() {
        return mLastForegroundActivity;
    }


}
