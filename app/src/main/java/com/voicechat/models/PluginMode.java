package com.voicechat.models;

import android.content.Context;
import android.widget.Toast;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PluginMode {
    private final Context context;
    private final ExecutorService executorService;

    public PluginMode(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    // Fungsi untuk mengirim data suara atau perintah ke server dengan plugin
    public void sendDataToPlugin(String serverUrl, String command) {
        if (serverUrl == null || serverUrl.isEmpty() || command == null || command.isEmpty()) {
            Toast.makeText(context, "URL server atau command tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            try {
                // Membuat koneksi HTTP ke server Minecraft
                URL url = new URL(serverUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                // Mengirim data ke server
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(command.getBytes());
                    os.flush();
                }

                // Menunggu respons dari server
                int responseCode = connection.getResponseCode();
                boolean success = responseCode == HttpURLConnection.HTTP_OK;

                // Menampilkan hasil di UI thread
                showToast(success ? "Data berhasil dikirim ke plugin!" : "Gagal mengirim data ke plugin!");
            } catch (Exception e) {
                e.printStackTrace();
                showToast("Terjadi kesalahan saat mengirim data ke plugin!");
            }
        });
    }

    // Fungsi untuk menampilkan Toast di UI thread
    private void showToast(String message) {
        ((android.app.Activity) context).runOnUiThread(() -> 
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        );
    }

    // Fungsi untuk mengecek mode plugin
    public void checkPluginMode(String selectedMode, String serverUrl, String command) {
        if (!"plugin".equals(selectedMode)) {
            return; // Jangan lakukan apapun jika mode bukan plugin
        }

        try {
            // Kirim data ke plugin jika mode adalah plugin
            sendDataToPlugin(serverUrl, command);
        } catch (Exception e) {
            showToast("Gagal mengirim data ke plugin!");
        }
    }
}
