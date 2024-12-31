package com.voicechat;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
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
    public void connectToHotspot(String hotspotSSID, String hotspotPassword) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            Toast.makeText(context, "Android Q ke atas tidak mendukung API langsung untuk menghubungkan ke Wi-Fi.", Toast.LENGTH_LONG).show();
            return;
        }

        // Membuat konfigurasi Wi-Fi
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + hotspotSSID + "\""; // Tambahkan tanda kutip untuk SSID
        wifiConfig.preSharedKey = "\"" + hotspotPassword + "\""; // Tambahkan tanda kutip untuk password

        // Tambahkan konfigurasi Wi-Fi ke WifiManager
        int netId = wifiManager.addNetwork(wifiConfig);
        if (netId == -1) {
            Toast.makeText(context, "Gagal menambahkan konfigurasi Wi-Fi.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hubungkan ke jaringan Wi-Fi
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        Toast.makeText(context, "Mencoba menghubungkan ke " + hotspotSSID, Toast.LENGTH_SHORT).show();
    }

    // Fungsi untuk memeriksa apakah terhubung dengan hotspot
    public boolean isConnectedToHotspot(String hotspotSSID) {
        String currentSSID = wifiManager.getConnectionInfo().getSSID();
        return currentSSID != null && currentSSID.equals("\"" + hotspotSSID + "\"");
    }
}
