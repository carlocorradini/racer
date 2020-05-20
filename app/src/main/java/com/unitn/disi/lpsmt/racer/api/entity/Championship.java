package com.unitn.disi.lpsmt.racer.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Championship mapping
 *
 * @author Carlo Corradini
 */
public class Championship {
    /**
     * Id of the Championship
     */
    @SerializedName("id")
    @Expose
    public Long id;

    /**
     * Name of the Championship
     */
    @SerializedName("name")
    @Expose
    public String name;

    /**
     * Logo image url of the Championship
     */
    @SerializedName("logo")
    @Expose
    public URI logo;

    /**
     * Forum url of the Championship
     */
    @SerializedName("forum")
    @Expose
    public URL forum;

    /**
     * List of Car ids that are allowed in the Championship
     */
    @SerializedName("cars")
    @Expose
    public List<Long> cars;

    /**
     * List of User ids that are subscribed to the Championship
     */
    @SerializedName("users")
    @Expose
    public List<UUID> users;

    /**
     * List of Team ids that are subscribed to the Championship
     */
    @SerializedName("teams")
    @Expose
    public List<Long> teams;

    /**
     * List of Circuit ids in calendar of the Championship
     */
    @SerializedName("circuits")
    @Expose
    public List<Long> circuits;

    /**
     * List of Game Setting ids of the Championship
     */
    @SerializedName("game_settings")
    @Expose
    public List<Long> game_settings;

    /**
     * List of Photo {@link URI uris} of the Championship
     */
    @SerializedName("photos")
    @Expose
    public List<URI> photos;

    /**
     * Creation {@link LocalDateTime} of the Championship
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the Championship
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct a Championship class
     *
     * @param id            Id of the Championship
     * @param name          Name of the Championship
     * @param logo          Logo image url of the Championship
     * @param forum         Forum url of the Championship
     * @param cars          List of Car ids that are allowed in the Championship
     * @param users         List of User ids that are subscribed to the Championship
     * @param teams         List of Team ids that are subscribed to the Championship
     * @param circuits      List of Circuit ids in calendar of the Championship
     * @param game_settings List of Game Setting ids of the Championship
     * @param photos        List of Photo {@link URI uris} of the Championship
     * @param createdAt     Creation {@link LocalDateTime} of the Championship
     * @param updatedAt     Update {@link LocalDateTime} of the Championship
     */
    public Championship(Long id, String name, URI logo, URL forum, List<Long> cars, List<UUID> users, List<Long> teams, List<Long> circuits, List<Long> game_settings, List<URI> photos, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.forum = forum;
        this.cars = cars;
        this.users = users;
        this.teams = teams;
        this.circuits = circuits;
        this.game_settings = game_settings;
        this.photos = photos;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty Championship class
     */
    public Championship() {
    }
}
