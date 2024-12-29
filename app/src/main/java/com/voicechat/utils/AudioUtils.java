package com.voicechat;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

public class AudioUtils {

    private static final String TAG = "AudioUtils";

    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;
    private static AudioManager audioManager;

    // Memulai perekaman suara
    public static void startRecording(Context context, String filePath) {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(filePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Log.d(TAG, "Recording started");
        } catch (IOException e) {
            Log.e(TAG, "startRecording: ", e);
        }
    }

    // Menghentikan perekaman suara
    public static void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Log.d(TAG, "Recording stopped");
        }
    }

    // Memulai pemutaran suara
    public static void startPlaying(Context context, String filePath) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Log.d(TAG, "Playing started");
        } catch (IOException e) {
            Log.e(TAG, "startPlaying: ", e);
        }
    }

    // Menghentikan pemutaran suara
    public static void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d(TAG, "Playing stopped");
        }
    }

    // Mengatur volume perangkat
    public static void setVolume(Context context, int volumeLevel) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int volume = (int) (maxVolume * (volumeLevel / 100.0));
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
            Log.d(TAG, "Volume set to: " + volumeLevel + "%");
        }
    }

    // Mendapatkan volume perangkat
    public static int getVolume(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            return (int) ((currentVolume / (float) maxVolume) * 100);
        }
        return 0; // Default return if audioManager is null
    }

    // Mengecek apakah perekaman sedang berlangsung
    public static boolean isRecording() {
        return mediaRecorder != null;
    }

    // Mengecek apakah pemutaran suara sedang berlangsung
    public static boolean isPlaying() {
        return mediaPlayer != null;
    }
}
