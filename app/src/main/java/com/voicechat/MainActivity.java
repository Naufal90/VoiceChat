package com.voicechat;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private RadioGroup modeSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        modeSelection = findViewById(R.id.modeSelection);

        modeSelection.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.offlineMode:
                    Toast.makeText(this, "Offline Mode Selected", Toast.LENGTH_SHORT).show();
                    VoiceModeHandler.setMode(VoiceModeHandler.Mode.OFFLINE);
                    break;
                case R.id.tunnelMode:
                    Toast.makeText(this, "Tunnel Mode Selected", Toast.LENGTH_SHORT).show();
                    VoiceModeHandler.setMode(VoiceModeHandler.Mode.TUNNEL);
                    break;
                case R.id.pluginMode:
                    Toast.makeText(this, "Plugin Mode Selected", Toast.LENGTH_SHORT).show();
                    VoiceModeHandler.setMode(VoiceModeHandler.Mode.PLUGIN);
                    break;
                case R.id.onlineMode:
                    Toast.makeText(this, "Online Mode Selected", Toast.LENGTH_SHORT).show();
                    VoiceModeHandler.setMode(VoiceModeHandler.Mode.ONLINE);
                    break;
                case R.id.vpnMode:
                    Toast.makeText(this, "VPN Mode Selected", Toast.LENGTH_SHORT).show();
                    VoiceModeHandler.setMode(VoiceModeHandler.Mode.VPN);
                    break;
            }
        });
    }
}
