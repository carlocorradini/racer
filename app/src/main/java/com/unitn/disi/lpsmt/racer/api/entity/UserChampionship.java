package com.unitn.disi.lpsmt.racer.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User Championship mapping
 *
 * @author Carlo Corradini
 */
public class UserChampionship {
    /**
     * User id of the User Championship
     */
    @SerializedName("user")
    @Expose
    public UUID user;

    /**
     * Championship id of the User Championship
     */
    @SerializedName("championship")
    @Expose
    public Long championship;

    /**
     * Car if of the User Championship
     */
    @SerializedName("car")
    @Expose
    public Long car;

    /**
     * Team id of the User Championship
     */
    @SerializedName("team")
    @Expose
    public Long team;

    /**
     * Points of the User Championship
     */
    @SerializedName("points")
    @Expose
    public Short points;

    /**
     * Creation {@link LocalDateTime} of the User Championship
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the User Championship
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct a User Championship class
     *
     * @param user         User id of the User Championship
     * @param championship Championship id of the User Championship
     * @param car          Car if of the User Championship
     * @param team         Team id of the User Championship
     * @param points       Points of the User Championship
     * @param createdAt    Creation {@link LocalDateTime} of the User Championship
     * @param updatedAt    Update {@link LocalDateTime} of the User Championship
     */
    public UserChampionship(UUID user, Long championship, Long car, Long team, Short points, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.user = user;
        this.championship = championship;
        this.car = car;
        this.team = team;
        this.points = points;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty User Championship class
     */
    public UserChampionship() {
    }
}
