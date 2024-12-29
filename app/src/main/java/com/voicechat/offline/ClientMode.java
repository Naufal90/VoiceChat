package com.voicechat;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class ClientMode {
    private Context context;
    private WifiManager wifiManager;

    public ClientMode(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    // Fungsi untuk menghubungkan ke hotspot Player 1
    public void connectToHotspot(String hotspotSSID) {
        // Logika untuk menghubungkan ke Wi-Fi hotspot Player 1
        // Biasanya Anda akan menggunakan pengaturan Wi-Fi Android untuk memilih SSID yang benar.
        Toast.makeText(context, "Mencoba menghubungkan ke " + hotspotSSID, Toast.LENGTH_SHORT).show();
    }

    // Fungsi untuk memeriksa apakah terhubung dengan hotspot
    public boolean isConnectedToHotspot(String hotspotSSID) {
        // Pengecekan apakah sudah terhubung ke jaringan Wi-Fi yang benar
        return wifiManager.getConnectionInfo().getSSID().equals("\"" + hotspotSSID + "\"");
    }
}
