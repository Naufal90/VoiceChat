package com.voicechat;

public class VoiceModeHandler {

    public enum Mode {
        OFFLINE,
        TUNNEL,
        PLUGIN,
        ONLINE,
        VPN
    }

    private static Mode currentMode = Mode.OFFLINE;

    public static void setMode(Mode mode) {
        currentMode = mode;
        // Implementasikan logika khusus untuk setiap mode
        switch (currentMode) {
            case OFFLINE:
                // Mode Offline: Gunakan WLAN lokal
                break;
            case TUNNEL:
                // Tunnel Mode: Menggunakan ngrok atau Playit
                break;
            case PLUGIN:
                // Plugin Mode: Menghubungkan ke server Minecraft
                break;
            case ONLINE:
                // Mode Online: Menggunakan internet untuk komunikasi
                break;
            case VPN:
                // Mode VPN: Menggunakan VPN untuk komunikasi
                break;
        }
    }

    public static Mode getCurrentMode() {
        return currentMode;
    }
}
