package com.voicechat;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.AudioTrack;
import android.media.AudioManager;  // Impor ini tetap dipertahankan karena digunakan oleh Android SDK

public class MyAudioManager {  // Ganti nama kelas di sini

    private static final int SAMPLE_RATE = 16000;  // Contoh sample rate untuk audio
    private AudioRecord recorder;
    private AudioTrack player;

    public void startRecording() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        recorder.startRecording();
        // Tambahkan logika untuk mengirim suara ke server atau perangkat lain
    }

    public void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
        }
    }

    public void playAudio(byte[] audioData) {
        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        player = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);

        player.play();
        player.write(audioData, 0, audioData.length);
    }
}
