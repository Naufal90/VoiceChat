package com.voicechat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {
    private static final String AUDIO_FILE_PATH = "/storage/emulated/0/VoiceChat/audio_file.3gp"; // Tentukan path file audio yang benar
    private static final int REQUEST_CODE_RECORD_AUDIO = 1;

    private Button recordButton;
    private Button stopRecordButton;
    private Button playButton;
    private Button stopPlayButton;
    private Button sendButton;
    private Button startHotspotButton;
    private Button connectButton;
    private EditText commandEditText;

    private AdView mAdView;

    private MediaRecorder recorder;
    private MediaPlayer player;

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
        commandEditText = findViewById(R.id.commandEditText);
        mAdView = findViewById(R.id.adView);

        // Menampilkan banner iklan
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Meminta izin saat aplikasi pertama kali dijalankan
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_RECORD_AUDIO);
        }

        // Tombol untuk memulai perekaman
        recordButton.setOnClickListener(v -> startRecording());

        // Tombol untuk menghentikan perekaman
        stopRecordButton.setOnClickListener(v -> stopRecording());

        // Tombol untuk memutar suara
        playButton.setOnClickListener(v -> startPlaying());

        // Tombol untuk menghentikan pemutaran suara
        stopPlayButton.setOnClickListener(v -> stopPlaying());

        // Tombol untuk mengirim perintah
        sendButton.setOnClickListener(v -> {
            String command = commandEditText.getText().toString();
            Toast.makeText(this, "Perintah dikirim: " + command, Toast.LENGTH_SHORT).show();
        });

        // Tombol untuk memulai hotspot (ini hanya contoh, Anda perlu menambahkannya sesuai implementasi)
        startHotspotButton.setOnClickListener(v -> {
            // Implementasikan logika untuk memulai hotspot
            Toast.makeText(this, "Hotspot dimulai", Toast.LENGTH_SHORT).show();
        });

        // Tombol untuk menghubungkan ke hotspot (implementasi sesuai dengan logika yang diinginkan)
        connectButton.setOnClickListener(v -> {
            Toast.makeText(this, "Mencoba menghubungkan ke hotspot", Toast.LENGTH_SHORT).show();
        });
    }

    // Fungsi untuk memulai perekaman audio
    private void startRecording() {
        if (recorder == null) {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(AUDIO_FILE_PATH);
            try {
                recorder.prepare();
                recorder.start();
                Toast.makeText(this, "Perekaman dimulai", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memulai perekaman", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Fungsi untuk menghentikan perekaman audio
    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            Toast.makeText(this, "Perekaman dihentikan", Toast.LENGTH_SHORT).show();
        }
    }

    // Fungsi untuk memutar audio
    private void startPlaying() {
        if (player == null) {
            player = new MediaPlayer();
            try {
                player.setDataSource(AUDIO_FILE_PATH);
                player.prepare();
                player.start();
                Toast.makeText(this, "Memulai pemutaran audio", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memutar audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Fungsi untuk menghentikan pemutaran audio
    private void stopPlaying() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
            Toast.makeText(this, "Pemutaran audio dihentikan", Toast.LENGTH_SHORT).show();
        }
    }

    // Menangani izin yang diminta oleh aplikasi
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, Anda dapat mulai merekam
                Toast.makeText(this, "Izin diberikan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Izin ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
