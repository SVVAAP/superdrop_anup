package com.svvaap.superdrop_admin;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.example.superdrop_admin.R;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.example.superdrop_admin.R;

public class BackgroundMusicService extends Service {
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public BackgroundMusicService getService() {
            return BackgroundMusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.addtocart_music);
        mediaPlayer.setLooping(true);
    }

    public void startMusic() {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start();
            isPlaying = true;

            // Log a message indicating that music is playing
            System.out.println("Music is playing");
        }
    }

    public void stopMusic() {
        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;

            // Log a message indicating that music is stopped
            System.out.println("Music is stopped");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
