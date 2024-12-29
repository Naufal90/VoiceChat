package com.voicechat;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PluginMode {
    private Context context;

    public PluginMode(Context context) {
        this.context = context;
    }

    // Fungsi untuk mengirim data suara atau perintah ke server dengan plugin
    public void sendDataToPlugin(String serverUrl, String command) {
        // Menggunakan AsyncTask untuk melakukan operasi jaringan di background thread
        new SendDataTask().execute(serverUrl, command);
    }

    // AsyncTask untuk mengirim data ke server Minecraft
    private class SendDataTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String serverUrl = params[0];
            String command = params[1];

            try {
                // Membuat koneksi HTTP ke server Minecraft
                URL url = new URL(serverUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.getOutputStream().write(command.getBytes());

                // Menunggu respons dari server
                int responseCode = connection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            // Menampilkan pesan hasil
            if (success) {
                Toast.makeText(context, "Data berhasil dikirim ke plugin!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Gagal mengirim data ke plugin!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
