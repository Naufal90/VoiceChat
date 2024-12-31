package com.voicechat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;

import android.net.wifi.WifiManager;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String AUDIO_FILE_PATH = "/storage/emulated/0/VoiceChat/audio_file.3gp";
    private static final String TAG = "VoiceChatApp";
    private static final int REQUEST_CODE_PERMISSIONS = 1;

    private OfflineMode offlineMode;
    private ClientMode clientMode;
    private PluginMode pluginMode;

    private Button recordButton, stopRecordButton, playButton, stopPlayButton, sendButton, startHotspotButton, connectButton;
    private EditText commandEditText;
    private AdView mAdView;

    private MediaRecorder recorder;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Memastikan semua izin telah diberikan
        requestPermissions();

        offlineMode = new OfflineMode(this);
        clientMode = new ClientMode(this);
        pluginMode = new PluginMode(this);

        // Memulai hotspot
        offlineMode.startHotspot();

        // Memeriksa status hotspot
        offlineMode.verifyHotspotStatus();

        // Memeriksa koneksi ke hotspot
        String hotspotSSID = "NamaHotspot";
        clientMode.verifyConnection(hotspotSSID);
        clientMode.connectToHotspot(hotspotSSID);

        // Mengirim data ke plugin
        String serverUrl = "http://example.com/plugin-endpoint";
        String command = "{ \"action\": \"start\" }";
        pluginMode.sendDataToPlugin(serverUrl, command);

        // Inisialisasi AdMob
        MobileAds.initialize(this, initializationStatus -> {});
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Inisialisasi UI
        initUI();

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initUI() {
        recordButton = findViewById(R.id.recordButton);
        stopRecordButton = findViewById(R.id.stopRecordButton);
        playButton = findViewById(R.id.playButton);
        stopPlayButton = findViewById(R.id.stopPlayButton);
        sendButton = findViewById(R.id.sendButton);
        startHotspotButton = findViewById(R.id.startHotspotButton);
        connectButton = findViewById(R.id.connectButton);
        commandEditText = findViewById(R.id.commandEditText);

        recordButton.setOnClickListener(v -> startRecording());
        stopRecordButton.setOnClickListener(v -> stopRecording());
        playButton.setOnClickListener(v -> startPlaying());
        stopPlayButton.setOnClickListener(v -> stopPlaying());
        sendButton.setOnClickListener(v -> sendCommand());
        startHotspotButton.setOnClickListener(v -> startHotspot());
        connectButton.setOnClickListener(v -> connectToHotspot("SSID_HOTSPOT", "PASSWORD_HOTSPOT"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Menghentikan hotspot
        offlineMode.stopHotspot();
    }

    private void startRecording() {
        try {
            File folder = new File(getExternalFilesDir(null), "VoiceChat");
            if (!folder.exists() && !folder.mkdirs()) {
                showErrorToast("Gagal membuat folder untuk menyimpan audio");
                return;
            }

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(AUDIO_FILE_PATH);

            recorder.prepare();
            recorder.start();
            showSuccessToast("Perekaman dimulai");
        } catch (IOException e) {
            e.printStackTrace();
            showErrorToast("Gagal memulai perekaman");
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            showSuccessToast("Perekaman dihentikan");
        }
    }

    private void startPlaying() {
        File audioFile = new File(AUDIO_FILE_PATH);
        if (!audioFile.exists()) {
            Toast.makeText(this, "File audio tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        if (player != null) {
            player.stop();
            player.release
                }
    }
