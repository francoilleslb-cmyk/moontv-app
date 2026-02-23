package com.moontv.app.network;

import com.moontv.app.models.ApiResponse;
import com.moontv.app.models.Channel;
import com.moontv.app.models.Event;
import com.moontv.app.models.Movie;
import com.moontv.app.models.Series;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ─── CANALES ─────────────────────────────────────────────────────────────

    @GET("api/channels")
    Call<ApiResponse<List<Channel>>> getAllChannels();

    @GET("api/channels")
    Call<ApiResponse<List<Channel>>> getChannelsByCategory(
            @Query("category") String category);

    @GET("api/channels")
    Call<ApiResponse<List<Channel>>> getChannelsPaged(
            @Query("page") int page,
            @Query("limit") int limit);

    @GET("api/channels/categories")
    Call<ApiResponse<List<String>>> getCategories();

    @GET("api/channels/search")
    Call<ApiResponse<List<Channel>>> searchChannels(@Query("q") String query);

    @GET("api/channels/{id}")
    Call<ApiResponse<Channel>> getChannelById(@Path("id") String channelId);

    @POST("api/channels/{id}/view")
    Call<ApiResponse<Void>> registerView(@Path("id") String channelId);

    // ─── PELÍCULAS ────────────────────────────────────────────────────────────

    @GET("api/movies")
    Call<ApiResponse<List<Movie>>> getAllMovies();

    @GET("api/movies")
    Call<ApiResponse<List<Movie>>> getMoviesPaged(
            @Query("page") int page,
            @Query("limit") int limit);

    @GET("api/movies/search")
    Call<ApiResponse<List<Movie>>> searchMovies(@Query("q") String query);

    // ─── SERIES ───────────────────────────────────────────────────────────────

    @GET("api/series")
    Call<ApiResponse<List<Series>>> getAllSeries();

    @GET("api/series")
    Call<ApiResponse<List<Series>>> getSeriesPaged(
            @Query("page") int page,
            @Query("limit") int limit);

    @GET("api/series/search")
    Call<ApiResponse<List<Series>>> searchSeries(@Query("q") String query);

    // ─── EVENTOS ─────────────────────────────────────────────────────────────

    @GET("api/events/today")
    Call<ApiResponse<List<Event>>> getTodayEvents();

    @GET("api/events")
    Call<ApiResponse<List<Event>>> getUpcomingEvents(@Query("days") int days);
}
