package com.voicechat.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientMode {
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    // Fungsi untuk menghubungkan ke host (server)
    public void connectToServer(String host) {
        try {
            socket = new Socket(host, 12345); // Menghubungkan ke host di port 12345
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            System.out.println("Terhubung ke server.");

            // Komunikasi antara client dan host bisa dimulai di sini
            // Misalnya, mengirim data ke server atau menerima data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fungsi untuk menghentikan koneksi
    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fungsi untuk mengirim data ke server
    public void sendData(String message) {
        try {
            if (outputStream != null) {
                outputStream.write(message.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fungsi untuk menerima data dari server
    public String receiveData() {
        StringBuilder data = new StringBuilder();
        try {
            if (inputStream != null) {
                int character;
                while ((character = inputStream.read()) != -1) {
                    data.append((char) character);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data.toString();
    }
}
