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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int SAMPLE_RATE = 44100; // 44.1 kHz
    private static final int BUFFER_SIZE = 1024; // Buffer size untuk audio
    private AudioRecord recorder;
    private AudioTrack player;
    private boolean isRecording = false;
    private Thread recordingThread;

    private Button btnStartRecording;
    private Button btnStopRecording;
    private RadioGroup modeSelection;
    private AdView adView; 
    private OkHttpClient client;

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
        // Periksa izin di AndroidManifest.xml sebelumnya

        btnStartRecording = findViewById(R.id.btnStartRecording);
        btnStopRecording = findViewById(R.id.btnStopRecording);
        modeSelection = findViewById(R.id.modeSelection);

        client = new OkHttpClient();

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
                    connectToPlugin();
                    break;
                case R.id.onlineMode:
                    showToast("Online Mode");
                    connectToMojangServer();
                    break;
                case R.id.vpnMode:
                    showToast("VPN Mode");
                    break;
            }
        });

        // Cek koneksi internet
        checkNetworkCapabilities();
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

        recorder.stop();
        player.stop();

        recorder.release();
        player.release();
        recordingThread = null;
    }

    private void connectToPlugin() {
        // Implementasi untuk terhubung ke plugin
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url("http://plugin_server_url") // Ganti dengan URL server plugin Anda
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Log.i("Plugin", "Connected to plugin");
                } else {
                    Log.e("Plugin", "Failed to connect to plugin");
                }
            } catch (Exception e) {
                Log.e("Plugin", "Error: " + e.getMessage());
            }
        }).start();
    }

    private void connectToMojangServer() {
        // Implementasi untuk terhubung ke server Mojang
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url("https://api.minecraft.net/connection_check") // Ganti dengan URL server Mojang
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Log.i("Mojang", "Connected to Mojang server");
                } else {
                    Log.e("Mojang", "Failed to connect to Mojang server");
                }
            } catch (Exception e) {
                Log.e("Mojang", "Error: " + e.getMessage());
            }
        }).start();
    }

    private void checkNetworkCapabilities() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                showToast("Internet is connected");
            } else {
                showToast("No internet connection");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording) {
            stopRecording();
        }
    }
            }
