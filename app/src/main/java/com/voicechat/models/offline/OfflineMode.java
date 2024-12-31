package com.voicechat;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class OfflineMode {
    private Context context;
    private WifiManager wifiManager;
    private WifiManager.LocalOnlyHotspotReservation hotspotReservation;

    public OfflineMode(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    // Fungsi untuk memulai hotspot
    public void startHotspot() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false); // Matikan Wi-Fi jika menyala
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            wifiManager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
                @Override
                public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                    super.onStarted(reservation);
                    hotspotReservation = reservation;
                    Toast.makeText(context, "Hotspot dimulai!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStopped() {
                    super.onStopped();
                    Toast.makeText(context, "Hotspot dihentikan!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(int reason) {
                    super.onFailed(reason);
                    Toast.makeText(context, "Gagal memulai hotspot!", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } else {
            Toast.makeText(context, "Fitur ini hanya tersedia di Android Oreo atau lebih baru!", Toast.LENGTH_SHORT).show();
        }
    }

    // Fungsi untuk memeriksa apakah perangkat terhubung ke hotspot
    public boolean isConnectedToHotspot() {
        String ssid = wifiManager.getConnectionInfo().getSSID();
        return ssid != null && ssid.contains("Hotspot");
    }

    // Fungsi untuk menghentikan hotspot
    public void stopHotspot() {
        if (hotspotReservation != null) {
            hotspotReservation.close();
            hotspotReservation = null;
            Toast.makeText(context, "Hotspot dihentikan!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Tidak ada hotspot yang aktif!", Toast.LENGTH_SHORT).show();
        }
    }
}
