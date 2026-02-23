package com.moontv.app.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("token")
    private String token;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("isPremium")
    private boolean isPremium;

    public User() {}

    public String getId()       { return id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getToken()    { return token; }
    public String getAvatar()   { return avatar; }
    public boolean isPremium()  { return isPremium; }

    public void setId(String id)         { this.id = id; }
    public void setName(String name)     { this.name = name; }
    public void setEmail(String email)   { this.email = email; }
    public void setToken(String token)   { this.token = token; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public void setPremium(boolean p)    { this.isPremium = p; }
}
