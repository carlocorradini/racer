package com.unitn.disi.lpsmt.racer.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.time.LocalDateTime;

/**
 * Championship Photo mapping
 *
 * @author Carlo Corradini
 */
public class ChampionshipPhoto {
    /**
     * Championship id of the Championship Photo
     */
    @SerializedName("championship")
    @Expose
    public Long championship;

    /**
     * Championship photo of the Championship Photo
     */
    @SerializedName("photo")
    @Expose
    public URI photo;

    /**
     * Creation {@link LocalDateTime} of the Championship Photo
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the Championship Photo
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct a Championship Photo class
     *
     * @param championship Championship id of the Championship Photo
     * @param photo        Championship photo of the Championship Photo
     * @param createdAt    Creation {@link LocalDateTime} of the Championship Photo
     * @param updatedAt    Update {@link LocalDateTime} of the Championship Photo
     */
    public ChampionshipPhoto(Long championship, URI photo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.championship = championship;
        this.photo = photo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty Championship Photo class
     */
    public ChampionshipPhoto() {
    }
}
