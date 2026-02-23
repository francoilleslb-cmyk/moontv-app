package com.moontv.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.moontv.app.R;
import com.moontv.app.adapters.ChannelCompactAdapter;
import com.moontv.app.adapters.EventsAdapter;
import com.moontv.app.adapters.FeaturedAdapter;
import com.moontv.app.adapters.GenreAdapter;
import com.moontv.app.adapters.MediaAdapter;
import com.moontv.app.models.ApiResponse;
import com.moontv.app.models.Channel;
import com.moontv.app.models.Event;
import com.moontv.app.models.Movie;
import com.moontv.app.models.Series;
import com.moontv.app.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvFeatured, rvChannels, rvCategories, rvMovies, rvSeries, rvEvents;
    private ProgressBar progressBar;
    private TextView tvError;
    private EditText etSearch;

    private EventsAdapter eventsAdapter;
    private FeaturedAdapter featuredAdapter;
    private ChannelCompactAdapter channelCompactAdapter;
    private GenreAdapter genreAdapter;
    private MediaAdapter moviesAdapter, seriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RetrofitClient.getInstance(this);

        initViews();
        setupAdapters();
        setupBottomNav();
        loadAllData();
        setupSearch();
    }

    private void initViews() {
        rvFeatured = findViewById(R.id.rv_featured);
        rvChannels = findViewById(R.id.rv_channels);
        rvCategories = findViewById(R.id.rv_categories);
        rvMovies = findViewById(R.id.rv_movies);
        rvSeries = findViewById(R.id.rv_series);
        progressBar = findViewById(R.id.progress_bar);
        tvError = findViewById(R.id.tv_error);
        etSearch = findViewById(R.id.et_search);
        rvEvents = findViewById(R.id.rv_events);

        // Header: quitar saludo de usuario, mostrar subtítulo fijo
        TextView tvWelcome = findViewById(R.id.tv_welcome);
        if (tvWelcome != null)
            tvWelcome.setText("Tu entretenimiento en un lugar");

        // Botones de categoría rápida
        View btnMovies = findViewById(R.id.btn_cat_movies);
        View btnSeries = findViewById(R.id.btn_cat_series);
        View btnTv = findViewById(R.id.btn_cat_tv);

        if (btnMovies != null)
            btnMovies.setOnClickListener(v -> openCategory("Películas", "movies"));
        if (btnSeries != null)
            btnSeries.setOnClickListener(v -> openCategory("Series", "series"));
        if (btnTv != null)
            btnTv.setOnClickListener(v -> openCategory("TV en Vivo", null));

        // Botones header
        View btnFav = findViewById(R.id.btn_favorites);
        View btnSet = findViewById(R.id.btn_settings);
        if (btnFav != null)
            btnFav.setOnClickListener(v -> startActivity(new Intent(this, FavoritesActivity.class)));
        if (btnSet != null)
            btnSet.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void openCategory(String name, String type) {
        Intent i = new Intent(this, ChannelListActivity.class);
        i.putExtra("category_name", name);
        if (type != null)
            i.putExtra("content_type", type);
        startActivity(i);
    }

    private void setupAdapters() {
        // Eventos del día
        eventsAdapter = new EventsAdapter(new ArrayList<>(), event -> {
            Intent i = new Intent(this, EventDetailActivity.class);
            i.putExtra("event_json", new Gson().toJson(event));
            startActivity(i);
        });
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(eventsAdapter);

        // Carrusel grande featured (canales destacados)
        featuredAdapter = new FeaturedAdapter(new ArrayList<>(), this::openChannelPlayer);
        rvFeatured.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFeatured.setAdapter(featuredAdapter);

        // Fila compacta TV (logos circulares)
        channelCompactAdapter = new ChannelCompactAdapter(new ArrayList<>(), this::openChannelPlayer);
        rvChannels.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvChannels.setAdapter(channelCompactAdapter);

        // Géneros
        genreAdapter = new GenreAdapter(new ArrayList<>(), genre -> {
            Intent i = new Intent(this, ChannelListActivity.class);
            i.putExtra("category_name", genre);
            startActivity(i);
        });
        rvCategories.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(genreAdapter);

        // Películas
        moviesAdapter = new MediaAdapter(new ArrayList<>(), item -> openMediaPlayer(
                item.getStreamUrl(), item.getTitle(), item.getPoster(), null));
        rvMovies.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvMovies.setAdapter(moviesAdapter);

        // Series
        seriesAdapter = new MediaAdapter(new ArrayList<>(), item -> openMediaPlayer(
                item.getStreamUrl() != null ? item.getStreamUrl() : "",
                item.getTitle(), item.getPoster(), null));
        rvSeries.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSeries.setAdapter(seriesAdapter);
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_tv) {
                openCategory("TV en Vivo", null);
                return true;
            } else if (id == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadAllData() {
        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        loadChannels();
        loadEvents();
        loadCategories();
        loadMovies();
        loadSeries();
    }

    private void loadChannels() {
        RetrofitClient.getInstance().getApiService().getAllChannels()
                .enqueue(new Callback<ApiResponse<List<Channel>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Channel>>> call,
                            Response<ApiResponse<List<Channel>>> r) {
                        progressBar.setVisibility(View.GONE);
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                            List<Channel> list = r.body().getData();
                            featuredAdapter.updateData(list);
                            channelCompactAdapter.updateData(list);
                        } else {
                            showError("No se pudieron cargar los canales");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Channel>>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        showError("Sin conexión al servidor.\nVerificá la URL en Ajustes ⚙");
                    }
                });
    }

    private void loadCategories() {
        RetrofitClient.getInstance().getApiService().getCategories()
                .enqueue(new Callback<ApiResponse<List<String>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<String>>> call,
                            Response<ApiResponse<List<String>>> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                            List<String> cats = r.body().getData();
                            genreAdapter = new GenreAdapter(cats, genre -> {
                                Intent i = new Intent(MainActivity.this, ChannelListActivity.class);
                                i.putExtra("category_name", genre);
                                startActivity(i);
                            });
                            rvCategories.setAdapter(genreAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<String>>> c, Throwable t) {
                    }
                });
    }

    private void loadMovies() {
        RetrofitClient.getInstance().getApiService().getAllMovies()
                .enqueue(new Callback<ApiResponse<List<Movie>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Movie>>> c,
                            Response<ApiResponse<List<Movie>>> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess())
                            moviesAdapter.updateMovies(r.body().getData());
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Movie>>> c, Throwable t) {
                    }
                });
    }

    private void loadSeries() {
        RetrofitClient.getInstance().getApiService().getAllSeries()
                .enqueue(new Callback<ApiResponse<List<Series>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Series>>> c,
                            Response<ApiResponse<List<Series>>> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess())
                            seriesAdapter.updateSeries(r.body().getData());
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Series>>> c, Throwable t) {
                    }
                });
    }

    private void loadEvents() {
        RetrofitClient.getInstance().getApiService().getTodayEvents()
                .enqueue(new Callback<ApiResponse<List<Event>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Event>>> c,
                            Response<ApiResponse<List<Event>>> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                            List<Event> events = r.body().getData();
                            eventsAdapter.updateData(events);
                            if (rvEvents != null)
                                rvEvents.setVisibility(events.isEmpty() ? View.GONE : View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Event>>> c, Throwable t) {
                        if (rvEvents != null)
                            rvEvents.setVisibility(View.GONE);
                    }
                });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {
            }

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (s.length() == 0)
                    loadChannels();
                else if (s.length() >= 2)
                    searchChannels(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void searchChannels(String q) {
        RetrofitClient.getInstance().getApiService().searchChannels(q)
                .enqueue(new Callback<ApiResponse<List<Channel>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Channel>>> c,
                            Response<ApiResponse<List<Channel>>> r) {
                        if (r.isSuccessful() && r.body() != null)
                            featuredAdapter.updateData(r.body().getData());
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Channel>>> c, Throwable t) {
                    }
                });
    }

    private void openChannelPlayer(Channel channel) {
        Intent i = new Intent(this, PlayerActivity.class);
        i.putExtra("stream_url", channel.getStreamUrl());
        i.putExtra("channel_name", channel.getName());
        i.putExtra("channel_logo", channel.getLogo());
        i.putExtra("channel_id", channel.getId());
        // Pasar servidores fallback como JSON
        if (channel.getServers() != null && !channel.getServers().isEmpty()) {
            i.putExtra("servers_json", new com.google.gson.Gson().toJson(channel.getServers()));
        }
        startActivity(i);
    }

    private void openMediaPlayer(String url, String name, String logo, String id) {
        Intent i = new Intent(this, PlayerActivity.class);
        i.putExtra("stream_url", url);
        i.putExtra("channel_name", name);
        i.putExtra("channel_logo", logo);
        i.putExtra("channel_id", id);
        startActivity(i);
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }
}
