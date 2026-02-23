package com.moontv.app.models;

import com.google.gson.annotations.SerializedName;

/**
 * Wrapper genérico para respuestas del backend Moon TV.
 *
 * Formato esperado del backend Express:
 * { "success": true, "data": {...}, "count": 50, "message": "..." }
 *
 * En caso de error:
 * { "success": false, "message": "Error description" }
 */
public class ApiResponse<T> {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private T data;

    @SerializedName("message")
    private String message;

    @SerializedName("count")
    private int count;

    @SerializedName("total")
    private int total;

    @SerializedName("token")
    private String token;   // Algunos endpoints devuelven el token en raíz

    public boolean isSuccess() { return success; }
    public T getData()         { return data; }
    public String getMessage() { return message; }
    public int getCount()      { return count; }
    public int getTotal()      { return total; }
    public String getToken()   { return token; }
}
