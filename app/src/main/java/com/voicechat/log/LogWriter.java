package com.voicechat.log;

import android.content.Context;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
    private final File logDirectory;
    private final File logFile;
    private static final long MAX_LOG_SIZE = 5 * 1024 * 1024; // 1 MB

    public LogWriter(Context context) {
        // Inisialisasi direktori log di dalam direktori aplikasi
        logDirectory = new File(context.getFilesDir(), "logs");
        if (!logDirectory.exists() && !logDirectory.mkdirs()) {
            System.err.println("Gagal membuat direktori log.");
        }
        logFile = new File(logDirectory, "app_log.txt");
    }

    public void writeLog(String message) {
        System.out.println(message); // Cetak log ke konsol
        try {
            // Periksa apakah ukuran file log melebihi batas
            if (logFile.exists() && logFile.length() > MAX_LOG_SIZE) {
                // Buat file cadangan dengan nama unik
                File backupFile = new File(logDirectory, "app_log_" + System.currentTimeMillis() + ".txt");
                if (!logFile.renameTo(backupFile)) {
                    System.err.println("Gagal memindahkan log ke file cadangan.");
                }
            }

            // Tulis log baru ke dalam file
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.append(message).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Gagal menulis log: " + e.getMessage());
        }
    }

    public void clearLogs() {
        if (logFile.exists() && !logFile.delete()) {
            System.err.println("Gagal menghapus file log.");
        }
    }
}
