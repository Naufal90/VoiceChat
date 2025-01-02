package com.voicechat.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {

    // Mendefinisikan konstanta untuk requestCode
    private static final int REQUEST_CODE = 100;

    // Daftar izin yang diperlukan
    private static final String[] REQUIRED_PERMISSIONS = {
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.WRITE_SETTINGS,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
        Manifest.permission.CHANGE_NETWORK_STATE,
        Manifest.permission.VIBRATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
        Manifest.permission.ACCESS_VPN_STATE,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN
    };

public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    // Meminta izin untuk mengakses berbagai fitur
    public static void requestPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Mengecek izin yang belum diberikan dan meminta izin yang diperlukan
            for (String permission : REQUIRED_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, REQUEST_CODE);
                }
            }
        }
    }

    // Mengecek apakah izin sudah diberikan
    public static boolean isPermissionGranted(Activity activity, String[] REQUIRED_PERMISSIONS) {
    for (String permission : REQUIRED_PERMISSIONS) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            return false; // Jika ada satu izin yang belum diberikan
        }
    }
    return true; // Semua izin sudah diberikan
}

    // Menangani hasil permintaan izin
    public static void handlePermissionResult(int requestCode, String[] permissions, int[] grantResults, Activity activity) {
        if (requestCode == REQUEST_CODE) {
            // Menampilkan pesan jika izin ditolak
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, "Izin " + permissions[i] + " ditolak", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Pastikan aktivitas memiliki metode onRequestPermissionsResult
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, Activity activity) {
        if (requestCode == REQUEST_CODE) {
            handlePermissionResult(requestCode, permissions, grantResults, activity);
        }
    }
}
