package com.moontv.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Modelo Channel del backend Moon TV.
 * Campos mapeados del modelo Mongoose del backend real.
 *
 * Backend devuelve:
 *   status: "active" | "inactive" | "testing"
 *   streamUrl: URL principal del stream
 *   servers: [{ url, label, type, isWorking }]
 *   isFeatured: boolean
 *   currentProgram: string
 *   description: string
 */
public class Channel {

    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    // URL principal — el modelo del backend usa "streamUrl"
    @SerializedName("streamUrl")
    private String streamUrl;

    // Compatibilidad si algún registro antiguo tiene "url"
    @SerializedName("url")
    private String url;

    @SerializedName("logo")
    private String logo;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("category")
    private String category;

    // "group" = campo que viene de imports M3U
    @SerializedName("group")
    private String group;

    @SerializedName("country")
    private String country;

    @SerializedName("language")
    private String language;

    @SerializedName("description")
    private String description;

    @SerializedName("currentProgram")
    private String currentProgram;

    // ── CRÍTICO: el backend devuelve "status": "active" / "inactive"
    //   NO un boolean "isActive". Bug corregido aquí.
    @SerializedName("status")
    private String status;

    @SerializedName("isFeatured")
    private boolean isFeatured;

    @SerializedName("isPaid")
    private boolean isPaid;

    @SerializedName("isAdult")
    private boolean isAdult;

    @SerializedName("viewCount")
    private int viewCount;

    @SerializedName("sortOrder")
    private int sortOrder;

    @SerializedName("tags")
    private List<String> tags;

    // Servidores fallback del canal
    @SerializedName("servers")
    private List<Server> servers;

    public Channel() {}

    // ─── Getters ─────────────────────────────────────────────────────────────

    public String getId()            { return id; }
    public String getName()          { return name; }
    public String getLogo()          { return logo; }
    public String getThumbnail()     { return thumbnail; }
    public String getGroup()         { return group; }
    public String getCountry()       { return country; }
    public String getLanguage()      { return language; }
    public String getDescription()   { return description; }
    public String getCurrentProgram(){ return currentProgram; }
    public String getStatus()        { return status; }
    public boolean isFeatured()      { return isFeatured; }
    public boolean isPaid()          { return isPaid; }
    public boolean isAdult()         { return isAdult; }
    public int getViewCount()        { return viewCount; }
    public int getSortOrder()        { return sortOrder; }
    public List<String> getTags()    { return tags; }
    public List<Server> getServers() { return servers; }

    /**
     * Devuelve la categoría del canal.
     * Prioriza "category" del backend; cae en "group" si viene de importación M3U.
     */
    public String getCategory() {
        if (category != null && !category.isEmpty()) return category;
        return group;
    }

    /**
     * ✅ CORREGIDO: isActive usa "status" == "active" (string del backend).
     *    Antes el campo era @SerializedName("isActive") boolean — no coincidía.
     */
    public boolean isActive() {
        return "active".equals(status);
    }

    /**
     * Devuelve la mejor URL de stream disponible.
     * Prioridad: streamUrl > url > primer server disponible.
     */
    public String getStreamUrl() {
        if (streamUrl != null && !streamUrl.isEmpty()) return streamUrl;
        if (url != null && !url.isEmpty()) return url;
        // Intentar con el primer servidor disponible
        if (servers != null) {
            for (Server s : servers) {
                if (s != null && s.getUrl() != null && !s.getUrl().isEmpty()
                        && s.isWorking()) {
                    return s.getUrl();
                }
            }
            // Si ninguno figura como working, devolver el primero igual
            if (!servers.isEmpty() && servers.get(0) != null) {
                return servers.get(0).getUrl();
            }
        }
        return null;
    }

    // ─── Setters ─────────────────────────────────────────────────────────────

    public void setId(String id)                 { this.id = id; }
    public void setName(String name)             { this.name = name; }
    public void setStreamUrl(String streamUrl)   { this.streamUrl = streamUrl; }
    public void setUrl(String url)               { this.url = url; }
    public void setLogo(String logo)             { this.logo = logo; }
    public void setCategory(String category)     { this.category = category; }
    public void setGroup(String group)           { this.group = group; }
    public void setCountry(String country)       { this.country = country; }
    public void setStatus(String status)         { this.status = status; }
    public void setFeatured(boolean featured)    { isFeatured = featured; }

    // ─── Clase interna Server ────────────────────────────────────────────────

    public static class Server {

        @SerializedName("url")
        private String url;

        @SerializedName("label")
        private String label;

        @SerializedName("type")
        private String type;  // "hls" | "mp4" | "rtmp" | "dash"

        @SerializedName("isWorking")
        private boolean isWorking = true;

        public String getUrl()     { return url; }
        public String getLabel()   { return label != null ? label : "HD"; }
        public String getType()    { return type != null ? type : "hls"; }
        public boolean isWorking() { return isWorking; }
    }
}
