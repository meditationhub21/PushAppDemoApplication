package com.example.mypushappnotificationdemo2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypushappnotificationdemo2.Settings.NotificationSettings;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;

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

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity=this;
        createNotificationChannel();
        TextView messageTextView = (TextView) findViewById(R.id.getText);
        Button btn=(Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=messageTextView.getText().toString();
                Log.d("User entered: ",name);
                getToken(name);
            }
        });


    }

    private void getToken(String userTag){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Log.d(TAG,"onComplete: Failed to get the Token");
                }
                //Token
                String token=task.getResult();
                getRegisteredWithNotificationHub(token,userTag);
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
                // TextView helloText=(TextView) findViewById(R.id.text_hello);
                // helloText.setText(notificationMessage);
            }
        });
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

    public void getRegisteredWithNotificationHub(String FCM_token,String tag){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        try {
            TimeUnit.SECONDS.sleep(1);

            NotificationHub hub = new NotificationHub(NotificationSettings.HubName,
                    NotificationSettings.ConnectionString, this);
            Log.d(TAG, "Attempting a new registration with NH using FCM token : " + FCM_token);
            Thread thread =new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        regID = hub.register(FCM_token,tag).getRegistrationId();
                        Log.d("RegistrationId",regID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

            resultString = "New NH Registration Successfully - RegId : " + regID;
            Log.d(TAG, resultString);

            sharedPreferences.edit().putString("registrationID", regID);
            sharedPreferences.edit().putString("FCMtoken", FCM_token ).apply();

        } catch (Exception e) {
            Log.e(TAG, resultString="Failed to complete registration", e);
        }

        if (MainActivity.isVisible) {
            MainActivity.mainActivity.ToastNotify("Hello world");
        }
    }






}