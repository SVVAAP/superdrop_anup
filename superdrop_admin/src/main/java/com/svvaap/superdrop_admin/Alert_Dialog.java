package com.svvaap.superdrop_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.superdrop_admin.R;

public class Alert_Dialog extends AppCompatActivity {
    private Ringtone ringtone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialog);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String title = bundle != null ? bundle.getString("title") : null;
        String body = bundle != null ? bundle.getString("body") : null;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(title)
                .setMessage(body);

        // Set the window type to TYPE_SYSTEM_ALERT
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
        if(ringtone!=null){
            ringtone.play();
        }
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Open notification access settings
                stopRingtone();
                // Open the ownerTabactivity
                Intent ownerIntent = new Intent(Alert_Dialog.this, OwnersTabActivity.class);
                startActivity(ownerIntent);
                // Close the dialog
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the user's choice to cancel
                stopRingtone();
               // Close the dialog
                onBackPressed();
//                Intent previousActivityIntent = new Intent(Alert_Dialog.this, MainActivity.class);
//                startActivity(previousActivityIntent);
//                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        alertDialog.show();

    }

    private void stopRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }
}