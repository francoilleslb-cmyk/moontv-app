package com.moontv.app.network;

import android.content.Context;

import com.moontv.app.utils.AppPreferences;
import com.moontv.app.utils.SessionManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    private static RetrofitClient instance;
    private ApiService  apiService;
    private AuthService authService;
    private String currentBaseUrl;
    private Context appContext;

    private RetrofitClient(Context context, String baseUrl) {
        this.appContext     = context.getApplicationContext();
        this.currentBaseUrl = baseUrl;
        buildClient(baseUrl);
    }

    private void buildClient(String baseUrl) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    okhttp3.Request.Builder builder = chain.request().newBuilder()
                            .header("User-Agent", "MoonTV/1.0 (Android)")
                            .header("Accept", "application/json");

                    // Agregar JWT automáticamente si hay sesión activa
                    if (appContext != null) {
                        String bearer = SessionManager.getInstance(appContext).getBearerToken();
                        if (bearer != null) {
                            builder.header("Authorization", bearer);
                        }
                    }
                    return chain.proceed(builder.build());
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService  = retrofit.create(ApiService.class);
        authService = retrofit.create(AuthService.class);
    }

    public static synchronized RetrofitClient getInstance(Context context) {
        String savedUrl = AppPreferences.getInstance(context).getBackendUrl();
        if (instance == null || !savedUrl.equals(instance.currentBaseUrl)) {
            instance = new RetrofitClient(context, savedUrl);
        }
        return instance;
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Llama primero a getInstance(context)");
        }
        return instance;
    }

    public ApiService  getApiService()  { return apiService;  }
    public AuthService getAuthService() { return authService; }

    public static void reset() { instance = null; }
}
