package com.voicechat.services;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class MyVpnService extends VpnService {

    private static final String TAG = "MyVpnService";
    private ParcelFileDescriptor vpnInterface;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Memulai VPN Service
        Builder builder = new Builder();
        
        // Mengonfigurasi session VPN
        builder.setSession("Minecraft Voice Chat VPN") // Nama sesi VPN
               .addAddress("10.0.0.2", 24)  // IP lokal untuk perangkat dalam jaringan VPN
               .addRoute("0.0.0.0", 0)  // Semua lalu lintas internet akan menggunakan VPN

        // Menambahkan DNS Server (Google DNS pada contoh ini)
        builder.addDnsServer("8.8.8.8");

        // Mengatur konfigurasi VPN lainnya, jika diperlukan
        builder.addRoute("10.0.0.0", 16);  // Menambahkan rute khusus jika diperlukan

        try {
            // Membuat koneksi VPN dan menyimpan interface yang digunakan
            vpnInterface = builder.establish();

            if (vpnInterface != null) {
                Log.d(TAG, "VPN started successfully.");
            } else {
                Log.e(TAG, "Failed to establish VPN interface.");
            }

        } catch (Exception e) {
            // Menangani error jika terjadi masalah saat membuat koneksi VPN
            Log.e(TAG, "VPN setup failed", e);
        }

        // Memastikan service tetap berjalan meskipun aplikasi di-background
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Menutup koneksi VPN ketika service dihentikan
        if (vpnInterface != null) {
            try {
                vpnInterface.close();
                Log.d(TAG, "VPN interface closed.");
            } catch (Exception e) {
                Log.e(TAG, "Failed to close VPN interface", e);
            }
        }

        // Log saat VPN dihentikan
        Log.d(TAG, "VPN stopped.");
    }

    @Override
    public void onRevoke() {
        super.onRevoke();
        // Menangani event ketika VPN di-revoke atau dihentikan oleh sistem
        Log.d(TAG, "VPN revoked.");
    }
}
