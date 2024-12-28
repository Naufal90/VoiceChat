package com.voicechat;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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
    private RadioGroup modeSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartRecording = findViewById(R.id.btnStartRecording);
        btnStopRecording = findViewById(R.id.btnStopRecording);
        modeSelection = findViewById(R.id.modeSelection);

        // Set default button states
        btnStartRecording.setEnabled(true);
        btnStopRecording.setEnabled(false);

        // Mode Selection Listener
        modeSelection.setOnCheckedChangeListener((group, checkedId) -> {
            String mode = "";
            switch (checkedId) {
                case R.id.offlineMode:
                    mode = "Offline Mode";
                    break;
                case R.id.tunnelMode:
                    mode = "Tunnel Mode";
                    break;
                case R.id.pluginMode:
                    mode = "Plugin Mode";
                    break;
                case R.id.onlineMode:
                    mode = "Online Mode";
                    break;
                case R.id.vpnMode:
                    mode = "VPN Mode";
                    break;
            }
            // Show Toast when mode is selected
            Toast.makeText(getApplicationContext(), mode + " Selected", Toast.LENGTH_SHORT).show();
        });

        // Start Recording Button Listener
        btnStartRecording.setOnClickListener(v -> startRecording());

        // Stop Recording Button Listener
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

        // Show Toast when Recording Starts
        Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_SHORT).show();

        // Disable Start Recording and Enable Stop Recording
        btnStartRecording.setEnabled(false);
        btnStopRecording.setEnabled(true);
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

        // Show Toast when Recording Stops
        Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_SHORT).show();

        // Disable Stop Recording and Enable Start Recording
        btnStopRecording.setEnabled(false);
        btnStartRecording.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording) {
            stopRecording();
        }
    }
}
