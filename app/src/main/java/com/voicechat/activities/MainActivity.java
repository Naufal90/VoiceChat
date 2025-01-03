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
import android.os.Build;
import android.provider.Settings;
import android.content.Intent;
import android.os.Environment;

import com.voicechat.R;
import com.voicechat.models.OfflineMode;
import com.voicechat.models.ClientMode;
import com.voicechat.models.PluginMode;
import com.voicechat.models.VpnMode;
import com.voicechat.log.LogWriter;
import com.voicechat.managers.MyAudioManager;
import com.voicechat.managers.ProximitySensor;
import com.voicechat.managers.VoiceModeHandler;
import com.voicechat.services.MyVpnService;
import com.voicechat.utils.NetworkUtils;
import com.voicechat.utils.PermissionUtils;
import com.voicechat.utils.ToastUtils;
import com.voicechat.sound.AudioPlayer;
import com.voicechat.sound.AudioRecorder;
import com.voicechat.sound.SoundManager;

import android.net.wifi.WifiManager;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String AUDIO_FILE_PATH = new File(getExternalFilesDir(null), "VoiceChat/audio_file.3gp").getAbsolutePath();
    private static final String TAG = "VoiceChat";

    private OfflineMode offlineMode;
    private ClientMode clientMode;
    private PluginMode pluginMode;
    private VpnMode vpnMode;

    private Button startStopButton, recordButton, stopRecordButton, playButton, stopPlayButton, sendButton, startHotspotButton, connectButton;
    private EditText etServerUrl, etCommand;
    private EditText commandEditText;
    private AdView mAdView;
    private boolean isRecording = false;

    private AudioRecorder audioRecorder;
    private AudioPlayer audioPlayer;
    private SoundManager soundManager;

    private LogWriter logWriter;

    private void requestStoragePermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // Cek apakah aplikasi sudah memiliki akses penuh ke penyimpanan
        if (!Environment.isExternalStorageManager()) {
            // Minta izin akses penuh ke penyimpanan
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            startActivityForResult(intent, 1); // Ganti dengan REQUEST_CODE yang sesuai
        }
    } else {
        // Untuk Android 10 dan lebih rendah, gunakan izin lama
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
}

   @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    requestStoragePermission();

    // Memeriksa apakah izin yang diperlukan sudah diberikan
    if (!PermissionUtils.isPermissionGranted(this)) {
        PermissionUtils.requestPermissions(this);
    }

    // Inisialisasi mode
    offlineMode = new OfflineMode(MainActivity.this);
    clientMode = new ClientMode();
    pluginMode = new PluginMode(this);
    vpnMode = new VpnMode(this);

    // Mengambil input dari pengguna untuk Plugin Mode
    EditText serverUrlEditText = findViewById(R.id.serverUrl);  // Ambil URL server
    EditText serverPortEditText = findViewById(R.id.serverPort);  // Ambil port server

    // Cek apakah mode yang dipilih adalah Plugin Mode
    String selectedMode = "plugin"; // Atau ambil dari pilihan mode pengguna (misalnya dari spinner)

    // Jika mode plugin, ambil input dari pengguna
    if (selectedMode.equals("plugin")) {
        String serverUrl = serverUrlEditText.getText().toString();
        int serverPort = Integer.parseInt(serverPortEditText.getText().toString());

        // Menginisialisasi AudioRecorder dengan server yang dimasukkan pengguna
        InetAddress serverAddress = InetAddress.getByName(serverUrl);
        audioRecorder = new AudioRecorder(serverAddress, serverPort);

        // Mengirim data ke plugin
        String command = "{\"action\":\"sendMessage\",\"message\":\"Hello Plugin!\"}";
        pluginMode.sendDataToPlugin(serverUrl, command);
    } 
    // Jika mode offline atau vpn, gunakan deteksi otomatis
    else {
        if (selectedMode.equals("offline")) {
            // Deteksi otomatis server untuk offline mode
            audioRecorder = new AudioRecorder(InetAddress.getLoopbackAddress(), 12345);  // Contoh alamat localhost dan port default
            offlineMode.startServer();
        } else if (selectedMode.equals("vpn")) {
            // Deteksi otomatis server untuk VPN mode
            audioRecorder = new AudioRecorder(InetAddress.getByName("vpn.server.com"), 12345);  // Ganti dengan server VPN yang sesuai
            vpnMode.startVPNServer();
        }
    }

    // Menulis log aplikasi dimulai
    logWriter = new LogWriter(this);
    logWriter.writeLog("Aplikasi Dimulai");

    // Memulai server offlineMode atau menghubungkan clientMode
    offlineMode.startServer();
    clientMode.connectToServer("alamat_server");

    // Inisialisasi AdMob
    MobileAds.initialize(this, initializationStatus -> {});
    mAdView = findViewById(R.id.adView);
    AdRequest adRequest = new AdRequest.Builder().build();
    mAdView.loadAd(adRequest);
    initializeAdMob();

    // Inisialisasi UI
    initUI();

    // Inisialisasi audio
    audioPlayer = new AudioPlayer(audioFile.getAbsolutePath());
    soundManager = new SoundManager(this, R.raw.sample_audio); // Pastikan file ada di res/raw

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

        recordButton.setOnClickListener(v -> audioRecorder.startRecording());
        stopRecordButton.setOnClickListener(v -> audioRecorder.stopRecording());
        playButton.setOnClickListener(v -> audioPlayer.play(AUDIO_FILE_PATH));
        stopPlayButton.setOnClickListener(v -> audioPlayer.stop());
        sendButton.setOnClickListener(v -> sendCommand());
    }

    @Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
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
    logWriter.writeLog("Aplikasi Dihentikan");

    // Pastikan untuk melepaskan resource AudioRecorder dan AudioPlayer
    if (audioRecorder != null) {
        audioRecorder.release();
        audioRecorder = null;
    }

    if (audioPlayer != null) {
        audioPlayer.release();
        audioPlayer = null;
    }

    // Jika SoundManager juga digunakan
    if (soundManager != null) {
        soundManager.release();
        soundManager = null;
    }
}

    private void startRecording() {
    try {
        // Menentukan folder penyimpanan
        File folder = new File(getExternalFilesDir(null), "VoiceChat");

        // Pastikan folder ada atau coba buat jika belum ada
        if (!folder.exists() && !folder.mkdirs()) {
            showErrorToast("Gagal membuat folder untuk menyimpan audio");
            return;
        }

        // Menentukan path file audio
        String audioFilePath = new File(folder, "recording.3gp").getAbsolutePath();

        // Memastikan audioRecorder tidak sedang dalam kondisi perekaman
        if (audioRecorder != null && isRecording) {
            showErrorToast("Perekaman sudah dimulai");
            return;
        }

        // Membuat instance baru AudioRecorder
        audioRecorder = new AudioRecorder(audioFilePath);

        // Mulai merekam
        audioRecorder.startRecording();
        isRecording = true; // Menandakan bahwa perekaman sedang berlangsung

        // Menampilkan toast untuk memberi tahu pengguna bahwa perekaman telah dimulai
        showSuccessToast("Perekaman dimulai");

    } catch (IOException e) {
        e.printStackTrace();
        showErrorToast("Gagal memulai perekaman");
    } catch (IllegalStateException e) {
        e.printStackTrace();
        showErrorToast("Perekaman dalam kondisi tidak valid");
    }
}
        
    private void startPlaying() {
    try {
        // Menentukan file audio
        File audioFile = new File(getExternalFilesDir(null), "VoiceChat/recording.3gp");
        
        // Memeriksa apakah file audio ada
        if (!audioFile.exists()) {
            Toast.makeText(this, "File audio tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        // Jika ada audio player yang aktif, hentikan dan lepaskan
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer.release();
        }

        // Inisialisasi AudioPlayer dan mulai pemutaran
        audioPlayer = new AudioPlayer(audioFile.getAbsolutePath());
        
        // Menangani pemutaran audio
        try {
            audioPlayer.startPlaying();
            showSuccessToast("Pemutaran dimulai");
        } catch (IOException e) {
            e.printStackTrace();
            showErrorToast("Gagal memutar audio");
        }

    } catch (Exception e) {
        e.printStackTrace();
        showErrorToast("Terjadi kesalahan saat memulai pemutaran");
    }
}

private void stopPlaying() {
    if (audioPlayer != null) {
        audioPlayer.stopPlaying();
        audioPlayer.release();
        audioPlayer = null;
        showSuccessToast("Pemutaran dihentikan");
    }
}    

private void stopRecording() {
    if (audioRecorder != null) {
        try {
            audioRecorder.stopRecording(); // Berhenti merekam
            showSuccessToast("Perekaman dihentikan");
        } catch (IllegalStateException e) {
            e.printStackTrace();
            showErrorToast("Perekaman tidak dapat dihentikan karena status tidak valid");
        }
        audioRecorder.release(); // Melepaskan resource
        audioRecorder = null;    // Null-kan recorder setelah selesai
    }
}

    private void sendCommand() {
    String serverUrl = etServerUrl.getText().toString().trim();
    String command = etCommand.getText().toString().trim();

    if (serverUrl.isEmpty() || command.isEmpty()) {
        showErrorToast("URL server dan perintah tidak boleh kosong");
        return;
    }

    pluginMode.sendDataToPlugin(serverUrl, command);
    showSuccessToast("Perintah dikirim");
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

// Metode untuk menginisialisasi AdMob
    public void initializeAdMob() {
        MobileAds.initialize(this, initializationStatus -> {
            if (initializationStatus.getInitializationState() == InitializationStatus.InitializationState.FAILURE) {
                // Penanganan jika inisialisasi AdMob gagal
                showErrorToast("Gagal menginisialisasi AdMob");
            } else {
                // Penanganan jika inisialisasi berhasil
                showSuccessToast("AdMob berhasil diinisialisasi");
            }
        });
    }
}
