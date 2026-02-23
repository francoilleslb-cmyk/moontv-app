package com.moontv.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moontv.app.R;
import com.moontv.app.models.Channel;

import java.util.ArrayList;
import java.util.List;

/** Adapter compacto con logos circulares para la fila de TV en Vivo */
public class ChannelCompactAdapter extends RecyclerView.Adapter<ChannelCompactAdapter.ViewHolder> {

    public interface OnChannelClick { void onClick(Channel channel); }

    private List<Channel> channels;
    private final OnChannelClick listener;

    public ChannelCompactAdapter(List<Channel> channels, OnChannelClick listener) {
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
                .inflate(R.layout.item_channel_compact, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) { h.bind(channels.get(pos)); }
    @Override public int getItemCount() { return channels.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo;
        TextView tvName;
        ViewHolder(View v) {
            super(v);
            ivLogo = v.findViewById(R.id.iv_channel_logo);
            tvName = v.findViewById(R.id.tv_channel_name);
        }
        void bind(Channel channel) {
            tvName.setText(channel.getName());
            if (channel.getLogo() != null && !channel.getLogo().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(channel.getLogo())
                        .circleCrop()
                        .placeholder(R.drawable.ic_channel_placeholder)
                        .into(ivLogo);
            } else {
                ivLogo.setImageResource(R.drawable.ic_channel_placeholder);
            }
            itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(channel); });
        }
    }
}
