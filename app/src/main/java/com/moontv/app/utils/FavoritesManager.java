package com.moontv.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moontv.app.models.Channel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de favoritos usando SharedPreferences + Gson.
 * Los canales se persisten localmente en el dispositivo.
 */
public class FavoritesManager {

    private static final String PREFS_NAME = "moon_tv_favorites";
    private static final String KEY_FAVORITES = "favorites_list";

    private static FavoritesManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;

    private FavoritesManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized FavoritesManager getInstance(Context context) {
        if (instance == null) {
            instance = new FavoritesManager(context);
        }
        return instance;
    }

    /** Devuelve la lista completa de favoritos guardados */
    public List<Channel> getFavorites() {
        String json = prefs.getString(KEY_FAVORITES, null);
        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<List<Channel>>() {}.getType();
        List<Channel> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    /** Agrega un canal a favoritos (evita duplicados) */
    public void addFavorite(Channel channel) {
        List<Channel> list = getFavorites();
        // Evitar duplicado por ID
        for (Channel c : list) {
            if (c.getId() != null && c.getId().equals(channel.getId())) return;
        }
        list.add(channel);
        save(list);
    }

    /** Elimina un canal de favoritos */
    public void removeFavorite(String channelId) {
        List<Channel> list = getFavorites();
        list.removeIf(c -> channelId.equals(c.getId()));
        save(list);
    }

    /** Toggle: si es favorito lo quita, si no lo agrega. Devuelve el nuevo estado */
    public boolean toggleFavorite(Channel channel) {
        if (isFavorite(channel.getId())) {
            removeFavorite(channel.getId());
            return false;
        } else {
            addFavorite(channel);
            return true;
        }
    }

    /** Verifica si un canal ya está en favoritos */
    public boolean isFavorite(String channelId) {
        if (channelId == null) return false;
        for (Channel c : getFavorites()) {
            if (channelId.equals(c.getId())) return true;
        }
        return false;
    }


    /** Alias de getFavorites() — usado por FavoritesActivity */
    public List<Channel> getFavoriteChannels() {
        return getFavorites();
    }

    /** Limpia todos los favoritos */
    public void clearAll() {
        prefs.edit().remove(KEY_FAVORITES).apply();
    }

    private void save(List<Channel> list) {
        prefs.edit().putString(KEY_FAVORITES, gson.toJson(list)).apply();
    }
}
