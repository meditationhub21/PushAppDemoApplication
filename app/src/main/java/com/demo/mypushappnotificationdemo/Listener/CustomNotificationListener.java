package com.demo.mypushappnotificationdemo.Listener;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//import com.microsoft.windowsazure.messaging.notificationhubs.NotificationListener;
//
//public class CustomNotificationListener extends FirebaseMessagingService implements NotificationListener {
//    private static final String TAG = "PushNotification";
//    @Override
//    public void onPushNotificationReceived(Context context, RemoteMessage remoteMessage) {
//        RemoteMessage.Notification notification = remoteMessage.getNotification();
//        String title = notification.getTitle();
//        String body = notification.getBody();
//        System.out.println(title);
//        System.out.println(body);
//
//
//        if (remoteMessage != null) {
//            Log.d(TAG, "Message Notification Title: " + title);
//            Log.d(TAG, "Message Notification Body: " + remoteMessage);
//        }
//    }
//
//
//}
