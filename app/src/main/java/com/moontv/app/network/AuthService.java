package com.moontv.app.network;

import com.moontv.app.models.ApiResponse;
import com.moontv.app.models.AuthRequest;
import com.moontv.app.models.UpdateProfileRequest;
import com.moontv.app.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface AuthService {

    /** POST /api/auth/register */
    @POST("api/auth/register")
    Call<ApiResponse<User>> register(@Body AuthRequest request);

    /** POST /api/auth/login */
    @POST("api/auth/login")
    Call<ApiResponse<User>> login(@Body AuthRequest request);

    /** GET /api/auth/me — perfil del usuario autenticado (JWT en header automático) */
    @GET("api/auth/me")
    Call<ApiResponse<User>> getMe();

    /** PUT /api/auth/update-profile */
    @PUT("api/auth/update-profile")
    Call<ApiResponse<User>> updateProfile(@Body UpdateProfileRequest request);
}
