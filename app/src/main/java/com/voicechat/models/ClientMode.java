package com.voicechat.models;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

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
            // Jika perangkat menggunakan Android Q atau lebih tinggi, arahkan pengguna untuk menghubungkan secara manual
            Toast.makeText(context,
                "Silakan hubungkan perangkat Anda secara manual ke hotspot \"" 
                + hotspotSSID + "\" melalui pengaturan Wi-Fi sebelum melanjutkan.",
                Toast.LENGTH_LONG).show();
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
            // Jika perangkat belum terhubung, munculkan dialog untuk mengarahkan pengguna
            new AlertDialog.Builder(context)
                .setTitle("Koneksi Hotspot")
                .setMessage("Perangkat Anda belum terhubung ke hotspot \"" + hotspotSSID + 
                    "\". Silakan hubungkan terlebih dahulu melalui pengaturan Wi-Fi.")
                .setCancelable(false)
                .setPositiveButton("Buka Pengaturan", (dialog, id) -> {
                    // Arahkan pengguna ke pengaturan Wi-Fi
                    context.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                })
                .setNegativeButton("Batal", (dialog, id) -> dialog.dismiss())
                .show();
        } else {
            Toast.makeText(context, 
                "Perangkat Anda sudah terhubung ke hotspot \"" + hotspotSSID + "\".", 
                Toast.LENGTH_SHORT).show();
        }
    }
}
