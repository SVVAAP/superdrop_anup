package com.example.superdrop_admin;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.CommonNotificationBuilder;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyNotification extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Intent intent=new Intent(getApplicationContext(),OwnersActivity.class);
        if(message.getData().size()>0) {
            String title = message.getData().get("title");
            String body=message.getData().get("body");
            showNotification(getApplicationContext(),title,body,intent);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View dialogView = inflater.inflate(R.layout.notification_call, null);
            builder.setView(dialogView);

            // Retrieve views from the custom layout
            TextView titleTextView = dialogView.findViewById(R.id.notification_title);
            Button acceptButton = dialogView.findViewById(R.id.accept_button);
            Button rejectButton = dialogView.findViewById(R.id.reject_button);

            // Set the title
            titleTextView.setText(message.getData().get("title"));

            // Create the AlertDialog
            final AlertDialog alertDialog = builder.create();

            // Set the behavior for the Accept button
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the acceptance action here (e.g., open the tab activity)
                    Intent intent = new Intent(getApplicationContext(), OwnersTabActivity.class);
                    startActivity(intent);
                    alertDialog.dismiss(); // Close the dialog
                }
            });

            // Set the behavior for the Reject button
            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss(); // Close the dialog
                }
            });

            // Show the AlertDialog as a call notification
            alertDialog.show();
        } else {
            // Handle other types of notifications here
        }


        showNotification(getApplicationContext(),message.getNotification().getTitle(),message.getNotification().getBody(),intent);
    }
    
    public void showNotification(Context context,String title,String message,Intent intent){
        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationid=1;
        String channelid ="Channel1";
        String channelName="My Channel";
        int importance=NotificationManager.IMPORTANCE_HIGH;
        Uri soundUri=Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ring);

        NotificationChannel notificationChannel=new NotificationChannel(channelid,channelName,importance);
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder mbuilder=new NotificationCompat.Builder(context,channelid)
                .setSmallIcon(R.drawable.cat_2)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(soundUri);

        PendingIntent intent1=PendingIntent.getActivity(context,1,intent,PendingIntent.FLAG_MUTABLE);
        mbuilder.setContentIntent(intent1);
        notificationManager.notify(notificationid,mbuilder.build());
    }
}
