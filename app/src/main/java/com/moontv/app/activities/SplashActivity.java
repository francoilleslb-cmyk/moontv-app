package com.moontv.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.moontv.app.R;
import com.moontv.app.network.RetrofitClient;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 1800L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        // Pre-inicializar el cliente HTTP con la URL guardada
        RetrofitClient.getInstance(this);

        // Sin login: siempre ir a MainActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, SPLASH_DURATION);
    }
}
