package com.example.electric.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.media.MediaPlayer;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.example.electric.R;

public class Music extends Service implements LifecycleObserver {

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // Đăng ký observer để theo dõi trạng thái vòng đời ứng dụng
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        // Khởi tạo MediaPlayer với nhạc nền từ tài nguyên
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        mediaPlayer.setLooping(true); // Đặt chế độ lặp cho nhạc

        // Khởi tạo AudioManager để điều chỉnh âm lượng
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Đặt âm lượng hệ thống lên mức tối đa
        audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0); // Không cần flags

        // Yêu cầu quyền điều khiển âm thanh
        requestAudioFocus();
    }

    // Yêu cầu quyền điều khiển âm thanh
    private void requestAudioFocus() {
        AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        // Nếu nhạc đã bị dừng tạm thời, tiếp tục phát lại
                        startMusic();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        // Mất quyền điều khiển âm thanh, dừng phát nhạc
                        stopMusic();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        // Mất quyền tạm thời, tạm dừng phát nhạc
                        pauseMusic();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        // Giảm âm lượng tạm thời khi không cần dừng hoàn toàn
                        if (mediaPlayer != null) {
                            mediaPlayer.setVolume(0.1f, 0.1f);
                        }
                        break;
                }
            }
        };

        // Yêu cầu quyền điều khiển âm thanh
        int result = audioManager.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        // Nếu có quyền điều khiển, bắt đầu phát nhạc
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            startMusic();
        }
    }

    // Bắt đầu phát nhạc
    private void startMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start(); // Phát nhạc
            mediaPlayer.setVolume(1.0f, 1.0f); // Đặt âm lượng tối đa
        }
    }

    // Dừng phát nhạc
    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop(); // Dừng nhạc
        }
    }

    // Tạm dừng phát nhạc
    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause(); // Tạm dừng nhạc
        }
    }

    // Ứng dụng vào nền, tạm dừng nhạc
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Dịch vụ sẽ tiếp tục chạy ngay cả khi bị đóng
    }

    @Override
    public void onDestroy() {
        // Giải phóng tài nguyên MediaPlayer
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        // Hủy quyền điều khiển âm thanh
        audioManager.abandonAudioFocus(null);
        // Xóa observer vòng đời ứng dụng
        ProcessLifecycleOwner.get().getLifecycle().removeObserver(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Không ràng buộc với bất kỳ component nào
    }
}
