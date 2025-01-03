package com.voicechat.sound;

public class AudioRecorder {
    private AudioRecord audioRecord;
    private int bufferSize;
    private byte[] audioBuffer;

    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;

    public AudioRecorder(InetAddress serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
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
        audioRecord.startRecording();
        new Thread(() -> {
            while (true) {
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
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }
}￼Enter
