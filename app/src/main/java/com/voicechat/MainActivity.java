package com.voicechat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int SAMPLE_RATE = 16000;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private AudioRecord recorder;
    private AudioTrack player;
    private boolean isRecording = false;

    private Button btnStartRecording;
    private Button btnStopRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi tombol
        btnStartRecording = findViewById(R.id.btnStartRecording);
        btnStopRecording = findViewById(R.id.btnStopRecording);

        // Memeriksa izin untuk rekaman audio
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }

        // Listener untuk tombol mulai rekaman
        btnStartRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
                btnStartRecording.setEnabled(false); // Menonaktifkan tombol mulai rekaman setelah ditekan
                btnStopRecording.setEnabled(true); // Mengaktifkan tombol berhenti rekaman
            }
        });

        // Listener untuk tombol berhenti rekaman
        btnStopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
                btnStartRecording.setEnabled(true); // Mengaktifkan tombol mulai rekaman
                btnStopRecording.setEnabled(false); // Menonaktifkan tombol berhenti rekaman
            }
        });
    }

    // Menangani izin yang diminta
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Jika izin diberikan, mulai rekaman
                startRecording();
            } else {
                // Tampilkan pesan jika izin ditolak
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Memulai proses rekaman
    private void startRecording() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
            recorder.startRecording();
            isRecording = true;
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();

            // Rekam suara di thread terpisah
            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] audioBuffer = new byte[bufferSize];
                    while (isRecording) {
                        int read = recorder.read(audioBuffer, 0, audioBuffer.length);
                        if (read > 0) {
                            // Kirim data audio atau lakukan pemrosesan lebih lanjut
                            // Untuk saat ini, kita akan langsung memainkan audio
                            playAudio(audioBuffer, read);
                        }
                    }
                }
            }).start();
        } else {
            Toast.makeText(this, "Failed to initialize AudioRecord", Toast.LENGTH_SHORT).show();
        }
    }

    // Menghentikan rekaman
    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            isRecording = false;
            Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
        }
    }

    // Memutar audio yang direkam
    private void playAudio(byte[] audioData, int length) {
        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        player = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);

        player.play();
        player.write(audioData, 0, length);
    }

    // Menambahkan tombol atau logika lain untuk menghentikan rekaman jika diperlukan
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecording(); // Pastikan rekaman dihentikan ketika aktivitas dihancurkan
    }
}
