package com.moontv.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.moontv.app.R;
import com.moontv.app.adapters.ChannelAdapter;
import com.moontv.app.models.ApiResponse;
import com.moontv.app.models.Channel;
import com.moontv.app.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChannelListActivity extends AppCompatActivity {

    private RecyclerView     rvChannels;
    private ProgressBar      progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private TextView         tvTitle, tvError, tvEmpty;
    private ChannelAdapter   channelAdapter;
    private String           categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);

        categoryName = getIntent().getStringExtra("category_name");
        initViews();
        loadChannels();
    }

    private void initViews() {
        rvChannels   = findViewById(R.id.rv_channels);
        progressBar  = findViewById(R.id.progress_bar);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        tvTitle      = findViewById(R.id.tv_title);
        tvError      = findViewById(R.id.tv_error);
        tvEmpty      = findViewById(R.id.tv_empty);

        tvTitle.setText(categoryName != null ? categoryName : "Canales");

        channelAdapter = new ChannelAdapter(new ArrayList<>(), channel -> {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("stream_url",   channel.getStreamUrl());
            intent.putExtra("channel_name", channel.getName());
            intent.putExtra("channel_logo", channel.getLogo());
            intent.putExtra("channel_id",   channel.getId());
            startActivity(intent);
        });

        rvChannels.setLayoutManager(new LinearLayoutManager(this));
        rvChannels.setAdapter(channelAdapter);
        swipeRefresh.setOnRefreshListener(this::loadChannels);
        swipeRefresh.setColorSchemeResources(R.color.moon_purple, R.color.moon_blue);
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());
    }

    private void loadChannels() {
        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);

        Call<ApiResponse<List<Channel>>> call = (categoryName != null)
                ? RetrofitClient.getInstance(this).getApiService().getChannelsByCategory(categoryName)
                : RetrofitClient.getInstance(this).getApiService().getAllChannels();

        call.enqueue(new Callback<ApiResponse<List<Channel>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Channel>>> c,
                                   Response<ApiResponse<List<Channel>>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Channel> list = response.body().getData();
                    channelAdapter.updateData(list);
                    tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    showError("Error al cargar canales");
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Channel>>> c, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                showError("Sin conexión: " + t.getMessage());
            }
        });
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }
}
