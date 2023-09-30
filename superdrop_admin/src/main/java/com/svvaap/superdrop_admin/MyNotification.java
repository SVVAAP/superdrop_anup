package com.svvaap.superdrop_admin;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.example.superdrop_admin.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.svvaap.superdrop_admin.adapter.AlarmStopReceiver;

public class MyNotification extends FirebaseMessagingService {

    private static final int ALARM_INTERVAL = 60000; // 1 minute interval
    private static final int NOTIFICATION_ID = 0;
    public static MediaPlayer mediaPlayer;
    private static PendingIntent alarmIntent;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if (message.getData().size() > 0) {
            String title = message.getData().get("title");
            String body = message.getData().get("body");
            showNotification(title, body);

            if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                playNotificationSoundLikeAlarm();
            }
        }
        message.getNotification().getBody();
    }

    public void showNotification(String title, String message) {
        Intent intent = new Intent(this, OwnersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "Channel1";
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ring);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.cat_2)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, "My Channel", importance);
            notificationManager.createNotificationChannel(notificationChannel);

            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    private void playNotificationSoundLikeAlarm() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.ring);

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mediaPlayer.setAudioAttributes(attributes);
        }

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }

        // Schedule the alarm to stop after a set interval
        if (alarmIntent != null) {
            alarmIntent.cancel();
        }

        Intent alarmStopIntent = new Intent(this, AlarmStopReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, alarmStopIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Schedule the alarm to stop after ALARM_INTERVAL (1 minute)
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ALARM_INTERVAL, alarmIntent);
        }
    }
}
