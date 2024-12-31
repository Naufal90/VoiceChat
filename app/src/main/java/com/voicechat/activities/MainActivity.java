package com.voicechat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String AUDIO_FILE_PATH = "/storage/emulated/0/VoiceChat/audio_file.3gp";
    private static final String TAG = "VoiceChatApp";
    private static final int REQUEST_CODE_PERMISSIONS = 1;

    private Button recordButton, stopRecordButton, playButton, stopPlayButton, sendButton, startHotspotButton, connectButton;
    private EditText commandEditText;
    private AdView mAdView;

    private MediaRecorder recorder;
    private MediaPlayer player;
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Memastikan semua izin telah diberikan
        requestPermissions();

        // Log untuk debugging
        Log.d(TAG, "Aplikasi dimulai");

        // Menulis log ke file
        writeLogToFile("Aplikasi dimulai");

       // Mendapatkan instance WifiManager
        WifiManager wifiManager = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
        
        if (wifiManager != null) {
            Log.d(TAG, "WifiManager berhasil diakses");
            writeLogToFile("WifiManager berhasil diakses");
        } else {
            Log.d(TAG, "WifiManager tidak tersedia");
            writeLogToFile("WifiManager tidak tersedia");
            
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

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS);
        }
    }

    private boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == REQUEST_CODE_PERMISSIONS) {
        boolean allPermissionsGranted = true;

        // Cek semua izin yang diminta
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (allPermissionsGranted) {
            // Jika semua izin diberikan, lanjutkan aksi yang memerlukan izin
            startPlaying(); // Misalnya, memulai pemutaran audio
        } else {
            // Jika izin ditolak, beri tahu pengguna dan mungkin arahkan ke pengaturan
            if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) ||
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Izin diperlukan untuk memutar audio", Toast.LENGTH_SHORT).show();
            } else {
                // Jika izin ditolak secara permanen, beri tahu pengguna untuk membuka pengaturan
                Toast.makeText(this, "Izin ditolak secara permanen. Buka pengaturan untuk memberikan izin.", Toast.LENGTH_LONG).show();
            }
        }
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
            player.setDataSource(AUDIO_FILE_PATH);
            player.prepare();
            player.start();
            Toast.makeText(this, "Memutar audio", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal memutar audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
 }

    private void stopPlaying() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
            showSuccessToast("Pemutaran audio dihentikan");
        }
    }

    private void sendCommand() {
        String command = commandEditText.getText().toString();
        showSuccessToast("Perintah dikirim: " + command);
    }

    private void startHotspot() {
    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    if (wifiManager == null) {
        Toast.makeText(this, "Wi-Fi tidak tersedia di perangkat ini", Toast.LENGTH_SHORT).show();
        return;
    }

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        // Android O ke atas (Menggunakan Soft AP Configuration)
        WifiManager.LocalOnlyHotspotReservation[] hotspotReservation = new WifiManager.LocalOnlyHotspotReservation[1];
        wifiManager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
            @Override
            public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                super.onStarted(reservation);
                hotspotReservation[0] = reservation;
                Toast.makeText(MainActivity.this, "Hotspot dimulai", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopped() {
                super.onStopped();
                Toast.makeText(MainActivity.this, "Hotspot dihentikan", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int reason) {
                super.onFailed(reason);
                Toast.makeText(MainActivity.this, "Gagal memulai hotspot, kode: " + reason, Toast.LENGTH_SHORT).show();
            }
        }, null);
    } else {
        Toast.makeText(this, "Versi Android tidak mendukung fitur ini", Toast.LENGTH_SHORT).show();
    }
}

    private void connectToHotspot(String ssid, String password) {
    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    if (wifiManager == null) {
        Toast.makeText(this, "Wi-Fi tidak tersedia di perangkat ini", Toast.LENGTH_SHORT).show();
        return;
    }

    WifiConfiguration wifiConfig = new WifiConfiguration();
    wifiConfig.SSID = "\"" + ssid + "\"";
    wifiConfig.preSharedKey = "\"" + password + "\"";

    int netId = wifiManager.addNetwork(wifiConfig);
    if (netId == -1) {
        Toast.makeText(this, "Gagal menambahkan jaringan hotspot", Toast.LENGTH_SHORT).show();
        return;
    }

    wifiManager.disconnect();
    wifiManager.enableNetwork(netId, true);
    wifiManager.reconnect();
    Toast.makeText(this, "Berhasil menghubungkan ke hotspot", Toast.LENGTH_SHORT).show();
}

    private void showSuccessToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
