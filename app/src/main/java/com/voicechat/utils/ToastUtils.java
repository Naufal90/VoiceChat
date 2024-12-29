package com.voicechat

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    // Menampilkan pesan toast
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
