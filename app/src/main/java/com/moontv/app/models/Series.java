package com.moontv.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Series {

    @SerializedName("_id")
    private String id;

    @SerializedName("title")
    private String title;

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

    @SerializedName("description")
    private String description;

    @SerializedName("rating")
    private double rating;

    @SerializedName("episodes")
    private int episodes;

    @SerializedName("seasons")
    private int seasons;

    @SerializedName("isActive")
    private boolean isActive;

    public Series() {}

    public String getId()          { return id; }
    public String getTitle()       { return title; }
    public String getPoster()      { return poster != null ? poster : logo; }
    public String getCategory()    { return category != null ? category : genre; }
    public String getDescription() { return description; }
    public int getYear()           { return year; }
    public double getRating()      { return rating; }
    public int getEpisodes()       { return episodes; }
    public int getSeasons()        { return seasons; }
    public boolean isActive()      { return isActive; }
}
