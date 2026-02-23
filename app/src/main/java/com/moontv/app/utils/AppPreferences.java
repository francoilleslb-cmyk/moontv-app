package com.moontv.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Preferencias globales de la app Moon TV.
 * Permite al usuario configurar su propio backend desde la app.
 */
public class AppPreferences {

    private static final String PREFS_NAME = "moon_tv_prefs";

    // Keys
    private static final String KEY_BACKEND_URL  = "backend_url";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_AUTOPLAY     = "autoplay";
    private static final String KEY_QUALITY      = "quality";

    // Default backend URL - se puede cambiar desde SettingsActivity
    public static final String DEFAULT_BACKEND_URL = "https://moontv-backend.onrender.com/";

    private static AppPreferences instance;
    private final SharedPreferences prefs;

    private AppPreferences(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized AppPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new AppPreferences(context);
        }
        return instance;
    }

    // ─── Backend URL ─────────────────────────────────────────────────────────

    public String getBackendUrl() {
        return prefs.getString(KEY_BACKEND_URL, DEFAULT_BACKEND_URL);
    }

    public void setBackendUrl(String url) {
        // Asegura que termina en /
        if (!url.endsWith("/")) url = url + "/";
        prefs.edit().putString(KEY_BACKEND_URL, url).apply();
    }

    // ─── Autoplay ────────────────────────────────────────────────────────────

    public boolean isAutoplay() {
        return prefs.getBoolean(KEY_AUTOPLAY, true);
    }

    public void setAutoplay(boolean autoplay) {
        prefs.edit().putBoolean(KEY_AUTOPLAY, autoplay).apply();
    }

    // ─── First launch ────────────────────────────────────────────────────────

    public boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void setFirstLaunch(boolean value) {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply();
    }

    // ─── Calidad preferida ───────────────────────────────────────────────────

    public String getQuality() {
        return prefs.getString(KEY_QUALITY, "auto");
    }

    public void setQuality(String quality) {
        prefs.edit().putString(KEY_QUALITY, quality).apply();
    }

    // ─── Reset ───────────────────────────────────────────────────────────────

    public void resetToDefaults() {
        prefs.edit().clear().apply();
    }
}
