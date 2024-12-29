package com.voicechat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private OfflineMode offlineMode;
    private ClientMode clientMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        offlineMode = new OfflineMode(this);
        clientMode = new ClientMode(this);
pluginMode = new PluginMode(this);

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
}
