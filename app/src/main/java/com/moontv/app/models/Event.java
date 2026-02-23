package com.moontv.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class Event {

    @SerializedName("_id")
    private String id;

    @SerializedName("title")
    private String title;           // "River vs Tigre"

    @SerializedName("competition")
    private String competition;     // "Liga Profesional Argentina"

    @SerializedName("countryCode")
    private String countryCode;     // "AR"

    @SerializedName("sport")
    private String sport;           // "football"

    @SerializedName("teamHome")
    private String teamHome;

    @SerializedName("teamAway")
    private String teamAway;

    @SerializedName("logoHome")
    private String logoHome;

    @SerializedName("logoAway")
    private String logoAway;

    @SerializedName("datetime")
    private String datetime;        // ISO string "2025-02-21T20:00:00Z"

    @SerializedName("status")
    private String status;          // "upcoming" | "live" | "finished"

    @SerializedName("channels")
    private List<EventChannel> channels;

    @SerializedName("isActive")
    private boolean isActive;

    public Event() {}

    // Getters
    public String getId()          { return id; }
    public String getTitle()       { return title; }
    public String getCompetition() { return competition; }
    public String getCountryCode() { return countryCode; }
    public String getSport()       { return sport; }
    public String getTeamHome()    { return teamHome; }
    public String getTeamAway()    { return teamAway; }
    public String getLogoHome()    { return logoHome; }
    public String getLogoAway()    { return logoAway; }
    public String getDatetime()    { return datetime; }
    public String getStatus()      { return status; }
    public List<EventChannel> getChannels() { return channels; }
    public boolean isActive()      { return isActive; }
    public boolean isLive()        { return "live".equals(status); }

    /** Convierte countryCode a emoji de bandera */
    public String getFlagEmoji() {
        if (countryCode == null || countryCode.length() != 2) return "🌍";
        int firstLetter  = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6;
        int secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6;
        return new String(Character.toChars(firstLetter))
             + new String(Character.toChars(secondLetter));
    }

    // ── Canal de transmisión del evento ──────────────────────────
    public static class EventChannel {

        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("streamUrl")
        private String streamUrl;

        @SerializedName("logo")
        private String logo;

        public String getId()        { return id; }
        public String getName()      { return name; }
        public String getStreamUrl() { return streamUrl; }
        public String getLogo()      { return logo; }
    }
}
