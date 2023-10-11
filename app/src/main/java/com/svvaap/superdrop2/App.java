package com.svvaap.superdrop2;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_1_ID="chammel1";

    @Override
    public void onCreate(){
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels(){
        NotificationChannel channel =new NotificationChannel(CHANNEL_1_ID,"channel 1", NotificationManager.IMPORTANCE_HIGH);
       NotificationManager manager=getSystemService(NotificationManager.class);
       manager.createNotificationChannel(channel);
    }
}
