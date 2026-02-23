package com.moontv.app.models;

import com.google.gson.annotations.SerializedName;

public class AuthRequest {

    @SerializedName("name")
    private String name;         // Solo para registro

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    // Constructor para LOGIN
    public AuthRequest(String email, String password) {
        this.email    = email;
        this.password = password;
    }

    // Constructor para REGISTRO
    public AuthRequest(String name, String email, String password) {
        this.name     = name;
        this.email    = email;
        this.password = password;
    }

    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
}
