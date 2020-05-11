package com.unitn.disi.lpsmt.racer.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

/**
 * Game Setting mapping
 *
 * @author Carlo Corradini
 */
public class GameSetting {
    /**
     * Id of the Game Setting
     */
    @SerializedName("id")
    @Expose
    public Long id;

    /**
     * Name of the Game Setting
     */
    @SerializedName("name")
    @Expose
    public String name;

    /**
     * Creation {@link LocalDateTime} of the Game Setting
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the Game Setting
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct a Game Setting class
     *
     * @param id        Id of the Game Setting
     * @param name      Name of the Game Setting
     * @param createdAt Creation {@link LocalDateTime} of the Game Setting
     * @param updatedAt Update {@link LocalDateTime} of the Game Setting
     */
    public GameSetting(Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty Game Setting class
     */
    public GameSetting() {
    }
}
