package com.example.superdrop2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.superdrop2.payment.OrderPlacedActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyNotification extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Intent intent=new Intent(getApplicationContext(), OrderPlacedActivity.class);
        if(message.getData().size()>0) {
            String title = message.getData().get("title");
            String body=message.getData().get("body");
            showNotification(getApplicationContext(),title,body,intent);
        }
        showNotification(getApplicationContext(),message.getNotification().getTitle(),message.getNotification().getBody(),intent);
    }

    public void showNotification(Context context,String title,String message,Intent intent){
        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationid=1;
        String channelid ="Channel1";
        String channelName="My Channel";
        int importance=NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel notificationChannel=new NotificationChannel(channelid,channelName,importance);
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder mbuilder=new NotificationCompat.Builder(context,channelid)
                .setSmallIcon(R.drawable.cat_2)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        PendingIntent intent1=PendingIntent.getActivity(context,1,intent,PendingIntent.FLAG_MUTABLE);
        mbuilder.setContentIntent(intent1);
        notificationManager.notify(notificationid,mbuilder.build());
    }
}
