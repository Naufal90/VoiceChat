package com.voicechat.models;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class OfflineMode {
    private ServerSocket serverSocket;
    private Context context;
    private Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Activity activity;

public OfflineMode(Activity activity) {
    this.activity = activity;
}

    public OfflineMode(Context context) {
        this.context = context;
    }

    // Fungsi untuk menampilkan toast di UI thread
    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Fungsi untuk memulai server dan mendengarkan koneksi client
    public void startServer() {
    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                // Membuka ServerSocket pada port yang ditentukan (misalnya 12345)
                serverSocket = new ServerSocket(12345);
                System.out.println("Menunggu koneksi dari client...");

                // Menunggu koneksi dari client
                clientSocket = serverSocket.accept();
                System.out.println("Client terhubung.");

                // Update UI di thread utama setelah koneksi diterima
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Menampilkan pesan toast di UI thread
                        Toast.makeText(context, "Client terhubung!", Toast.LENGTH_SHORT).show();
                    }
                });

                // Mempersiapkan InputStream dan OutputStream untuk komunikasi
                inputStream = clientSocket.getInputStream();
                outputStream = clientSocket.getOutputStream();

                // Komunikasi antara host dan client bisa dimulai di sini
                receiveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }).start();
}

    // Fungsi untuk menerima data audio dari client
    public void receiveData() {
        byte[] buffer = new byte[1024];
        try {
            File receivedFile = new File(context.getExternalFilesDir(null), "received_audio.3gp");
            FileOutputStream fileOutputStream = new FileOutputStream(receivedFile);
            
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            fileOutputStream.close();

            // Setelah menerima data, kita bisa memutar audio menggunakan MediaPlayer
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(receivedFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fungsi untuk mengirim data ke client
    public void sendData(String message) {
        try {
            if (outputStream != null) {
                outputStream.write(message.getBytes());
                System.out.println("Pesan terkirim ke client.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fungsi untuk menghentikan server
    public void stopServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
