package com.voicechat.managers;

public class VoiceModeHandler {

    public enum Mode {
        OFFLINE,
        PLUGIN,
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
            case PLUGIN:
                // Plugin Mode: Menghubungkan ke server Minecraft
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
