package com.moontv.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.gson.Gson;
import com.moontv.app.R;
import com.moontv.app.models.Event;

import java.util.List;

/**
 * Pantalla de detalle de un evento deportivo.
 * Muestra los equipos y los canales que lo transmiten.
 * Al tocar un canal abre directamente el reproductor.
 */
public class EventDetailActivity extends AppCompatActivity {

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // El evento llega serializado como JSON
        String eventJson = getIntent().getStringExtra("event_json");
        if (eventJson == null) { finish(); return; }
        event = new Gson().fromJson(eventJson, Event.class);

        initViews();
    }

    private void initViews() {
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        // Bandera + competencia
        ((TextView) findViewById(R.id.tv_flag)).setText(event.getFlagEmoji());
        ((TextView) findViewById(R.id.tv_competition)).setText(event.getCompetition());

        // Equipos
        TextView tvHome = findViewById(R.id.tv_team_home);
        TextView tvAway = findViewById(R.id.tv_team_away);
        TextView tvVs   = findViewById(R.id.tv_vs);

        tvHome.setText(event.getTeamHome() != null ? event.getTeamHome() : "");
        tvAway.setText(event.getTeamAway() != null ? event.getTeamAway() : "");
        tvVs.setVisibility(event.getTeamAway() != null && !event.getTeamAway().isEmpty()
                ? View.VISIBLE : View.GONE);

        // Logos
        ImageView ivHome = findViewById(R.id.iv_logo_home);
        ImageView ivAway = findViewById(R.id.iv_logo_away);
        loadLogo(event.getLogoHome(), ivHome);
        loadLogo(event.getLogoAway(), ivAway);

        // Hora
        ((TextView) findViewById(R.id.tv_time)).setText(
                formatTime(event.getDatetime()));

        // Estado
        TextView tvStatus = findViewById(R.id.tv_status);
        if (event.isLive()) {
            tvStatus.setText("● EN VIVO AHORA");
            tvStatus.setVisibility(View.VISIBLE);
        } else {
            tvStatus.setVisibility(View.GONE);
        }

        // Lista de canales
        buildChannelList();
    }

    private void buildChannelList() {
        LinearLayout container = findViewById(R.id.ll_channels);
        List<Event.EventChannel> channels = event.getChannels();

        if (channels == null || channels.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No hay canales disponibles para este evento");
            empty.setTextColor(0xFF9CA3AF);
            empty.setPadding(0, 16, 0, 0);
            container.addView(empty);
            return;
        }

        for (Event.EventChannel ch : channels) {
            View item = LayoutInflater.from(this)
                    .inflate(R.layout.item_event_channel, container, false);

            // Logo
            ImageView ivLogo = item.findViewById(R.id.iv_ch_logo);
            loadLogo(ch.getLogo(), ivLogo);

            // Nombre
            ((TextView) item.findViewById(R.id.tv_ch_name)).setText(ch.getName());

            // URL del stream (parcial, para mostrar)
            String url = ch.getStreamUrl();
            ((TextView) item.findViewById(R.id.tv_ch_url)).setText(
                    url != null ? (url.length() > 45 ? url.substring(0, 45) + "…" : url) : "");

            // Click → abrir reproductor
            item.setOnClickListener(v -> {
                Intent intent = new Intent(this, PlayerActivity.class);
                intent.putExtra("stream_url",   ch.getStreamUrl());
                intent.putExtra("channel_name", ch.getName() + " — " + event.getTitle());
                intent.putExtra("channel_logo", ch.getLogo());
                startActivity(intent);
            });

            container.addView(item);
        }
    }

    private void loadLogo(String url, ImageView iv) {
        if (url != null && !url.isEmpty()) {
            Glide.with(this).load(url).transform(new CircleCrop())
                    .placeholder(R.drawable.ic_channel_placeholder).into(iv);
        }
    }

    private String formatTime(String iso) {
        if (iso == null || iso.length() < 16) return "--:--";
        return iso.substring(11, 16);
    }
}
