package com.voicechat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String AUDIO_FILE_PATH = "/path/to/audio/file.3gp"; // Tentukan path file audio
    private Button recordButton;
    private Button stopRecordButton;
    private Button playButton;
    private Button stopPlayButton;
    private Button sendButton;
    private Button startHotspotButton;
    private Button connectButton;
    private Button loginButton;
    private EditText commandEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private AdView mAdView;

    private OfflineMode offlineMode;
    private ClientMode clientMode;
    private PluginMode pluginMode;
    private OnlineMode onlineMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi AdMob
        MobileAds.initialize(this, initializationStatus -> {});

        // Inisialisasi komponen UI
        recordButton = findViewById(R.id.recordButton);
        stopRecordButton = findViewById(R.id.stopRecordButton);
        playButton = findViewById(R.id.playButton);
        stopPlayButton = findViewById(R.id.stopPlayButton);
        sendButton = findViewById(R.id.sendButton);
        startHotspotButton = findViewById(R.id.startHotspotButton);
        connectButton = findViewById(R.id.connectButton);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        commandEditText = findViewById(R.id.commandEditText);
        mAdView = findViewById(R.id.adView);

        // Inisialisasi mode
        offlineMode = new OfflineMode(this);
        clientMode = new ClientMode(this);
        pluginMode = new PluginMode(this);
        onlineMode = new OnlineMode(this);

        // Menampilkan banner iklan
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Meminta izin saat aplikasi pertama kali dijalankan
        PermissionUtils.requestPermissions(this);

        // Mengecek apakah perangkat terhubung ke internet
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
        }

        // Tombol untuk memulai perekaman
        recordButton.setOnClickListener(v -> {
            if (!AudioUtils.isRecording()) {
                AudioUtils.startRecording(this, AUDIO_FILE_PATH);
            }
        });

        // Tombol untuk menghentikan perekaman
        stopRecordButton.setOnClickListener(v -> {
            if (AudioUtils.isRecording()) {
                AudioUtils.stopRecording();
            }
        });

        // Tombol untuk memutar suara
        playButton.setOnClickListener(v -> {
            if (!AudioUtils.isPlaying()) {
                AudioUtils.startPlaying(this, AUDIO_FILE_PATH);
            }
        });

        // Tombol untuk menghentikan pemutaran suara
        stopPlayButton.setOnClickListener(v -> {
            if (AudioUtils.isPlaying()) {
                AudioUtils.stopPlaying();
            }
        });

        // Tombol untuk memulai hotspot
        startHotspotButton.setOnClickListener(v -> {
            offlineMode.startHotspot(); // Player 1 mulai hotspot
        });

        // Tombol untuk menghubungkan ke hotspot
        connectButton.setOnClickListener(v -> {
            String hotspotSSID = "Player";
