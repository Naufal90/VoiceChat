package com.voicechat.models;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

public class VpnMode {
    private Context context;
    private ConnectionsClient connectionsClient;

    public VpnMode(Context context) {
        this.context = context;
        this.connectionsClient = new ConnectionsClient(context);
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
            // Mulai pencarian perangkat dalam jaringan VPN yang sama menggunakan mDNS
            startDeviceDiscovery();
        } else {
            Toast.makeText(context, "VPN tidak terhubung", Toast.LENGTH_SHORT).show();
        }
    }

    // Fungsi untuk memulai pencarian perangkat di jaringan VPN menggunakan mDNS
    private void startDeviceDiscovery() {
    // Menggunakan Nearby Connections API untuk menemukan perangkat dalam jaringan yang sama
    String serviceId = "com.voicechat.service"; // ID layanan mDNS

    // Menambahkan konfigurasi Strategy
    Strategy strategy = new Strategy(Strategy.TETHERING, Strategy.P2P_CLUSTER);

    connectionsClient.startDiscovery(
            serviceId,
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    // Ketika perangkat ditemukan, lakukan koneksi
                    Toast.makeText(context, "Perangkat ditemukan: " + deviceName, Toast.LENGTH_SHORT).show();
                    // Lakukan koneksi lebih lanjut jika diperlukan
                    connectionsClient.requestConnection("MyDevice", endpointId, connectionLifecycleCallback);
                }

                @Override
                public void onEndpointLost(String endpointId) {
                    // Ketika perangkat hilang dari jaringan
                    Toast.makeText(context, "Perangkat hilang", Toast.LENGTH_SHORT).show();
                }
            },
            strategy // Menambahkan strategy yang telah diatur
    );
}

    // Callback untuk mengelola siklus koneksi perangkat yang ditemukan
    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
            // Ketika koneksi dimulai
            connectionsClient.acceptConnection(endpointId, new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    // Proses data yang diterima dari perangkat lain (jika diperlukan)
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                    // Update status transfer data
                }
            });
        }

        @Override
        public void onConnectionResult(String endpointId, ConnectionResolution result) {
            if (result.getStatus().isSuccess()) {
                Toast.makeText(context, "Terhubung dengan perangkat", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Koneksi gagal", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onDisconnected(String endpointId) {
            // Ketika perangkat terputus
            Toast.makeText(context, "Perangkat terputus", Toast.LENGTH_SHORT).show();
        }
    };

    // Fungsi untuk memeriksa koneksi VPN
    public void checkVpnConnection() {
        if (isVpnConnected()) {
            Toast.makeText(context, "VPN terhubung", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "VPN tidak terhubung", Toast.LENGTH_SHORT).show();
        }
    }

    // Menutup class VpnMode dengan tanda kurung penutup
}
