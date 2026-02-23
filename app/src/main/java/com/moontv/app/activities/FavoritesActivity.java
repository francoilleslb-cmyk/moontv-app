package com.moontv.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moontv.app.R;
import com.moontv.app.adapters.ChannelAdapter;
import com.moontv.app.models.Channel;
import com.moontv.app.utils.FavoritesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Favoritos guardados localmente en el dispositivo (sin login).
 * Usa FavoritesManager que persiste IDs en SharedPreferences.
 */
public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView   rvFavorites;
    private TextView       tvEmpty, tvError;
    private ProgressBar    progressBar;
    private ChannelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void initViews() {
        rvFavorites = findViewById(R.id.rv_favorites);
        tvEmpty     = findViewById(R.id.tv_empty);
        tvError     = findViewById(R.id.tv_error);
        progressBar = findViewById(R.id.progress_bar);

        if (progressBar != null) progressBar.setVisibility(View.GONE);

        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        adapter = new ChannelAdapter(new ArrayList<>(), channel -> {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("stream_url",   channel.getStreamUrl());
            intent.putExtra("channel_name", channel.getName());
            intent.putExtra("channel_logo", channel.getLogo());
            intent.putExtra("channel_id",   channel.getId());
            startActivity(intent);
        });

        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        rvFavorites.setAdapter(adapter);
    }

    private void loadFavorites() {
        // Favoritos locales: lista de objetos Channel guardados en SharedPrefs
        FavoritesManager fm = FavoritesManager.getInstance(this);
        List<Channel> favs  = fm.getFavoriteChannels();

        adapter.updateData(favs);
        boolean empty = favs.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvFavorites.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
