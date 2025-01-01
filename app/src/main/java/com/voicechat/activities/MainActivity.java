package com.voicechat.activities;

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

import com.voicechat.R;
import com.voicechat.models.OfflineMode;
import com.voicechat.models.ClientMode;
import com.voicechat.models.PluginMode;
import com.voicechat.models.VpnMode;

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
    private static final int REQUEST_CODE = 100;

    private OfflineMode offlineMode;
    private ClientMode clientMode;
    private PluginMode pluginMode;
    private VpnMode vpnMode;

    private Button recordButton, stopRecordButton, playButton, stopPlayButton, sendButton, startHotspotButton, connectButton;
    private EditText etServerUrl, etCommand;
    private EditText commandEditText;
    private AdView mAdView;

    private MediaRecorder recorder;
    private MediaPlayer player;

    private LogWriter logWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Memastikan semua izin telah diberikan
        requestPermissions();

        offlineMode = new OfflineMode(this);
        clientMode = new ClientMode(this);
        pluginMode = new PluginMode(this);
        vpnMode = new VpnMode(this);
        logWriter = new LogWriter(this);

        // Memulai hotspot
        offlineMode.startHotspot();

        // Memeriksa status hotspot
        offlineMode.verifyHotspotStatus();

        vpnMode.checkVpnConnection();

        // Memeriksa koneksi ke hotspot
        String hotspotSSID = "NamaHotspot";
        clientMode.verifyConnection(hotspotSSID);
        clientMode.connectToHotspot(hotspotSSID);

        // Mengirim data ke plugin
        String serverUrl = "http://example.com/plugin-endpoint";
        String command = "{ \"action\": \"start\" }";
        pluginMode.sendDataToPlugin(serverUrl, command);

        logWriter.writerLog("Aplikasi Dimulai");

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
        etServerUrl = findViewById(R.id.etServerUrl);
        etCommand = findViewById(R.id.etCommand);

        recordButton.setOnClickListener(v -> startRecording());
        stopRecordButton.setOnClickListener(v -> stopRecording());
        playButton.setOnClickListener(v -> startPlaying());
        stopPlayButton.setOnClickListener(v -> stopPlaying());
        sendButton.setOnClickListener(v -> sendCommand());
        startHotspotButton.setOnClickListener(v -> startHotspot());
        connectButton.setOnClickListener(v -> connectToHotspot("SSID_HOTSPOT", "PASSWORD_HOTSPOT"));
    }

    private void requestPermissions() {
        // Daftar izin yang diperlukan
        String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.VIBRATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            Manifest.permission.ACCESS_VPN_STATE
        };

        // Mengecek izin yang belum diberikan dan meminta izin yang diperlukan
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE);
            }
        }
    }

    // Menangani hasil permintaan izin
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Izin " + permissions[i] + " ditolak", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showSuccessToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
}

private void showErrorToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
}

    @Override
protected void onDestroy() {
    super.onDestroy();
    // Menghentikan hotspot
    offlineMode.stopHotspot();
    logWriter.writerLog("Aplikasi Dihentikan");

    // Pastikan untuk melepaskan resource recorder dan player
    if (recorder != null) {
        recorder.release();
        recorder = null;
    }

    if (player != null) {
        player.release();
        player = null;
    }
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
        player.release();
    }

    player = new MediaPlayer();
    try {
        player.setDataSource(audioFile.getAbsolutePath());
        player.prepare();
        player.start();
        showSuccessToast("Pemutaran dimulai");
    } catch (IOException e) {
        e.printStackTrace();
        showErrorToast("Gagal memutar audio");
    }
    }

    @Override
public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_help, menu);
    return true;
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.item_help) {
        showHelp();
        return true;
    }
    return super.onOptionsItemSelected(item);
}

private void showHelp() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Panduan Penggunaan Aplikasi")
           .setMessage("Aplikasi Voice Chat menyediakan beberapa fitur untuk memudahkan komunikasi suara antar perangkat. Berikut adalah cara menggunakan aplikasi ini:\n\n" +
                       "1. **Pilih Mode**: Pilih salah satu mode yang sesuai dengan kebutuhan Anda:\n" +
                       "   - **Offline Mode**: Mode untuk menggunakan aplikasi tanpa koneksi internet (Wi-Fi atau data seluler). Cocok untuk penggunaan lokal antar perangkat.\n" +
                       "   - **Plugin Mode**: Mode yang digunakan untuk menghubungkan aplikasi dengan plugin Minecraft. Cocok untuk berkomunikasi dengan pemain Minecraft menggunakan voice chat.\n" +
                       "   - **VPN Mode**: Mode ini digunakan untuk koneksi aman menggunakan jaringan VPN. Berguna jika Anda ingin memastikan koneksi yang lebih aman antar perangkat.\n\n" +
                       
                       "2. **Mulai Perekaman Suara**: Tekan tombol **'Start Recording'** untuk mulai merekam suara Anda.\n" +
                       "   - Setelah perekaman dimulai, Anda dapat berbicara dan suara Anda akan direkam.\n\n" +
                       
                       "3. **Hentikan Perekaman**: Tekan tombol **'Stop Recording'** untuk menghentikan perekaman suara Anda.\n" +
                       "   - Suara yang telah direkam akan disimpan dan dapat diputar ulang.\n\n" +
                       
                       "4. **Putar Suara yang Terekam**: Tekan tombol **'Play'** untuk memutar suara yang telah direkam sebelumnya.\n" +
                       "   - Jika Anda ingin menghentikan pemutaran suara, tekan tombol **'Stop'**.\n\n" +
                       
                       "5. **Hotspot dan Koneksi**: Anda bisa menggunakan tombol **'Start Hotspot'** untuk membuat hotspot pribadi (hanya jika perangkat Anda mendukungnya).\n" +
                       "   - Tekan tombol **'Connect'** untuk menghubungkan perangkat lain ke hotspot yang telah dibuat.\n\n" +
                       
                       "6. **Mengirim Perintah**: Anda dapat menggunakan tombol **'Send'** untuk mengirim perintah yang Anda ketikkan di kolom teks. Perintah ini bisa digunakan untuk integrasi lebih lanjut dengan aplikasi atau server lainnya.\n\n" +
                       
                       "Semoga panduan ini membantu Anda dalam menggunakan aplikasi Voice Chat. Selamat mencoba!")
           .setPositiveButton("OK", null)
           .show();
}
}
