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

import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder> {

    public interface OnChannelClickListener {
        void onChannelClick(Channel channel);
    }

    private List<Channel> channels;
    private final OnChannelClickListener listener;

    public ChannelAdapter(List<Channel> channels, OnChannelClickListener listener) {
        this.channels = channels;
        this.listener = listener;
    }

    public void updateData(List<Channel> newChannels) {
        this.channels = newChannels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_channel, parent, false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
        holder.bind(channels.get(position));
    }

    @Override
    public int getItemCount() {
        return channels != null ? channels.size() : 0;
    }

    class ChannelViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivLogo;
        private final TextView tvName, tvCategory;
        private final View ivLive;

        ChannelViewHolder(View itemView) {
            super(itemView);
            ivLogo    = itemView.findViewById(R.id.iv_channel_logo);
            tvName    = itemView.findViewById(R.id.tv_channel_name);
            tvCategory = itemView.findViewById(R.id.tv_channel_category);
            ivLive    = itemView.findViewById(R.id.badge_live);
        }

        void bind(Channel channel) {
            tvName.setText(channel.getName());
            tvCategory.setText(channel.getCategory() != null ? channel.getCategory() : "");
            ivLive.setVisibility(channel.isActive() ? View.VISIBLE : View.GONE);

            if (channel.getLogo() != null && !channel.getLogo().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(channel.getLogo())
                        .placeholder(R.drawable.ic_channel_placeholder)
                        .error(R.drawable.ic_channel_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivLogo);
            } else {
                ivLogo.setImageResource(R.drawable.ic_channel_placeholder);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onChannelClick(channel);
            });
        }
    }
}
