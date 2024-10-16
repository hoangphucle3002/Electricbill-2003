package com.example.electric.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.media.MediaPlayer;
import android.util.Log;
import android.media.AudioManager.OnAudioFocusChangeListener;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.example.electric.R;


public class MusicService extends Service implements LifecycleObserver {

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private boolean wasPlayingBeforeLoss = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // Register lifecycle observer to monitor app state
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        mediaPlayer.setLooping(true);

        // Initialize AudioManager
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Request audio focus
        requestAudioFocus();
    }

    // Request audio focus
    private void requestAudioFocus() {
        AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        if (wasPlayingBeforeLoss) {
                            startMusic();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        stopMusic();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        pauseMusic();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        if (mediaPlayer != null) {
                            mediaPlayer.setVolume(0.1f, 0.1f);
                        }
                        break;
                }
            }
        };

        int result = audioManager.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            startMusic();
        }
    }

    // Bắt đầu phát nhạc
    private void startMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            wasPlayingBeforeLoss = true;
        }
    }

    // Dừng nhạc
    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            wasPlayingBeforeLoss = false;
        }
    }

    // Tạm dừng nhạc
    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            wasPlayingBeforeLoss = true;
        }
    }

    // App đi vào background
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        pauseMusic();
    }

    // App quay lại foreground
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        startMusic();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        audioManager.abandonAudioFocus(null);
        ProcessLifecycleOwner.get().getLifecycle().removeObserver(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
