package com.unitn.disi.lpsmt.racer.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

/**
 * Championship Game Setting mapping
 *
 * @author Carlo Corradini
 */
public class ChampionshipGameSetting {
    /**
     * Championship id of the Championship Game Setting
     */
    @SerializedName("championship")
    @Expose
    public Long championship;

    /**
     * Game Setting id of the Championship Game Setting
     */
    @SerializedName("game_setting")
    @Expose
    public Long gameSetting;

    /**
     * Value of the Championship Game Setting
     */
    @SerializedName("value")
    @Expose
    public String value;

    /**
     * Creation {@link LocalDateTime} of the Championship Game Setting
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the Championship Game Setting
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct a Championship Game Setting class
     *
     * @param championship Championship id of the Championship Game Setting
     * @param gameSetting  Game Setting id of the Championship Game Setting
     * @param value        Value of the Championship Game Setting
     * @param createdAt    Creation {@link LocalDateTime} of the Championship Game Setting
     * @param updatedAt    Update {@link LocalDateTime} of the Championship Game Setting
     */
    public ChampionshipGameSetting(Long championship, Long gameSetting, String value, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.championship = championship;
        this.gameSetting = gameSetting;
        this.value = value;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty Championship Game Setting class
     */
    public ChampionshipGameSetting() {
    }
}
