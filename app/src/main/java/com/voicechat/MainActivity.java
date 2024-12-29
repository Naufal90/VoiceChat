package com.voicechat;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    private boolean isMuted = false;  // Status mute
    private Thread recordingThread;

    private Button btnStartRecording;
    private Button btnStopRecording;
    private Button btnMute;  // Tombol mute/unmute
    private RadioGroup modeSelection;
    private AdView adView; // Untuk iklan banner

    private String vpnHostIP; // Variabel untuk menyimpan IP host VPN

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi iklan AdMob
        MobileAds.initialize(this, initializationStatus -> {});
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // Panggil fungsi cek DNS AdGuard
        checkAdGuardDNS();

        // Inisialisasi tombol dan elemen UI lainnya
        btnStartRecording = findViewById(R.id.btnStartRecording);
        btnStopRecording = findViewById(R.id.btnStopRecording);
        btnMute = findViewById(R.id.btnMute);  // Tombol mute
        modeSelection = findViewById(R.id.modeSelection);

        // Tambahkan listener pada tombol
        btnStartRecording.setOnClickListener(v -> {
            startRecording();
            showToast("Rekaman dimulai.");
        });

        btnStopRecording.setOnClickListener(v -> {
            stopRecording();
            showToast("Rekaman dihentikan.");
        });

        btnMute.setOnClickListener(v -> {
            toggleMute();  // Fungsi untuk toggle mute/unmute
        });

        // Tambahkan listener untuk pemilihan mode
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
                    showVpnHostDialog(); // Tampilkan dialog untuk memasukkan IP host VPN
                    break;
            }
        });
    }

    private void checkAdGuardDNS() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            NetworkCapabilities networkCapabilities =
                    connectivityManager.getNetworkCapabilities(activeNetwork);

            if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                // Cek apakah Private DNS diaktifkan
                String privateDnsSetting = Settings.Global.getString(getContentResolver(), "private_dns_mode");
                String privateDnsSpec = Settings.Global.getString(getContentResolver(), "private_dns_specifier");

                if ("hostname".equals(privateDnsSetting) && privateDnsSpec != null && privateDnsSpec.contains("adguard")) {
                    // Deteksi DNS AdGuard
                    showAdGuardWarning();
                }
            }
        }
    }

    private void showAdGuardWarning() {
        new AlertDialog.Builder(this)
                .setTitle("Peringatan DNS AdGuard")
                .setMessage("Aplikasi mendeteksi bahwa Anda menggunakan DNS AdGuard. Matikan DNS AdGuard untuk melanjutkan.")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    finish(); // Keluar dari aplikasi
                })
                .setCancelable(false)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showVpnHostDialog() {
        // Buat dialog untuk memasukkan IP host
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Masukkan IP Host VPN");

        // Tambahkan input field
        final EditText input = new EditText(this);
        input.setHint("Masukkan IP Host");
        builder.setView(input);

        // Tambahkan tombol OK dan Cancel
        builder.setPositiveButton("OK", (dialog, which) -> {
            vpnHostIP = input.getText().toString().trim();
            if (!vpnHostIP.isEmpty()) {
                showToast("IP Host VPN disetel: " + vpnHostIP);
            } else {
                showToast("IP Host VPN tidak boleh kosong.");
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void toggleMute() {
        isMuted = !isMuted;  // Toggle status mute
        if (isMuted) {
            showToast("Audio dimute.");
        } else {
            showToast("Audio tidak dimute.");
        }
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
                    if (!isMuted) {  // Jika tidak mute, data audio diteruskan
                        player.write(audioBuffer, 0, readResult);
                    }
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
        }ng();
        }
    }
}
