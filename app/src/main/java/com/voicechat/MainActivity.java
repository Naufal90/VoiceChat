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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

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
    private AdView adView; // Untuk iklan banner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi iklan AdMob
        MobileAds.initialize(this, initializationStatus -> {});
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Memastikan izin mikrofon telah diberikan
        // Memeriksa izin harus dilakukan sebelumnya (di AndroidManifest.xml)

        btnStartRecording = findViewById(R.id.btnStartRecording);
        btnStopRecording = findViewById(R.id.btnStopRecording);
        modeSelection = findViewById(R.id.modeSelection);

        btnStartRecording.setOnClickListener(v -> startRecording());
        btnStopRecording.setOnClickListener(v -> stopRecording());

        modeSelection.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.offlineMode:
                    showToast("Offline Mode");
                    break;
                case R.id.tunnelMode:
                    showToast("Tunnel Mode");
                    break;
                case R.id.pluginMode:
                    showToast("Plugin Mode");
                    break;
                case R.id.onlineMode:
                    showToast("Online Mode");
                    break;
                case R.id.vpnMode:
                    showToast("VPN Mode");
                    break;
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
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
