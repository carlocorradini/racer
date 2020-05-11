package com.unitn.disi.lpsmt.racer.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

/**
 * Team mapping
 *
 * @author Carlo Corradini
 */
public class Team {
    /**
     * Id of the Team
     */
    @SerializedName("id")
    @Expose
    public Long id;

    /**
     * Name of the Team
     */
    @SerializedName("name")
    @Expose
    public String name;

    /**
     * Creation {@link LocalDateTime} of the Team
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the Team
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct a Team class
     *
     * @param id        Id of the Team
     * @param name      Name of the Team
     * @param createdAt Creation {@link LocalDateTime} of the Team
     * @param updatedAt Update {@link LocalDateTime} of the Team
     */
    public Team(Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty Team class
     */
    public Team() {
    }
}
