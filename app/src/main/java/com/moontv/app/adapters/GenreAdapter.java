package com.moontv.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moontv.app.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Adapter para géneros con imagen de fondo estilo Bix Plus */
public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    public interface OnGenreClick { void onClick(String genre); }

    // Colores de fondo por género (sin imágenes reales, usamos colores)
    private static final Map<String, Integer> GENRE_COLORS = new HashMap<String, Integer>() {{
        put("Acción",       0xFF1E1B4B);
        put("Drama",        0xFF1F2937);
        put("Comedia",      0xFF3B1F00);
        put("Romance",      0xFF4C0519);
        put("Terror",       0xFF0F0F0F);
        put("Ciencia",      0xFF0C1A33);
        put("Deportes",     0xFF052E16);
        put("Noticias",     0xFF1E1E1E);
        put("Infantil",     0xFF4A1942);
        put("Música",       0xFF1C1917);
        put("Documentales", 0xFF1A1205);
        put("Entretenimiento", 0xFF1E1B4B);
    }};

    private static final Map<String, String> GENRE_EMOJI = new HashMap<String, String>() {{
        put("Acción",       "💥"); put("Drama",        "🎭");
        put("Comedia",      "😂"); put("Romance",      "❤️");
        put("Terror",       "👻"); put("Ciencia",      "🔬");
        put("Deportes",     "⚽"); put("Noticias",     "📰");
        put("Infantil",     "🧒"); put("Música",       "🎵");
        put("Documentales", "🎞"); put("Entretenimiento","✨");
    }};

    private final List<String> genres;
    private final OnGenreClick listener;

    public GenreAdapter(List<String> genres, OnGenreClick listener) {
        this.genres   = genres;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_genre, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) { h.bind(genres.get(pos)); }
    @Override public int getItemCount() { return genres != null ? genres.size() : 0; }

    class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        TextView tvName;
        ViewHolder(View v) {
            super(v);
            root   = v.findViewById(R.id.iv_genre_bg);
            tvName = v.findViewById(R.id.tv_genre_name);
        }
        void bind(String genre) {
            String emoji = GENRE_EMOJI.get(genre);
            tvName.setText(emoji != null ? emoji + "\n" + genre : genre);
            Integer color = GENRE_COLORS.get(genre);
            if (color != null && root != null) root.setBackgroundColor(color);
            itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(genre); });
        }
    }
}
