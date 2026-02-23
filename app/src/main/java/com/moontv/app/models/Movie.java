package com.moontv.app.models;

import com.google.gson.annotations.SerializedName;

public class Movie {

    @SerializedName("_id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("url")
    private String url;

    @SerializedName("streamUrl")
    private String streamUrl;

    @SerializedName("poster")
    private String poster;

    @SerializedName("logo")
    private String logo;

    @SerializedName("category")
    private String category;

    @SerializedName("genre")
    private String genre;

    @SerializedName("year")
    private int year;

    @SerializedName("duration")
    private String duration;

    @SerializedName("description")
    private String description;

    @SerializedName("rating")
    private double rating;

    @SerializedName("isActive")
    private boolean isActive;

    public Movie() {}

    public String getId()          { return id; }
    public String getTitle()       { return title; }
    public String getPoster()      { return poster != null ? poster : logo; }
    public String getCategory()    { return category != null ? category : genre; }
    public String getGenre()       { return genre; }
    public int getYear()           { return year; }
    public String getDuration()    { return duration; }
    public String getDescription() { return description; }
    public double getRating()      { return rating; }
    public boolean isActive()      { return isActive; }

    public String getStreamUrl() {
        if (streamUrl != null && !streamUrl.isEmpty()) return streamUrl;
        return url;
    }
}
