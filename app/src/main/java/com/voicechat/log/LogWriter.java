package com.voicechat;

import android.content.Context;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
    private final File logDirectory;
    private final File logFile;

    public LogWriter(Context context) {
        logDirectory = new File(context.getFilesDir(), "logs");
        if (!logDirectory.exists()) {
            logDirectory.mkdirs();
        }
        logFile = new File(logDirectory, "app_log.txt");
    }

    public void writeLog(String message) {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.append(message).append("\n");
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
