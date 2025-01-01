package com.voicechat.log;

import android.content.Context;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
    private final File logDirectory;
    private final File logFile;
    private static final long MAX_LOG_SIZE = 1000000; // 1 MB

    public LogWriter(Context context) {
        logDirectory = new File(context.getFilesDir(), "logs");
        if (!logDirectory.exists()) {
            logDirectory.mkdirs();
        }
        logFile = new File(logDirectory, "app_log.txt");
    }

    public void writeLog(String message) {
        try {
            // Cek apakah ukuran file log melebihi batas MAX_LOG_SIZE
            if (logFile.exists() && logFile.length() > MAX_LOG_SIZE) {
                // Jika ya, pindahkan file log lama ke file cadangan
                File backupFile = new File(logDirectory, "app_log_" + System.currentTimeMillis() + ".txt");
                logFile.renameTo(backupFile);
            }

            // Menulis log baru ke file
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.append(message).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearLogs() {
        if (logFile.exists()) {
            logFile.delete();
        }
    }
}
