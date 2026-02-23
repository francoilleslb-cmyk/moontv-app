package com.moontv.app.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.ui.PlayerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moontv.app.R;
import com.moontv.app.models.ApiResponse;
import com.moontv.app.models.Channel;
import com.moontv.app.network.RetrofitClient;
import com.moontv.app.utils.ExoPlayerManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * PlayerActivity — Moon TV
 *
 * Mejoras v2:
 * ✅ Recibe lista de servidores como JSON → fallback automático
 * ✅ Botones para cambiar de servidor manualmente
 * ✅ Registra "view" en el backend al abrir el canal
 * ✅ Sin login/favoritos
 */
public class PlayerActivity extends AppCompatActivity implements ExoPlayerManager.PlayerCallback {

    private static final String TAG = "MoonTV_Player";

    private PlayerView playerView;
    private ProgressBar bufferingProgress;
    private TextView tvChannelName, tvError;
    private ImageButton btnBack;
    private Button btnRetry;
    private LinearLayout llServers;

    private ExoPlayerManager playerManager;

    private String channelName, channelId;
    private List<String> serverUrls = new ArrayList<>();
    private List<String> serverLabels = new ArrayList<>();
    private int currentServerIdx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_player);

        channelName = getIntent().getStringExtra("channel_name");
        channelId = getIntent().getStringExtra("channel_id");

        // Recopilar URLs de servidores
        String mainUrl = getIntent().getStringExtra("stream_url");
        String serversJson = getIntent().getStringExtra("servers_json");

        if (mainUrl != null && !mainUrl.isEmpty()) {
            serverUrls.add(mainUrl);
            serverLabels.add("Principal");
        }

        if (serversJson != null && !serversJson.isEmpty()) {
            try {
                Type type = new TypeToken<List<Channel.Server>>() {
                }.getType();
                List<Channel.Server> servers = new Gson().fromJson(serversJson, type);
                if (servers != null) {
                    for (Channel.Server s : servers) {
                        String su = s.getUrl();
                        if (su != null && !su.isEmpty() && !serverUrls.contains(su)) {
                            serverUrls.add(su);
                            serverLabels.add(s.getLabel());
                        }
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "No se pudo parsear servers_json: " + e.getMessage());
            }
        }

        Log.d(TAG, "Canal: " + channelName + " | Servidores: " + serverUrls.size());

        initViews();
        startPlayback(currentServerIdx);
        registerView();
    }

    private void initViews() {
        playerView = findViewById(R.id.player_view);
        bufferingProgress = findViewById(R.id.buffering_progress);
        tvChannelName = findViewById(R.id.tv_channel_name);
        tvError = findViewById(R.id.tv_player_error);
        btnBack = findViewById(R.id.btn_back);
        btnRetry = findViewById(R.id.btn_retry);
        llServers = findViewById(R.id.ll_servers);

        tvChannelName.setText(channelName != null ? channelName : "Moon TV");
        btnBack.setOnClickListener(v -> onBackPressed());
        btnRetry.setOnClickListener(v -> {
            tvError.setVisibility(View.GONE);
            btnRetry.setVisibility(View.GONE);
            startPlayback(currentServerIdx);
        });

        // Sin login → ocultar botón favorito
        View btnFavorite = findViewById(R.id.btn_favorite);
        if (btnFavorite != null)
            btnFavorite.setVisibility(View.GONE);

        buildServerButtons();
        hideSystemUI();
    }

    /** Botones de servidor — solo si hay más de uno */
    private void buildServerButtons() {
        if (llServers == null || serverUrls.size() <= 1) {
            if (llServers != null)
                llServers.setVisibility(View.GONE);
            return;
        }
        llServers.setVisibility(View.VISIBLE);
        llServers.removeAllViews();
        for (int i = 0; i < serverUrls.size(); i++) {
            final int idx = i;
            Button btn = new Button(this);
            btn.setText(serverLabels.size() > i ? serverLabels.get(i) : "Srv " + (i + 1));
            btn.setTextSize(12f);
            btn.setOnClickListener(v -> switchServer(idx));
            llServers.addView(btn);
        }
    }

    private void startPlayback(int serverIdx) {
        if (serverUrls.isEmpty()) {
            showError("URL de stream no disponible.\nEste canal no tiene stream configurado.");
            return;
        }
        currentServerIdx = serverIdx;
        String url = serverUrls.get(serverIdx);
        Log.d(TAG, "Reproduciendo servidor " + (serverIdx + 1) + "/" + serverUrls.size() + ": " + url);

        bufferingProgress.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        btnRetry.setVisibility(View.GONE);

        playerManager = new ExoPlayerManager(this);
        playerManager.setCallback(this);
        playerView.setPlayer(playerManager.preparePlayer(url));
        playerView.setUseController(true);
        playerView.setControllerShowTimeoutMs(3000);
        playerView.setControllerHideOnTouch(true);
    }

    private void switchServer(int idx) {
        if (playerManager != null)
            playerManager.releasePlayer();
        startPlayback(idx);
    }

    /** Registra vista en el backend (fire-and-forget) */
    private void registerView() {
        if (channelId == null || channelId.isEmpty())
            return;
        try {
            RetrofitClient.getInstance(this).getApiService()
                    .registerView(channelId).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> c, Response<ApiResponse<Void>> r) {
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Void>> c, Throwable t) {
                        }
                    });
        } catch (Exception e) {
            /* Ignorar si aún no inicializado */ }
    }

    // ── PlayerCallback ────────────────────────────────────────────────────────

    @Override
    public void onPlayerReady() {
        runOnUiThread(() -> {
            bufferingProgress.setVisibility(View.GONE);
            tvError.setVisibility(View.GONE);
            btnRetry.setVisibility(View.GONE);
        });
    }

    @Override
    public void onPlayerError(String errorMessage) {
        Log.e(TAG, "Error servidor " + (currentServerIdx + 1) + ": " + errorMessage);
        // Fallback automático al siguiente servidor
        int nextIdx = currentServerIdx + 1;
        if (nextIdx < serverUrls.size()) {
            Log.d(TAG, "Cambiando a servidor " + (nextIdx + 1) + " automáticamente...");
            runOnUiThread(() -> startPlayback(nextIdx));
        } else {
            runOnUiThread(() -> {
                bufferingProgress.setVisibility(View.GONE);
                showError(errorMessage + "\n\n(Se probaron " + serverUrls.size() + " servidor(es))");
                btnRetry.setVisibility(View.VISIBLE);
            });
        }
    }

    @Override
    public void onBuffering(boolean isBuffering) {
        runOnUiThread(() -> bufferingProgress.setVisibility(isBuffering ? View.VISIBLE : View.GONE));
    }

    @Override
    public void onPlaybackEnded() {
        /* stream en vivo */ }

    // ── Ciclo de vida ─────────────────────────────────────────────────────────

    @Override
    protected void onPause() {
        super.onPause();
        if (playerManager != null)
            playerManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        if (playerManager != null)
            playerManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerManager != null)
            playerManager.releasePlayer();
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
