package com.voicechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class VoiceChatReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle receiving voice data
        String voiceData = intent.getStringExtra("voice_data");
        if (voiceData != null) {
            Log.d("VoiceChatReceiver", "Received voice data: " + voiceData);
            // Process the voice data (for example, play it back)
        }
    }
}
