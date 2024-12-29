package com.voicechat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import androidx.appcompat.app.AppCompatActivity;

private static final String AUDIO_FILE_PATH = "/path/to/audio/file.3gp"; // Tentukan path file audio
    private Button recordButton;
    private Button stopRecordButton;
    private Button playButton;
    private Button stopPlayButton;

public class MainActivity extends AppCompatActivity {
    private OfflineMode offlineMode;
    private ClientMode clientMode;
    private OnlineMode onlineMode;
    private AdView mAdView;
    private OnlineMode onlineMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordButton = findViewById(R.id.recordButton);
        stopRecordButton = findViewById(R.id.stopRecordButton);
        playButton = findViewById(R.id.playButton);
        stopPlayButton = findViewById(R.id.stopPlayButton);

        // Inisialisasi AdMob
        MobileAds.initialize(this, initializationStatus -> {});

        offlineMode = new OfflineMode(this);
        clientMode = new ClientMode(this);
        pluginMode = new PluginMode(this);
        onlineMode = new OnlineMode(this);

        Button startHotspotButton = findViewById(R.id.startHotspotButton);
        Button connectButton = findViewById(R.id.connectButton);

        startHotspotButton.setOnClickListener(v -> {
            offlineMode.startHotspot(); // Player 1 mulai hotspot
        });

        connectButton.setOnClickListener(v -> {
            String hotspotSSID = "Player1_Hotspot";  // SSID hotspot Player 1
            if (clientMode.isConnectedToHotspot(hotspotSSID)) {
                Toast.makeText(MainActivity.this, "Terhubung ke hotspot!", Toast.LENGTH_SHORT).show();
            } else {
                clientMode.connectToHotspot(hotspotSSID); // Player 2 coba connect
            }
        });
    }
Button sendButton = findViewById(R.id.sendButton);
        EditText commandEditText = findViewById(R.id.commandEditText);

        sendButton.setOnClickListener(v -> {
            String command = commandEditText.getText().toString();
            if (!command.isEmpty()) {
                String serverUrl = "http://your-server-url.com/plugin-endpoint"; // Ganti dengan URL server Anda
                pluginMode.sendDataToPlugin(serverUrl, command);
            } else {
                Toast.makeText(MainActivity.this, "Perintah tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        // Ketika tombol login diklik
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (!username.isEmpty() && !password.isEmpty()) {
                // Memanggil fungsi untuk autentikasi
                onlineMode.authenticateAndPlay(username, password);
            } else {
                Toast.makeText(MainActivity.this, "Username dan password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
// Menampilkan banner iklan
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
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
    }
 EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewpackage com.voicechat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import androidx.appcompat.app.AppCompatActivity;

private static final String AUDIO_FILE_PATH = "/path/to/audio/file.3gp"; // Tentukan path file audio
    private Button recordButton;
    private Button stopRecordButton;
    private Button playButton;
    private Button stopPlayButton;

public class MainActivity extends AppCompatActivity {
    private OfflineMode offlineMode;
    private ClientMode clientMode;
    private OnlineMode onlineMode;
    private AdView mAdView;
    private OnlineMode onlineMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordButton = findViewById(R.id.recordButton);
        stopRecordButton = findViewById(R.id.stopRecordButton);
        playButton = findViewById(R.id.playButton);
        stopPlayButton = findViewById(R.id.stopPlayButton);

        // Inisialisasi AdMob
        MobileAds.initialize(this, initializationStatus -> {});

        offlineMode = new OfflineMode(this);
        clientMode = new ClientMode(this);
        pluginMode = new PluginMode(this);
        onlineMode = new OnlineMode(this);

        Button startHotspotButton = findViewById(R.id.startHotspotButton);
        Button connectButton = findViewById(R.id.connectButton);

        startHotspotButton.setOnClickListener(v -> {
            offlineMode.startHotspot(); // Player 1 mulai hotspot
        });

        connectButton.setOnClickListener(v -> {
            String hotspotSSID = "Player1_Hotspot";  // SSID hotspot Player 1
            if (clientMode.isConnectedToHotspot(hotspotSSID)) {
                Toast.makeText(MainActivity.this, "Terhubung ke hotspot!", Toast.LENGTH_SHORT).show();
            } else {
                clientMode.connectToHotspot(hotspotSSID); // Player 2 coba connect
            }
        });
    }
Button sendButton = findViewById(R.id.sendButton);
        EditText commandEditText = findViewById(R.id.commandEditText);

        sendButton.setOnClickListener(v -> {
            String command = commandEditText.getText().toString();
            if (!command.isEmpty()) {
                String serverUrl = "http://your-server-url.com/plugin-endpoint"; // Ganti dengan URL server Anda
                pluginMode.sendDataToPlugin(serverUrl, command);
            } else {
                Toast.makeText(MainActivity.this, "Perintah tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        // Ketika tombol login diklik
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (!username.isEmpty() && !password.isEmpty()) {
                // Memanggil fungsi untuk autentikasi
                onlineMode.authenticateAndPlay(username, password);
            } else {
                Toast.makeText(MainActivity.this, "Username dan password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
// Menampilkan banner iklan
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
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
    }
 EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        // Meminta izin saat aplikasi pertama kali dijalankan
        PermissionUtils.requestPermissions(this);

        // Mengecek apakah perangkat terhubung ke internet
        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtils.showToast(this, "Tidak ada koneksi internet!");
        }

        // Ketika tombol login diklik
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (!username.isEmpty() && !password.isEmpty()) {
                // Memanggil fungsi untuk autentikasi
                onlineMode.authenticateAndPlay(username, password);
            } else {
                ToastUtils.showToast(this, "Username dan password tidak boleh kosong!");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.handlePermissionResult(requestCode, permissions, grantResults, this);
    }
}
ById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);

        // Meminta izin saat aplikasi pertama kali dijalankan
        PermissionUtils.requestPermissions(this);

        // Mengecek apakah perangkat terhubung ke internet
        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtils.showToast(this, "Tidak ada koneksi internet!");
        }

        // Ketika tombol login diklik
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (!username.isEmpty() && !password.isEmpty()) {
                // Memanggil fungsi untuk autentikasi
                onlineMode.authenticateAndPlay(username, password);
            } else {
                ToastUtils.showToast(this, "Username dan password tidak boleh kosong!");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.handlePermissionResult(requestCode, permissions, grantResults, this);
    }
}
