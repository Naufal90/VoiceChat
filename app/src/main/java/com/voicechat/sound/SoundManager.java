package com.voicechat.sound;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class SoundManager {
    private AudioRecorder audioRecorder;
    private AudioPlayer audioPlayer;
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private String mode;

    public SoundManager(String mode, InetAddress serverAddress, int serverPort) {
        this.mode = mode;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        // Inisialisasi AudioRecorder dan AudioPlayer sesuai dengan mode
        if (mode.equals("offline")) {
            initOfflineMode();
        } else if (mode.equals("plugin")) {
            initPluginMode();
        } else if (mode.equals("vpn")) {
            initVpnMode();
        }
    }

    // Inisialisasi untuk mode offline
    private void initOfflineMode() {
        audioRecorder = new AudioRecorder(serverAddress, serverPort);
        audioPlayer = new AudioPlayer();
    }

    // Inisialisasi untuk mode plugin
    private void initPluginMode() {
        audioRecorder = new AudioRecorder(serverAddress, serverPort);
        audioPlayer = new AudioPlayer();
    }

    // Inisialisasi untuk mode VPN
    private void initVpnMode() {
        audioRecorder = new AudioRecorder(serverAddress, serverPort);
        audioPlayer = new AudioPlayer();
    }

    // Mulai perekaman dan pengiriman suara sesuai mode
    public void startRecording() {
        if (audioRecorder != null) {
            audioRecorder.startRecording();
        }
    }

    // Mulai pemutaran suara sesuai mode
    public void startPlaying() {
        if (audioPlayer != null) {
            audioPlayer.startReceiving();
        }
    }

    // Stop perekaman suara
    public void stopRecording() {
        if (audioRecorder != null) {
            audioRecorder.stopRecording();
        }
    }

    // Menghentikan pemutaran suara
    public void stopPlaying() {
        if (audioPlayer != null) {
            audioPlayer.stopReceiving();
        }
    }

    // Mengirimkan data suara menggunakan soket
    public void sendAudioData(byte[] audioData) {
        DatagramPacket packet = new DatagramPacket(audioData, audioData.length, serverAddress, serverPort);
        try {
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
