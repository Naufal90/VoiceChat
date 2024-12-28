package com.voicechat;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SAMPLE_RATE = 44100; // Sample rate untuk audio
    private static final int BUFFER_SIZE = 1024; // Ukuran buffer
    private AudioRecord recorder;
    private AudioTrack player;
    private boolean isRecording = false;
    private Thread recordingThread;

    private Button btnStartRecording;
    private Button btnStopRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Memastikan izin mikrofon telah diberikan
        // Memeriksa izin harus dilakukan sebelumnya (di AndroidManifest.xml)

        btnStartRecording = findViewById(R.id.btnStartRecording);
        btnStopRecording = findViewById(R.id.btnStopRecording);

        btnStartRecording.setOnClickListener(v -> startRecording());
        btnStopRecording.setOnClickListener(v -> stopRecording());
    }

    private void startRecording() {
        if (isRecording) return;

        recorder = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                BUFFER_SIZE);

        if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e("AudioRecord", "Failed to initialize AudioRecord.");
            return;
        }

        // Inisialisasi AudioTrack untuk memutar suara yang diterima
        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

        if (bufferSize == AudioTrack.ERROR_BAD_VALUE || bufferSize == AudioTrack.ERROR) {
            Log.e("AudioTrack", "Invalid buffer size.");
            return;
        }

        player = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);

        if (player.getState() != AudioTrack.STATE_INITIALIZED) {
            Log.e("AudioTrack", "Failed to initialize AudioTrack.");
            return;
        }

        recorder.startRecording();
        player.play();

        isRecording = true;

        // Thread untuk membaca data dari mikrofon dan memainkannya melalui AudioTrack
        recordingThread = new Thread(() -> {
            byte[] audioBuffer = new byte[BUFFER_SIZE];
            while (isRecording) {
                int readResult = recorder.read(audioBuffer, 0, audioBuffer.length);
                if (readResult != AudioRecord.ERROR_INVALID_OPERATION) {
                    player.write(audioBuffer, 0, readResult);
                }
            }
        });
        recordingThread.start();
    }

    private void stopRecording() {
        if (!isRecording) return;

        isRecording = false;

        // Hentikan rekaman dan pemutaran
        recorder.stop();
        player.stop();

        // Bersihkan sumber daya
        recorder.release();
        player.release();
        recordingThread = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording) {
            stopRecording();
        }
    }
}
