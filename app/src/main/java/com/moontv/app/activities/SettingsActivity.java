package com.moontv.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.moontv.app.R;
import com.moontv.app.network.RetrofitClient;
import com.moontv.app.utils.AppPreferences;
import com.moontv.app.utils.FavoritesManager;

public class SettingsActivity extends AppCompatActivity {

    private EditText etBackendUrl;
    private Switch   switchAutoplay;
    private TextView tvFavCount, tvAppVersion;
    private AppPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = AppPreferences.getInstance(this);
        initViews();
        loadCurrentSettings();
    }

    private void initViews() {
        etBackendUrl   = findViewById(R.id.et_backend_url);
        switchAutoplay = findViewById(R.id.switch_autoplay);
        tvFavCount     = findViewById(R.id.tv_fav_count);
        tvAppVersion   = findViewById(R.id.tv_app_version);

        // Ocultar sección de usuario (sin login)
        android.view.View userSection = findViewById(R.id.user_section);
        if (userSection != null) userSection.setVisibility(android.view.View.GONE);

        // Ocultar botón logout
        android.view.View btnLogout = findViewById(R.id.btn_logout);
        if (btnLogout != null) btnLogout.setVisibility(android.view.View.GONE);

        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.btn_save_url).setOnClickListener(v -> saveBackendUrl());
        findViewById(R.id.btn_reset_url).setOnClickListener(v -> {
            etBackendUrl.setText(AppPreferences.DEFAULT_BACKEND_URL);
            saveBackendUrl();
        });

        switchAutoplay.setOnCheckedChangeListener((btn, checked) -> prefs.setAutoplay(checked));

        // Abrir panel admin en el navegador
        android.view.View btnAdmin = findViewById(R.id.btn_admin_panel);
        if (btnAdmin != null) {
            btnAdmin.setVisibility(android.view.View.VISIBLE);
            btnAdmin.setOnClickListener(v -> {
                String adminUrl = prefs.getBackendUrl() + "admin";
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(adminUrl));
                startActivity(intent);
            });
        }

        findViewById(R.id.btn_clear_favorites).setOnClickListener(v -> confirmClearFavorites());

        tvAppVersion.setText("Moon TV v2.0");
    }

    private void loadCurrentSettings() {
        etBackendUrl.setText(prefs.getBackendUrl());
        switchAutoplay.setChecked(prefs.isAutoplay());

        int favCount = FavoritesManager.getInstance(this).getFavorites().size();
        tvFavCount.setText(favCount + " canales guardados");
    }

    private void saveBackendUrl() {
        String url = etBackendUrl.getText().toString().trim();
        if (TextUtils.isEmpty(url)) { etBackendUrl.setError("URL vacía"); return; }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            etBackendUrl.setError("Debe comenzar con http:// o https://"); return;
        }
        prefs.setBackendUrl(url);
        RetrofitClient.reset(); // Forzar reconexión con nueva URL
        Toast.makeText(this, "✅ URL guardada", Toast.LENGTH_SHORT).show();
    }

    private void confirmClearFavorites() {
        new AlertDialog.Builder(this)
                .setTitle("Borrar favoritos")
                .setMessage("¿Estás seguro? Esta acción no se puede deshacer.")
                .setPositiveButton("Borrar", (d, w) -> {
                    FavoritesManager.getInstance(this).clearAll();
                    tvFavCount.setText("0 canales guardados");
                    Toast.makeText(this, "Favoritos eliminados", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
