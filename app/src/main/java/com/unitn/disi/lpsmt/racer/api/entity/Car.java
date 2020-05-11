package com.unitn.disi.lpsmt.racer.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

/**
 * Car mapping
 *
 * @author Carlo Corradini
 */
public class Car {
    /**
     * Id of the Car
     */
    @SerializedName("id")
    @Expose
    public Long id;

    /**
     * Name of the Car
     */
    @SerializedName("name")
    @Expose
    public String name;

    /**
     * Manufacturer id of the Car
     */
    @SerializedName("manufacturer")
    @Expose
    public Long manufacturer;

    /**
     * Creation {@link LocalDateTime} of the Car
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the Car
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct a Car class
     *
     * @param id           Id of the Car
     * @param name         Name of the Car
     * @param manufacturer Manufacturer id of the Car
     * @param createdAt    Creation {@link LocalDateTime} of the Car
     * @param updatedAt    Update {@link LocalDateTime} of the Car
     */
    public Car(Long id, String name, Long manufacturer, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty Car class
     */
    public Car() {
    }
}
