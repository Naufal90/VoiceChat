package com.voicechat.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

public class VpnStatusChecker {

    private Context context;

    public VpnStatusChecker(Context context) {
        this.context = context;
    }

    public void checkVpnStatus() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = cm.getAllNetworks(); // Mendapatkan semua jaringan yang aktif
        for (Network network : networks) {
            NetworkInfo networkInfo = cm.getNetworkInfo(network);
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_VPN) {
                Log.d("VpnStatus", "VPN is connected");
                return; // VPN aktif
            }
        }
        Log.d("VpnStatus", "No active VPN connection");
    }
}
