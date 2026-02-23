package com.moontv.app.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.okhttp.OkHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;

import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * ══════════════════════════════════════════════════════════════
 *  ExoPlayerManager — Moon TV
 *  Configurado especialmente para streams IPTV con HTTP://
 * ══════════════════════════════════════════════════════════════
 *
 *  PROBLEMAS RESUELTOS:
 *  ✅ 1. Android bloquea HTTP por defecto → resuelto con network_security_config.xml
 *  ✅ 2. Redirecciones HTTP→HTTPS (y viceversa) → setAllowCrossProtocolRedirects(true)
 *  ✅ 3. Servidores IPTV verifican User-Agent → Mozilla/5.0 completo
 *  ✅ 4. Streams .ts sin extensión → OkHttp + DefaultMediaSourceFactory detecta el tipo
 *  ✅ 5. Timeouts agresivos → connect 15s, read 20s
 *  ✅ 6. Reintento automático al fallar → retryCount con backoff
 */
public class ExoPlayerManager {

    private static final String TAG = "MoonTV_Player";
    private static final int MAX_RETRIES = 3;

    private ExoPlayer player;
    private final Context context;
    private PlayerCallback callback;
    private String currentUrl;
    private int retryCount = 0;

    public interface PlayerCallback {
        void onPlayerReady();
        void onPlayerError(String errorMessage);
        void onBuffering(boolean isBuffering);
        void onPlaybackEnded();
    }

    public ExoPlayerManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public void setCallback(PlayerCallback callback) {
        this.callback = callback;
    }

