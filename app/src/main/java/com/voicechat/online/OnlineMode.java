package com.voicechat;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSession;
import com.mojang.authlib.minecraft.MinecraftProfile;

import java.io.IOException;

public class OnlineMode {
    private Context context;

    public OnlineMode(Context context) {
        this.context = context;
    }

    // Fungsi untuk autentikasi login pengguna dan bermain di server Mojang
    public void authenticateAndPlay(String username, String password) {
        new AuthenticationTask().execute(username, password);
    }

    // AsyncTask untuk menangani login dan autentikasi
    private class AuthenticationTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];
            String password = params[1];

            try {
                // Autentikasi dengan Mojang menggunakan username dan password
                AuthenticationService authService = new AuthenticationService();
                MinecraftSession session = authService.authenticate(username, password);

                // Mendapatkan profil pemain
                MinecraftProfile profile = session.getProfile();
                
                // Verifikasi autentikasi berhasil
                return profile != null;
            } catch (IOException | AuthenticationException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            // Menampilkan pesan hasil autentikasi
            if (success) {
                Toast.makeText(context, "Login berhasil! Kamu sekarang bisa bermain online.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Login gagal! Periksa kembali kredensialmu.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
