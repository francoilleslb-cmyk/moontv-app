package com.moontv.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.moontv.app.R;
import com.moontv.app.models.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    public interface OnEventClick { void onClick(Event event); }

    private List<Event> events;
    private final OnEventClick listener;

    private static final SimpleDateFormat ISO_FORMAT;
    private static final SimpleDateFormat TIME_FORMAT;
    static {
        ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        ISO_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
        // TIME_FORMAT usa la zona horaria local del dispositivo
    }

    public EventsAdapter(List<Event> events, OnEventClick listener) {
        this.events   = events != null ? events : new ArrayList<>();
        this.listener = listener;
    }

    public void updateData(List<Event> data) {
        this.events = data != null ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) { h.bind(events.get(pos)); }
    @Override public int getItemCount() { return events.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogoHome, ivLogoAway;
        TextView tvFlag, tvCompetition, tvTeams, tvTime, tvStatus, tvChannelCount;

        ViewHolder(View v) {
            super(v);
            ivLogoHome    = v.findViewById(R.id.iv_logo_home);
            ivLogoAway    = v.findViewById(R.id.iv_logo_away);
            tvFlag        = v.findViewById(R.id.tv_flag);
            tvCompetition = v.findViewById(R.id.tv_competition);
            tvTeams       = v.findViewById(R.id.tv_teams);
            tvTime        = v.findViewById(R.id.tv_time);
            tvStatus      = v.findViewById(R.id.tv_status);
            tvChannelCount= v.findViewById(R.id.tv_channel_count);
        }

        void bind(Event event) {
            // Bandera + competencia
            tvFlag.setText(event.getFlagEmoji());
            tvCompetition.setText(event.getCompetition());

            // Equipos
            String home = event.getTeamHome() != null ? event.getTeamHome() : "";
            String away = event.getTeamAway() != null ? event.getTeamAway() : "";
            if (!away.isEmpty()) {
                tvTeams.setText(home + "  vs  " + away);
            } else {
                tvTeams.setText(home);
            }

            // Hora local
            tvTime.setText(formatTime(event.getDatetime()));

            // Estado EN VIVO / próximo
            if (event.isLive()) {
                tvStatus.setText("● EN VIVO");
                tvStatus.setTextColor(0xFFEF4444);
                tvStatus.setVisibility(View.VISIBLE);
            } else if ("finished".equals(event.getStatus())) {
                tvStatus.setText("Finalizado");
                tvStatus.setTextColor(0xFF6B7280);
                tvStatus.setVisibility(View.VISIBLE);
            } else {
                tvStatus.setVisibility(View.GONE);
            }

            // Cantidad de canales
            int chCount = event.getChannels() != null ? event.getChannels().size() : 0;
            if (chCount > 0) {
                tvChannelCount.setText("📺 " + chCount + (chCount == 1 ? " canal" : " canales"));
                tvChannelCount.setVisibility(View.VISIBLE);
            } else {
                tvChannelCount.setVisibility(View.GONE);
            }

            // Logos de equipos
            loadLogo(event.getLogoHome(), ivLogoHome);
            loadLogo(event.getLogoAway(), ivLogoAway);

            // Visibilidad logos
            ivLogoHome.setVisibility(event.getLogoHome() != null ? View.VISIBLE : View.GONE);
            ivLogoAway.setVisibility(event.getLogoAway() != null && !away.isEmpty() ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(event); });
        }

        private void loadLogo(String url, ImageView iv) {
            if (url != null && !url.isEmpty()) {
                Glide.with(iv.getContext())
                        .load(url)
                        .transform(new CircleCrop())
                        .placeholder(R.drawable.ic_channel_placeholder)
                        .into(iv);
            }
        }

        private String formatTime(String isoDatetime) {
            if (isoDatetime == null) return "--:--";
            try {
                // Intentar distintos formatos ISO
                String cleaned = isoDatetime.replace("Z", "+00:00");
                Date date = ISO_FORMAT.parse(isoDatetime.endsWith("Z")
                        ? isoDatetime.replace("Z", ".000Z")
                        : isoDatetime + ".000Z");
                if (date != null) return TIME_FORMAT.format(date);
            } catch (ParseException e) {
                // Fallback: extraer HH:mm del string
                if (isoDatetime.length() >= 16) {
                    return isoDatetime.substring(11, 16);
                }
            }
            return "--:--";
        }
    }
}