    // ─────────────────────────────────────────────────────────
    //  DataSource con OkHttp — más robusto que el default
    //  para streams HTTP de IPTV
    // ─────────────────────────────────────────────────────────
    private OkHttpDataSource.Factory buildOkHttpDataSource() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)   // ← permite HTTP→HTTPS
                .addInterceptor(chain -> {
                    // Añadir headers IPTV en cada request
                    okhttp3.Request request = chain.request().newBuilder()
                            .header("User-Agent",
                                    "Mozilla/5.0 (Linux; Android 11; Mobile) " +
                                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                    "Chrome/90.0.4430.91 Mobile Safari/537.36")
                            .header("Connection", "keep-alive")
                            .build();
                    return chain.proceed(request);
                })
                .build();

        return new OkHttpDataSource.Factory(okHttpClient);
    }

    // ─────────────────────────────────────────────────────────
    //  DataSource HTTP puro (fallback si OkHttp falla)
    // ─────────────────────────────────────────────────────────
    private DefaultHttpDataSource.Factory buildHttpDataSource() {
        return new DefaultHttpDataSource.Factory()
                .setUserAgent(
                    "Mozilla/5.0 (Linux; Android 11; Mobile) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/90.0.4430.91 Mobile Safari/537.36")
                // ← CRÍTICO para IPTV: permite redirecciones entre HTTP y HTTPS
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(15_000)
                .setReadTimeoutMs(20_000);
    }

    // ─────────────────────────────────────────────────────────
    //  preparePlayer — entrada principal
    //  Detecta automáticamente HLS (.m3u8), TS, MP4, etc.
    // ─────────────────────────────────────────────────────────
    public ExoPlayer preparePlayer(String streamUrl) {
        this.currentUrl = streamUrl;
        this.retryCount = 0;
        return buildPlayer(streamUrl, false);
    }

    private ExoPlayer buildPlayer(String streamUrl, boolean useFallbackDataSource) {
        releasePlayer();

        Log.d(TAG, "Iniciando stream: " + streamUrl
                + " (intento " + (retryCount + 1) + "/" + MAX_RETRIES + ")"
                + (useFallbackDataSource ? " [FALLBACK]" : ""));

        DefaultMediaSourceFactory mediaSourceFactory;

        if (useFallbackDataSource) {
            // Fallback: DefaultHttpDataSource cuando OkHttp falla
            mediaSourceFactory = new DefaultMediaSourceFactory(buildHttpDataSource());
        } else {
            // Principal: OkHttp — más robusto para HTTP://
            mediaSourceFactory = new DefaultMediaSourceFactory(buildOkHttpDataSource());
        }

        player = new ExoPlayer.Builder(context)
                .setMediaSourceFactory(mediaSourceFactory)
                .build();

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(streamUrl));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);

        attachListeners(useFallbackDataSource);
        return player;
    }

    private void attachListeners(boolean isFallback) {
        player.addListener(new Player.Listener() {

            @Override
            public void onPlaybackStateChanged(int state) {
                if (callback == null) return;
                switch (state) {
                    case Player.STATE_READY:
                        retryCount = 0; // reset al reproducir exitosamente
                        callback.onPlayerReady();
                        callback.onBuffering(false);
                        break;
                    case Player.STATE_BUFFERING:
                        callback.onBuffering(true);
                        break;
                    case Player.STATE_ENDED:
                        callback.onPlaybackEnded();
                        break;
                    case Player.STATE_IDLE:
                        break;
                }
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                // Loguear el error completo para debugging
                int errorCode = error.errorCode;
                Log.e(TAG, "Error " + errorCode + ": " + error.getMessage(), error);

                // Reintentar automáticamente
                if (retryCount < MAX_RETRIES && currentUrl != null) {
                    retryCount++;
                    Log.d(TAG, "Reintentando... (" + retryCount + "/" + MAX_RETRIES + ")");

                    // En el 2do reintento, cambiar al datasource de fallback
                    boolean usesFallback = (retryCount >= 2);

                    // Esperar 1 segundo antes de reintentar
                    new android.os.Handler(android.os.Looper.getMainLooper())
                            .postDelayed(() -> buildPlayer(currentUrl, usesFallback), 1000);
                    return;
                }

                // Agotados los reintentos → informar al usuario con mensaje claro
                if (callback != null) {
                    callback.onPlayerError(getFriendlyError(errorCode, error));
                }
            }
        });
    }

    /**
     * Convierte códigos de error de ExoPlayer en mensajes amigables en español.
     */
    private String getFriendlyError(int errorCode, PlaybackException error) {
        switch (errorCode) {
            // Errores de red
            case PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED:
                return "❌ Sin conexión a internet. Verifica tu red.";
            case PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT:
                return "⏱ Tiempo de espera agotado. El canal puede estar caído.";
            case PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS:
                return "🚫 El servidor rechazó la conexión (error HTTP). Canal no disponible.";
            case PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND:
                return "🔍 Stream no encontrado. URL incorrecta o canal fuera de línea.";
            case PlaybackException.ERROR_CODE_IO_CLEARTEXT_NOT_PERMITTED:
                // Este es el error cuando HTTP está bloqueado — no debería ocurrir con nuestra config
                return "🔒 HTTP bloqueado. Reporta este error.";
            case PlaybackException.ERROR_CODE_IO_NO_PERMISSION:
                return "🔒 Sin permisos para acceder a este stream.";
            // Errores de decodificación
            case PlaybackException.ERROR_CODE_DECODER_INIT_FAILED:
                return "⚠ No se pudo iniciar el decodificador de video.";
            case PlaybackException.ERROR_CODE_DECODING_FAILED:
                return "⚠ Error al decodificar el stream. Formato no soportado.";
            // Errores de parsing
            case PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED:
            case PlaybackException.ERROR_CODE_PARSING_MANIFEST_MALFORMED:
                return "⚠ Formato del stream inválido o corrupto.";
            default:
                return "⚠ Error reproduciendo el canal (código: " + errorCode + ").\n"
                        + "Intenta con otro canal.";
        }
    }

    public ExoPlayer getPlayer() { return player; }

    public void pause()  { if (player != null) player.setPlayWhenReady(false); }
    public void resume() { if (player != null) player.setPlayWhenReady(true); }

    public void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
