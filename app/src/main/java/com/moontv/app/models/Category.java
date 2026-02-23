package com.moontv.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Category {

    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("icon")
    private String icon;

    @SerializedName("channels")
    private List<Channel> channels;

    @SerializedName("channelCount")
    private int channelCount;

    public Category() {}

    public String getId() { return id; }
    public String getName() { return name; }
    public String getIcon() { return icon; }
    public List<Channel> getChannels() { return channels; }
    public int getChannelCount() { return channelCount; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setChannels(List<Channel> channels) { this.channels = channels; }
    public void setChannelCount(int channelCount) { this.channelCount = channelCount; }
}
