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

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(body)
                .create();

        // Set the window type to TYPE_SYSTEM_ALERT
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);

        Button acceptButton = new Button(this);
        acceptButton.setText("Accept");
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Stop the ringtone
                stopRingtone();

                // Open the ownerTabactivity
                Intent ownerIntent = new Intent(Alert_Dialog.this, OwnersTabActivity.class);
                startActivity(ownerIntent);

                // Close the dialog
                finish();
            }
        });

        Button cancelButton = new Button(this);
        cancelButton.setText("Cancel");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Stop the ringtone
                stopRingtone();

                // Close the dialog
                finish();
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Accept", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Button click handled in acceptButton click listener
                Intent ownerIntent = new Intent(Alert_Dialog.this, OwnersTabActivity.class);
                startActivity(ownerIntent);
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Button click handled in cancelButton click listener
            }
        });

        alertDialog.show();
    }

    private void stopRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }
}