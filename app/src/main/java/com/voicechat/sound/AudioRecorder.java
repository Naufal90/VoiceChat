package com.voicechat.sound;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class AudioRecorder {
    private AudioRecord audioRecord;
    private int bufferSize;
    private byte[] audioBuffer;

    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private boolean isRecording;

    // Untuk mode Plugin (memasukkan alamat server dan port dari pengguna)
public AudioRecorder(InetAddress serverAddress, int serverPort) {
    this.serverAddress = serverAddress;  // Alamat server yang dimasukkan oleh pengguna
    this.serverPort = serverPort;        // Port server yang dimasukkan oleh pengguna
    initAudioRecord();
    initSocket();
}

// Untuk mode Offline atau VPN (menggunakan loopback address dan port default)
public AudioRecorder() {
    this.serverAddress = InetAddress.getLoopbackAddress(); // Loopback address untuk offline
    this.serverPort = 12345;  // Port default untuk offline atau VPN
    initAudioRecord();
    initSocket();
}

    // Inisialisasi AudioRecord untuk perekaman
    private void initAudioRecord() {
        int sampleRate = 44100;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        audioBuffer = new byte[bufferSize];

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
        );
    }

    // Inisialisasi soket untuk pengiriman data
    private void initSocket() {
        try {
            socket = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Mulai perekaman dan pengiriman data audio
    public void startRecording() {
        isRecording = true;
        audioRecord.startRecording();
        new Thread(() -> {
            while (isRecording) {
                int read = audioRecord.read(audioBuffer, 0, bufferSize);
                if (read > 0) {
                    sendAudioDataToServer(audioBuffer);
                }
            }
        }).start();
    }

    // Mengirimkan data audio ke server
    private void sendAudioDataToServer(byte[] audioData) {
        DatagramPacket packet = new DatagramPacket(audioData, audioData.length, serverAddress, serverPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Stop perekaman
    public void stopRecording() {
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
