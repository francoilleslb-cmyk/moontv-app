package com.moontv.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.moontv.app.models.User;

/**
 * Gestiona la sesión del usuario: guarda el JWT token y los datos
 * del usuario en SharedPreferences de forma segura.
 */
public class SessionManager {

    private static final String PREFS_NAME   = "moon_tv_session";
    private static final String KEY_TOKEN    = "jwt_token";
    private static final String KEY_USER     = "user_data";
    private static final String KEY_LOGGED   = "is_logged_in";

    private static SessionManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;

    private SessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    // ─── Guardar sesión tras login/registro ──────────────────────────────────

    public void saveSession(User user) {
        prefs.edit()
                .putBoolean(KEY_LOGGED, true)
                .putString(KEY_TOKEN, user.getToken())
                .putString(KEY_USER, gson.toJson(user))
                .apply();
    }

    // ─── Leer datos ──────────────────────────────────────────────────────────

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED, false);
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public User getUser() {
        String json = prefs.getString(KEY_USER, null);
        if (json == null) return null;
        return gson.fromJson(json, User.class);
    }

    public String getUserName() {
        User user = getUser();
        return user != null ? user.getName() : "Usuario";
    }

    public String getUserEmail() {
        User user = getUser();
        return user != null ? user.getEmail() : "";
    }

    // ─── Cerrar sesión ───────────────────────────────────────────────────────

    public void logout() {
        prefs.edit().clear().apply();
    }

    // ─── Token para headers de Retrofit ─────────────────────────────────────

    public String getBearerToken() {
        String token = getToken();
        return token != null ? "Bearer " + token : null;
    }
}
