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
import com.moontv.app.models.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para el carrusel grande estilo Bix Plus.
 * Muestra imagen de fondo, badge Popular, título y descripción.
 */
public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.ViewHolder> {

    public interface OnItemClick { void onClick(Channel channel); }

    private List<Channel> channels;
    private final OnItemClick listener;

    public FeaturedAdapter(List<Channel> channels, OnItemClick listener) {
        this.channels = channels != null ? channels : new ArrayList<>();
        this.listener = listener;
    }

    public void updateData(List<Channel> data) {
        this.channels = data != null ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_featured_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        h.bind(channels.get(pos));
    }

    @Override public int getItemCount() { return channels.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBg;
        TextView tvTitle, tvDesc, tvBadge;

        ViewHolder(View v) {
            super(v);
            ivBg    = v.findViewById(R.id.iv_featured_bg);
            tvTitle = v.findViewById(R.id.tv_featured_title);
            tvDesc  = v.findViewById(R.id.tv_featured_desc);
            tvBadge = v.findViewById(R.id.tv_popular_badge);
        }

        void bind(Channel channel) {
            tvTitle.setText(channel.getName());

            // Descripción: país o categoría como subtítulo
            String desc = "";
            if (channel.getCategory() != null) desc += channel.getCategory();
            if (channel.getCountry() != null && !channel.getCountry().isEmpty())
                desc += (desc.isEmpty() ? "" : " · ") + channel.getCountry();
            tvDesc.setText(desc.isEmpty() ? "Canal en vivo" : desc);
            tvDesc.setVisibility(desc.isEmpty() ? View.GONE : View.VISIBLE);

            // Badge popular para canales con muchas vistas
            if (channel.getViewCount() > 100) {
                tvBadge.setVisibility(View.VISIBLE);
            }

            // Imagen de fondo
            if (channel.getLogo() != null && !channel.getLogo().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(channel.getLogo())
                        .placeholder(R.drawable.ic_channel_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivBg);
            } else {
                ivBg.setImageResource(R.drawable.ic_channel_placeholder);
            }

            itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(channel); });
        }
    }
}
