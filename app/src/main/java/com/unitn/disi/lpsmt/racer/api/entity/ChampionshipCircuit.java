package com.unitn.disi.lpsmt.racer.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Championship Circuit mapping
 *
 * @author Carlo Corradini
 */
public class ChampionshipCircuit {
    /**
     * Championship id of the Championship Circuit
     */
    @SerializedName("championship")
    @Expose
    public Long championship;

    /**
     * Circuit id of the Championship Circuit
     */
    @SerializedName("circuit")
    @Expose
    public Long circuit;

    /**
     * Date of the Championship Circuit
     */
    @SerializedName("date")
    @Expose
    public LocalDate date;

    /**
     * Creation {@link LocalDateTime} of the Championship Circuit
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the Championship Circuit
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct a Championship Circuit class
     *
     * @param championship Championship id of the Championship Circuit
     * @param circuit      Circuit id of the Championship Circuit
     * @param date         Date of the Championship Circuit
     * @param createdAt    Creation {@link LocalDateTime} of the Championship Circuit
     * @param updatedAt    Update {@link LocalDateTime} of the Championship Circuit
     */
    public ChampionshipCircuit(Long championship, Long circuit, LocalDate date, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.championship = championship;
        this.circuit = circuit;
        this.date = date;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty Championship Circuit class
     */
    public ChampionshipCircuit() {
    }
}
