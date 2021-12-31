package com.demo.mypushappnotificationdemo.notifier;


import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.demo.mypushappnotificationdemo.MainActivity;
import com.demo.mypushappnotificationdemo.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseService extends FirebaseMessagingService {

    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "101";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG,"From: "+remoteMessage.getFrom());
        Log.d(TAG,"onMessageReceived: "+remoteMessage.getNotification().getTitle());
        Log.d(TAG,"onMessageReceived: "+remoteMessage.getNotification().getBody());
        Log.d(TAG,"onMessageReceived: "+remoteMessage.getData());

//        if(MainActivity.isVisible){
//            MainActivity.mainActivity.ToastNotify(remoteMessage.getNotification().getBody());
//        }
        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }

    private void showNotification(String title,String message){
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }
}
