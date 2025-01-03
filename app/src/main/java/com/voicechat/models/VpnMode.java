package com.voicechat.models;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

public class VpnMode {
    private Context context;
    private ConnectionsClient connectionsClient;

    public VpnMode(Context context) {
        this.context = context; // Pastikan context diinisialisasi dengan benar
        this.connectionsClient = Nearby.getConnectionsClient(context);
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
            // Mulai pencarian perangkat dalam jaringan VPN yang sama menggunakan Nearby API
            startDeviceDiscovery();
        } else {
            Toast.makeText(context, "VPN tidak terhubung", Toast.LENGTH_SHORT).show();
        }
    }

    // Fungsi untuk memulai pencarian perangkat di jaringan VPN menggunakan Nearby Connections API
    private void startDeviceDiscovery() {
        String serviceId = "com.voicechat.service";

        connectionsClient.startDiscovery(
                serviceId,
                new EndpointDiscoveryCallback() {
                    @Override
                    public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                        // Mendapatkan nama perangkat dari DiscoveredEndpointInfo
                        String endpointName = info.getEndpointName();
                        Toast.makeText(context, "Perangkat ditemukan: " + endpointName, Toast.LENGTH_SHORT).show();

                        // Meminta koneksi ke perangkat yang ditemukan
                        connectionsClient.requestConnection("MyDevice", endpointId, connectionLifecycleCallback);
                    }

                    @Override
                    public void onEndpointLost(String endpointId) {
                        // Ketika perangkat hilang dari jaringan
                        Toast.makeText(context, "Perangkat hilang", Toast.LENGTH_SHORT).show();
                    }
                },
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        );
    }

    // Callback untuk mengelola siklus koneksi perangkat yang ditemukan
    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
            // Menanggapi permintaan koneksi dan menerima koneksi
            connectionsClient.acceptConnection(endpointId, new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    // Proses data yang diterima dari perangkat lain
                    // Misalnya, menerima data suara atau informasi lainnya
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
                // Koneksi berhasil
                Toast.makeText(context, "Terhubung dengan perangkat", Toast.LENGTH_SHORT).show();
            } else {
                // Koneksi gagal
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

    // Fungsi untuk mode VPN
    public void checkVPNMode(String selectedMode) {
        if (!"vpn".equals(selectedMode)) {
            return; // Jangan lakukan apapun jika mode bukan VPN
        }

        // Cek koneksi VPN
        if (!isVpnConnected()) {
            Toast.makeText(context, "VPN tidak terhubung", Toast.LENGTH_SHORT).show();
        } else {
            connectToVpnNetwork();
        }
    }
}
