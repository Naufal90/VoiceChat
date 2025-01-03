package com.voicechat.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyVpnService extends Service {

    private static final String TAG = "MyVpnService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "MyVpnService started");
        // Jika tidak digunakan untuk membangun koneksi VPN, cukup log status di sini
        return START_NOT_STICKY; // Tidak perlu restart service setelah dihentikan
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "MyVpnService stopped");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Tidak ada binding untuk service ini
    }
}
