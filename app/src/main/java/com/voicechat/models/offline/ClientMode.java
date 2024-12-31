package com.voicechat;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class ClientMode {
    private Context context;
    private WifiManager wifiManager;

    public ClientMode(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    // Fungsi untuk memulai koneksi ke hotspot
    public void connectToHotspot(String hotspotSSID) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Memberikan peringatan kepada pengguna untuk menghubungkan perangkat secara manual
            Toast.makeText(context,
                "Silakan hubungkan perangkat Anda secara manual ke hotspot \"" 
                + hotspotSSID + "\" melalui pengaturan Wi-Fi sebelum melanjutkan.",
                Toast.LENGTH_LONG).show();
        } else {
            // Logika otomatis untuk API 26-28
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            // Konfigurasi Wi-Fi untuk SSID tertentu
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = "\"" + hotspotSSID + "\""; // Tambahkan tanda petik agar sesuai format
            int netId = wifiManager.addNetwork(wifiConfig);

            if (netId == -1) {
                Toast.makeText(context, "Gagal menambahkan konfigurasi Wi-Fi.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Sambungkan ke jaringan
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();

            Toast.makeText(context, "Mencoba menghubungkan ke " + hotspotSSID, Toast.LENGTH_SHORT).show();
        }
    }

    // Fungsi untuk memeriksa apakah perangkat sudah terhubung ke hotspot
    public boolean isConnectedToHotspot(String hotspotSSID) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            String currentSSID = wifiInfo.getSSID();
            // Periksa apakah SSID yang terhubung sesuai dengan SSID hotspot
            return currentSSID != null && currentSSID.equals("\"" + hotspotSSID + "\"");
        }
        return false;
    }

    // Fungsi untuk memeriksa koneksi dan memberikan peringatan jika belum terhubung
    public void verifyConnection(String hotspotSSID) {
        if (!isConnectedToHotspot(hotspotSSID)) {
            Toast.makeText(context,
                "Perangkat Anda belum terhubung ke hotspot \"" + hotspotSSID + 
                "\". Silakan hubungkan terlebih dahulu melalui pengaturan Wi-Fi.",
                Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, 
                "Perangkat Anda sudah terhubung ke hotspot \"" + hotspotSSID + "\".", 
                Toast.LENGTH_SHORT).show();
        }
    }
}
