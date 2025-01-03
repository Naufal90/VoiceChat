package com.voicechat.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class OfflineMode {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    // Fungsi untuk memulai server dan mendengarkan koneksi client
    public void startServer() {
        try {
            // Membuka ServerSocket pada port yang ditentukan (misalnya 12345)
            serverSocket = new ServerSocket(12345);
            System.out.println("Menunggu koneksi dari client...");

            // Menunggu koneksi dari client
            clientSocket = serverSocket.accept();
            System.out.println("Client terhubung.");

            // Mempersiapkan InputStream dan OutputStream untuk komunikasi
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();

            // Komunikasi antara host dan client bisa dimulai di sini
            // Misalnya, menerima data dari client
            receiveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fungsi untuk menerima data dari client
    public void receiveData() {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                String receivedMessage = new String(buffer, 0, bytesRead);
                System.out.println("Pesan dari client: " + receivedMessage);
            }
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
