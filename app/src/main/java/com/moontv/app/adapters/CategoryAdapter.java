package com.moontv.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moontv.app.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter para categorías de canales.
 * El backend devuelve List<String> desde GET /api/channels/categories
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnCategoryClick { void onClick(String category); }

    // Mapa de emojis por nombre de categoría
    private static final Map<String, String> EMOJIS = new HashMap<String, String>() {{
        put("deportes",    "⚽"); put("sports",     "⚽");
        put("noticias",    "📰"); put("news",       "📰");
        put("entretenimiento", "🎭"); put("entertainment", "🎭");
        put("música",      "🎵"); put("music",      "🎵");
        put("infantil",    "🧒"); put("kids",       "🧒");
        put("películas",   "🎬"); put("movies",     "🎬");
        put("series",      "📺"); put("tv shows",   "📺");
        put("documentales","🔭"); put("documentary","🔭");
        put("cocina",      "🍳"); put("cooking",    "🍳");
        put("viajes",      "✈️"); put("travel",     "✈️");
        put("ciencia",     "🔬"); put("science",    "🔬");
        put("naturaleza",  "🌿"); put("nature",     "🌿");
    }};

    private List<String> categories;
    private final OnCategoryClick listener;

    public CategoryAdapter(List<String> categories, OnCategoryClick listener) {
        this.categories = categories;
        this.listener   = listener;
    }

    public void updateData(List<String> data) {
        this.categories = data;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        h.bind(categories.get(pos));
    }

    @Override public int getItemCount() { return categories != null ? categories.size() : 0; }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvName;
        ViewHolder(View v) {
            super(v);
            tvIcon = v.findViewById(R.id.tv_category_icon);
            tvName = v.findViewById(R.id.tv_category_name);
            // Ocultar el contador — no tenemos ese dato con List<String>
            View tvCount = v.findViewById(R.id.tv_channel_count);
            if (tvCount != null) tvCount.setVisibility(View.GONE);
        }
        void bind(String category) {
            tvName.setText(category);
            String emoji = EMOJIS.get(category.toLowerCase());
            tvIcon.setText(emoji != null ? emoji : "📺");
            itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(category); });
        }
    }
}
