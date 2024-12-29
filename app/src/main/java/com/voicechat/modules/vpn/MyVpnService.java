package com.voicechat;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class MyVpnService extends VpnService {

    private static final String TAG = "MyVpnService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Konfigurasi VPN dan jalankan koneksi VPN
        Builder builder = new Builder();
        builder.setSession("Minecraft Voice Chat VPN") // Nama sesi VPN
               .addAddress("10.0.0.2", 24) // IP lokal
               .addRoute("0.0.0.0", 0)  // Semua rute

        // Jika kamu ingin menggunakan DNS khusus, bisa ditambahkan seperti ini
        builder.addDnsServer("8.8.8.8");

        try {
            ParcelFileDescriptor vpnInterface = builder.establish();
            // VPN terhubung, kamu bisa melakukan operasi lebih lanjut di sini
            Log.d(TAG, "VPN started");
        } catch (Exception e) {
            Log.e(TAG, "VPN setup failed", e);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Hentikan VPN jika perlu
        Log.d(TAG, "VPN stopped");
    }
}
