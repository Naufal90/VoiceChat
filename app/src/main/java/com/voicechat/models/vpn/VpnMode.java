package com.voicechat;

import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class VpnMode {

    private static final String TAG = "VpnMode";
    private Context context;

    public VpnMode(Context context) {
        this.context = context;
    }

    // Fungsi untuk memulai VPN
    public void startVpn() {
        Intent vpnIntent = new Intent(context, MyVpnService.class);
        context.startService(vpnIntent);
        Log.d(TAG, "VPN service started");
    }

    // Fungsi untuk menghentikan VPN
    public void stopVpn() {
        Intent vpnIntent = new Intent(context, MyVpnService.class);
        context.stopService(vpnIntent);
        Log.d(TAG, "VPN service stopped");
    }

    // Implementasi lainnya jika diperlukan
}
