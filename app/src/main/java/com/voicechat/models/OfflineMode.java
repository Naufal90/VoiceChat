package com.voicechat.models;

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
            wifiManager.setWifiEnabled(false); // Matikan Wi-Fi terlebih dahulu
        }

        // Menampilkan peringatan bahwa hotspot sedang dimulai
        Toast.makeText(context, "Hotspot sedang dimulai...", Toast.LENGTH_SHORT).show();

        // Di Android API 26+ (Oreo ke atas), gunakan LocalOnlyHotspot
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            wifiManager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
                @Override
                public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                    super.onStarted(reservation);
                    Toast.makeText(context, "Hotspot berhasil dimulai!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(int reason) {
                    super.onFailed(reason);
                    Toast.makeText(context, "Gagal memulai hotspot!", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } else {
            Toast.makeText(context, "API tidak mendukung hotspot otomatis!", Toast.LENGTH_SHORT).show();
        }
    }

    // Fungsi untuk menghentikan hotspot
    public void stopHotspot() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Hotspot tidak dapat dihentikan langsung melalui API, bisa menggunakan metode manual
            Toast.makeText(context, "Hotspot perlu dihentikan secara manual.", Toast.LENGTH_SHORT).show();
        } else {
            // Untuk API di bawah 26, bisa mengandalkan metode root atau cara manual
            Toast.makeText(context, "API tidak mendukung penghentian hotspot secara otomatis.", Toast.LENGTH_SHORT).show();
        }
    }

    // Fungsi untuk memeriksa status hotspot
    public void verifyHotspotStatus() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // API 26 ke atas tidak memiliki akses langsung ke status hotspot
            Toast.makeText(context, "Periksa apakah perangkat lain dapat melihat hotspot!", Toast.LENGTH_SHORT).show();
        } else {
            // API di bawah 26 (gunakan metode manual atau akses root jika diperlukan)
            Toast.makeText(context, "Pastikan hotspot aktif secara manual!", Toast.LENGTH_SHORT).show();
        }
    }
}
