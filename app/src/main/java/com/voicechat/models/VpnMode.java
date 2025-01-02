package com.voicechat.models;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class VpnMode {
    private Context context;

    public VpnMode(Context context) {
        this.context = context;
    }

    // Fungsi untuk memeriksa status koneksi VPN
    public boolean isVpnConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_VPN) {
                return true; // VPN terhubung
            }
        }
        return false; // VPN tidak terhubung
    }

    // Fungsi untuk menghubungkan dengan perangkat yang berada dalam VPN yang sama
    public void connectToVpnNetwork() {
        if (isVpnConnected()) {
            // Implementasikan logika untuk mencari perangkat yang terhubung dalam jaringan VPN yang sama
            // Misalnya, menggunakan multicast atau mDNS (Bonjour) untuk mendeteksi perangkat lain
            Toast.makeText(context, "Terhubung dengan perangkat dalam jaringan VPN yang sama", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "VPN tidak terhubung", Toast.LENGTH_SHORT).show();
        }
    }
}
