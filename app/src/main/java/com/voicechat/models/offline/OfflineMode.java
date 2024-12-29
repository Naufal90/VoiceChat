
package com.voicechat;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class OfflineMode {
    private Context context;
    private WifiManager wifiManager;

    public OfflineMode(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    // Fungsi untuk memulai hotspot
    public void startHotspot() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false); // Matikan Wi-Fi
        }

        // Aktifkan hotspot
        // Di sini Anda bisa implementasikan logika untuk mengaktifkan hotspot,
        // tapi Android tidak menyediakan API langsung untuk ini.
        // Anda perlu menggunakan refleksi atau akses root untuk mengaktifkan hotspot.
        Toast.makeText(context, "Hotspot dimulai!", Toast.LENGTH_SHORT).show();
    }

    // Fungsi untuk memeriksa apakah perangkat terhubung ke hotspot
    public boolean isConnectedToHotspot() {
        // Pengecekan apakah perangkat terhubung ke jaringan Wi-Fi hotspot
        return wifiManager.getConnectionInfo().getSSID().contains("Hotspot");
    }
}
