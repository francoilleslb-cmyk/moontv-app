package com.moontv.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.moontv.app.R;
import com.moontv.app.models.Movie;
import com.moontv.app.models.Series;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter genérico para Películas y Series.
 * Usa un modelo intermedio MediaItem para unificar Movie y Series.
 */
public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    public interface OnMediaClick { void onClick(MediaItem item); }

    public static class MediaItem {
        private final String id, title, poster, streamUrl;
        public MediaItem(String id, String title, String poster, String streamUrl) {
            this.id = id; this.title = title; this.poster = poster; this.streamUrl = streamUrl;
        }
        public String getId()        { return id; }
        public String getTitle()     { return title; }
        public String getPoster()    { return poster; }
        public String getStreamUrl() { return streamUrl; }
    }

    private List<MediaItem> items;
    private final OnMediaClick listener;

    public MediaAdapter(List<MediaItem> items, OnMediaClick listener) {
        this.items    = items != null ? items : new ArrayList<>();
        this.listener = listener;
    }

    public void updateMovies(List<Movie> movies) {
        List<MediaItem> list = new ArrayList<>();
        if (movies != null) for (Movie m : movies)
            list.add(new MediaItem(m.getId(), m.getTitle(), m.getPoster(), m.getStreamUrl()));
        this.items = list;
        notifyDataSetChanged();
    }

    public void updateSeries(List<Series> series) {
        List<MediaItem> list = new ArrayList<>();
        if (series != null) for (Series s : series)
            list.add(new MediaItem(s.getId(), s.getTitle(), s.getPoster(), null));
        this.items = list;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_media, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) { h.bind(items.get(pos)); }
    @Override public int getItemCount() { return items.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle;
        ViewHolder(View v) {
            super(v);
            ivPoster = v.findViewById(R.id.iv_media_poster);
            tvTitle  = v.findViewById(R.id.tv_media_title);
        }
        void bind(MediaItem item) {
            tvTitle.setText(item.getTitle());
            if (item.getPoster() != null && !item.getPoster().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getPoster())
                        .placeholder(R.drawable.ic_channel_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivPoster);
            } else {
                ivPoster.setImageResource(R.drawable.ic_channel_placeholder);
            }
            itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(item); });
        }
    }
}
