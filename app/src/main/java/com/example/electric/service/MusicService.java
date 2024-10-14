package com.example.electric.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import com.example.electric.R;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        // Khởi tạo MediaPlayer với tệp nhạc từ thư mục raw
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music); // Sử dụng tên tệp của bạn
        mediaPlayer.setLooping(true); // Đặt nhạc lặp liên tục
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start(); // Bắt đầu phát nhạc
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop(); // Dừng phát nhạc khi service bị hủy
        }
        mediaPlayer.release(); // Giải phóng tài nguyên MediaPlayer
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Không cần bind cho service này
    }
}
