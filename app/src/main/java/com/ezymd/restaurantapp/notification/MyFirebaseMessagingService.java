/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ezymd.restaurantapp.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ezymd.restaurantapp.MainActivity;
import com.ezymd.restaurantapp.R;
import com.ezymd.restaurantapp.ui.myorder.OrderFragment;
import com.ezymd.restaurantapp.ui.myorder.model.OrderModel;
import com.ezymd.restaurantapp.utils.JSONKeys;
import com.ezymd.restaurantapp.utils.SnapLog;
import com.ezymd.restaurantapp.utils.UserInfo;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static int notification_count = 0;

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        UserInfo sd = UserInfo.getInstance(this);
        if (!TextUtils.isEmpty(refreshedToken)) {
            sd.setDeviceToken(refreshedToken);

        }
        SnapLog.print(sd.getDeviceToken());
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        try {
            UserInfo userInfo = UserInfo.getInstance(this);
            Map<String, String> data = remoteMessage.getData();
            for (Map.Entry<String, String> entry : data.entrySet()) {
                SnapLog.print(entry.getKey() + ":" + entry.getValue());
            }
            generateNotification(this, userInfo, remoteMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateNotification(Context context, UserInfo userInfo, RemoteMessage remoteData) {
        Map<String, String> data = remoteData.getData();
        /*if (EzymdApplication.isAppForeground(this)) {
            Bundle bundle = new Bundle();
            bundle.putString(JSONKeys.SUB_TITLE, data.get(JSONKeys.SUB_TITLE));
            bundle.putString(JSONKeys.TITLE, data.get(JSONKeys.TITLE));
            bundle.putString(JSONKeys.TYPE, data.get(JSONKeys.TYPE));
            if (Integer.parseInt(data.get(JSONKeys.TYPE)) == 1) {
                if (data.containsKey("order")) {
                    String value = data.get("order");
                    OrderModel orderModel = new Gson().fromJson(value, OrderModel.class);
                    bundle.putSerializable(JSONKeys.OBJECT, orderModel);
                }
            }
            Intent mIntent = new Intent(this, NotificationActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            mIntent.putExtras(bundle);
            startActivity(mIntent);
        } else {
        */
        String subTitle = data.get(JSONKeys.SUB_TITLE);
        String title = data.get(JSONKeys.TITLE);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), getString(R.string.default_notification_channel_id));
        notification.setPriority(NotificationCompat.PRIORITY_MAX);
        notification.setContentTitle(title);
        notification.setSmallIcon(R.drawable.ic_location);
        notification.setAutoCancel(true);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        notification.setContentText(subTitle);
        bigTextStyle.bigText(subTitle);
        notification.setStyle(bigTextStyle);

        Intent activityIntent = null;
        if (Integer.parseInt(data.get(JSONKeys.TYPE)) == 1) {
            activityIntent = new Intent(context, MainActivity.class);
            if (data.containsKey("order")) {
                String value = data.get("order");
                OrderModel orderModel = new Gson().fromJson(value, OrderModel.class);
                activityIntent.putExtra(JSONKeys.OBJECT, orderModel);
                activityIntent.putExtra(JSONKeys.LABEL, OrderFragment.class.getSimpleName());
            }
        } else
            activityIntent = new Intent(context, MainActivity.class);
        // activityIntent.putExtra(JSONKeys.TYPE, data.get(JSONKeys.TYPE));
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent intents = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentText(subTitle);
        bigTextStyle.bigText(subTitle);
        notification.setContentTitle(title);
        bigTextStyle.setBigContentTitle(title);
        notification.setContentIntent(intents);
        notification.setStyle(bigTextStyle);
        notificationManager.notify((int) System.currentTimeMillis(), notification.build());
    }


}