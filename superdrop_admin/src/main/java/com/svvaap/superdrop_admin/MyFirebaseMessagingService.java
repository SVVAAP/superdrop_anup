package com.svvaap.superdrop_admin;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.superdrop_admin.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("msg", "onMessageReceived: " + remoteMessage.getData().get("message"));
        Intent intent = new Intent(this, OwnersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        String channelId = "Default";
        NotificationCompat.Builder builder2 = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true).setContentIntent(pendingIntent)
                .setColor(Color.BLUE)
                .addAction(R.mipmap.ic_launcher,"Accept",pendingIntent)
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_MAX);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder2.build());

//        Intent intent1 = new Intent(getApplicationContext(), Alert_Dialog.class);
//        intent1.putExtra("title",remoteMessage.getNotification().getTitle());
//        intent1.putExtra("body",remoteMessage.getNotification().getBody());
//        try{
//            PendingIntent.getActivity(getApplicationContext(),0,intent1,PendingIntent.FLAG_IMMUTABLE).send();
//        }catch (Exception e){
//            Log.d("FMC ERROR",e.getMessage());
//        }

        // Create and show an AlertDialog with accept and reject buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(remoteMessage.getNotification().getTitle());
        builder.setMessage(remoteMessage.getNotification().getBody());
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the Accept button click here
                dialog.dismiss(); // Close the dialog
                stopRingtoneAndVibration(); // Stop ringtone and vibration
                // Start your desired activity
                startActivity(new Intent(getApplicationContext(), OwnersActivity.class));
            }
        });
        builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the Reject button click here
                dialog.dismiss(); // Close the dialog
                stopRingtoneAndVibration(); // Stop ringtone and vibration
            }
        });
        builder.setCancelable(false); // Prevent dismissal by tapping outside the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Start continuous ringtone and vibration
        startRingtoneAndVibration();
    }

    private Ringtone ringtone;
    private Vibrator vibrator;

    // Start continuous ringtone and vibration
    private void startRingtoneAndVibration() {
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (ringtone != null) {
            ringtone.play();
        }

        long[] pattern = {0, 1000, 1000}; // Vibrate for 1 second, then pause for 1 second
        if (vibrator != null) {
            vibrator.vibrate(pattern, 0);
        }
    }

    // Stop ringtone and vibration
    private void stopRingtoneAndVibration() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

}

