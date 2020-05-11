package com.unitn.disi.lpsmt.racer.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Car Manufacturer mapping
 *
 * @author Carlo Corradini
 */
public class CarManufacturer {
    /**
     * Id of the Car Manufacturer
     */
    @SerializedName("id")
    @Expose
    public Long id;

    /**
     * Name of the Car Manufacturer
     */
    @SerializedName("name")
    @Expose
    public String name;

    /**
     * List of Car ids that the Car Manufacturer produced
     */
    @SerializedName("cars")
    @Expose
    public List<Long> cars;

    /**
     * Creation {@link LocalDateTime} of the Car Manufacturer
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the Car Manufacturer
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct a Car Manufacturer class
     *
     * @param id        Id of the Car Manufacturer
     * @param name      Name of the Car Manufacturer
     * @param cars      List of Car ids that the Car Manufacturer produced
     * @param createdAt Creation {@link LocalDateTime} of the Car Manufacturer
     * @param updatedAt Update {@link LocalDateTime} of the Car Manufacturer
     */
    public CarManufacturer(Long id, String name, List<Long> cars, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.cars = cars;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty Car Manufacturer class
     */
    public CarManufacturer() {
    }
}
