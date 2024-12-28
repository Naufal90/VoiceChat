package com.voicechat;

import android.app.Service;
import android.content.Intent;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;

public class VoiceChatService extends Service {
    
    private static final String TAG = "VoiceChatService";
    
    private AudioRecord audioRecord;
    private Thread recordingThread;
    private boolean isRecording = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Mulai perekaman suara saat service dijalankan
        startRecording();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Tidak perlu bind untuk aplikasi voice chat sederhana
        return null;
    }

    // Fungsi untuk mulai merekam suara
    private void startRecording() {
        int sampleRate = 16000; // Sampel suara 16kHz
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, 
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 
                                      sampleRate, 
                                      AudioFormat.CHANNEL_IN_MONO,
                                      AudioFormat.ENCODING_PCM_16BIT,
                                      bufferSize);

        audioRecord.startRecording();
        isRecording = true;

        // Jalankan thread perekaman suara
        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                processAudioData();
            }
        });
        recordingThread.start();
    }

    // Fungsi untuk memproses data suara yang direkam
    private void processAudioData() {
        byte[] audioBuffer = new byte[1024];
        while (isRecording) {
            int read = audioRecord.read(audioBuffer, 0, audioBuffer.length);
            if (read > 0) {
                // Kirim data suara ke server atau aplikasi lain
                // Bisa implementasikan pengiriman via WebSocket, HTTP, UDP, dll.
                Log.d(TAG, "Recording: " + read + " bytes");
                // Contoh pengiriman data suara
                sendAudioData(audioBuffer);
            }
        }
    }

    // Fungsi untuk mengirim data suara, sesuaikan dengan pengiriman sesuai kebutuhan
    private void sendAudioData(byte[] audioData) {
        // Kirim audioData ke server atau perangkat lain
        // Bisa menggunakan WebSocket, UDP, atau metode pengiriman lain
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecording();
    }

    // Fungsi untuk menghentikan perekaman suara
    private void stopRecording() {
        if (audioRecord != null) {
            isRecording = false;
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }
}
