package com.demo.mypushappnotificationdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.mypushappnotificationdemo.notifier.RegistrationIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "101";
    public static MainActivity mainActivity;
    public static Boolean isVisible=false;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST=9000;
    String resultString = null;
    String regID = null;
    String storedToken = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity=this;
        //registerWithNotificationHubs();
        createNotificationChannel();
        getToken();
    }
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Log.d(TAG,"onComplete: Failed to get the Token");
                }
                //Token
                String token=task.getResult();
                getRegisteredWithNotificationHub(token);
                Log.d(TAG,"onComplete:"+token);
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "firebaseNotifChannel";
            String description = "Recieve Firebase Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void ToastNotify(final String notificationMessage){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, notificationMessage, Toast.LENGTH_LONG).show();
                TextView helloText=(TextView) findViewById(R.id.text_hello);
                helloText.setText(notificationMessage);
            }
        });
    }

    private boolean checkPlayServices(){
        GoogleApiAvailability apiAvailability=GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if (apiAvailability.isUserResolvableError(resultCode)){
                apiAvailability.getErrorDialog(this,resultCode,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else{
                Log.i(TAG, "This device is not supported by Google Play Services.");
                ToastNotify("This Device is not supported by Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }
    public void registerWithNotificationHubs(){
        if(checkPlayServices()){
            Intent intent=new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        isVisible=true;
    }
    @Override
    protected void onPause(){
        super.onPause();
        isVisible=false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        isVisible=true;
    }
    @Override
    protected void onStop(){
        super.onStop();
        isVisible=false;
    }

    public void getRegisteredWithNotificationHub(String FCM_token){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        try {
//            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
//                @Override
//                public void onSuccess(InstanceIdResult instanceIdResult) {
//                    FCM_token = instanceIdResult.getToken();
//                    Log.d(TAG, "FCM Registration Token: " + FCM_token);
//                }
//            });
//            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
//                @Override
//                public void onComplete(@NonNull Task<String> task) {
//                    if(!task.isSuccessful()){
//                        Log.d(TAG,"onComplete: Failed to get the Token");
//                    }
//                    //Token
//                    FCM_token=task.getResult();
//                    Log.d(TAG, "FCM Registration Token: " + FCM_token);
//                }
//            });
            TimeUnit.SECONDS.sleep(1);

            // Storing the registration ID that indicates whether the generated token has been
            // sent to your server. If it is not stored, send the token to your server.
            // Otherwise, your server should have already received the token.

                NotificationHub hub = new NotificationHub(NotificationSettings.HubName,
                        NotificationSettings.ConnectionString, this);
                Log.d(TAG, "Attempting a new registration with NH using FCM token : " + FCM_token);
                Thread thread =new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            regID = hub.register(FCM_token).getRegistrationId();
                            Log.d("RegistrationId",regID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
               thread.start();

                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/documentation/articles/notification-hubs-routing-tag-expressions/
                // regID = hub.register(token, "tag1,tag2").getRegistrationId();

                resultString = "New NH Registration Successfully - RegId : " + regID;
                Log.d(TAG, resultString);

                sharedPreferences.edit().putString("registrationID", regID);
                sharedPreferences.edit().putString("FCMtoken", FCM_token ).apply();

            // Check to see if the token has been compromised and needs refreshing.

        } catch (Exception e) {
            Log.e(TAG, resultString="Failed to complete registration", e);
            // If an exception happens while fetching the new token or updating registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }

        // Notify UI that registration has completed.
        if (MainActivity.isVisible) {
            MainActivity.mainActivity.ToastNotify("Hello world");
        }
    }


}