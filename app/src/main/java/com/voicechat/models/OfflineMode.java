package com.voicechat.models;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class OfflineMode {
    private ServerSocket serverSocket;
    private Socket clientSocket;

    // Fungsi untuk memulai server dan mendengarkan koneksi client
    public void startServer() {
        try {
            // Membuka ServerSocket pada port yang ditentukan (misalnya 12345)
            serverSocket = new ServerSocket(12345);
            System.out.println("Menunggu koneksi dari client...");

            // Menunggu koneksi dari client
            clientSocket = serverSocket.accept();
            System.out.println("Client terhubung.");

            // Komunikasi antara host dan client bisa dimulai di sini
            // Misalnya, Anda bisa menggunakan InputStream/OutputStream untuk mengirim data
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
