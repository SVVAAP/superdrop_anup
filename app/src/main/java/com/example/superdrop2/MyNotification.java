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
        Intent intent = new Intent(getApplicationContext(), Track_Order_Activity.class);
        if (message.getData().size() > 0) {
            String title = message.getData().get("title");
            String body = message.getData().get("body");
            showNotification(getApplicationContext(), title, body, intent);
        }
        showNotification(getApplicationContext(), message.getNotification().getTitle(), message.getNotification().getBody(), intent);
    }

    public void showNotification(Context context, String title, String message, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        String channelId = "Channel1";
        String channelName = "My Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        // Create a notification channel (only if it doesn't exist)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.cat_2)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        // Create a PendingIntent to open the Track_Order_Activity
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Notify using the NotificationManager
        notificationManager.notify(notificationId, builder.build());
    }
}
