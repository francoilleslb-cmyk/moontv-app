package com.moontv.app.models;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public UpdateProfileRequest(String name, String email) {
        this.name  = name;
        this.email = email;
    }

    public UpdateProfileRequest(String name, String email, String password) {
        this.name     = name;
        this.email    = email;
        this.password = password;
    }

    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
}
