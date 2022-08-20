package com.Segev.QuizTime;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MusicService extends Service {

    private MediaPlayer mp;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // start the music and keep it going until the stopping
        mp = MediaPlayer.create(this, R.raw.song);
        mp.setLooping(true);
        mp.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // stop the music
        mp.stop();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
