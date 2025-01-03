package com.voicechat.sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class AudioPlayer {
    private AudioTrack audioTrack;
    private DatagramSocket socket;
    private int bufferSize;
    private boolean isReceiving;

    public AudioPlayer(String filePath) {
    initAudioTrack();
    initSocket();
    // Lakukan sesuatu dengan filePath jika diperlukan
}

    // Inisialisasi AudioTrack untuk pemutaran suara
    private void initAudioTrack() {
        int sampleRate = 44100;
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize,
                AudioTrack.MODE_STREAM
        );
        audioTrack.play();
    }

    // Inisialisasi soket untuk menerima data
    private void initSocket() {
        try {
            socket = new DatagramSocket(12345);  // Ganti dengan port yang sesuai
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Mulai menerima dan memutar audio
    public void startReceiving() {
        isReceiving = true;
        new Thread(() -> {
            while (isReceiving) {
                byte[] receivedData = receiveAudioData();
                if (receivedData != null) {
                    audioTrack.write(receivedData, 0, receivedData.length);
                }
            }
        }).start();
    }

    // Menerima data audio dari soket
    private byte[] receiveAudioData() {
        byte[] buffer = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
            return packet.getData();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Berhenti menerima audio dan melepaskan sumber daya
    public void stopReceiving() {
        isReceiving = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
        }
    }
}
