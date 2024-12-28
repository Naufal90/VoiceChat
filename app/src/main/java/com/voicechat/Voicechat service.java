package com.voicechat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class VoiceChatService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Implementasikan logika untuk memulai voice chat
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
