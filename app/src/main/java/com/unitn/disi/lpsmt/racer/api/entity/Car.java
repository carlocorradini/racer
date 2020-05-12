package com.unitn.disi.lpsmt.racer.api.entity;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

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
     * Manufacturer of the Car
     */
    @SerializedName("manufacturer")
    @Expose
    public CarManufacturer manufacturer;

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
     * @param manufacturer Manufacturer of the Car
     * @param createdAt    Creation {@link LocalDateTime} of the Car
     * @param updatedAt    Update {@link LocalDateTime} of the Car
     */
    public Car(Long id, String name, CarManufacturer manufacturer, LocalDateTime createdAt, LocalDateTime updatedAt) {
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Car car = (Car) obj;
        return Objects.equals(id, car.id);
    }

    /**
     * Return the full car name having the {@link CarManufacturer manufacturer} name and {@link Car} name
     *
     * @return The {@link Car} full name having the {@link CarManufacturer manufacturer} name as first
     */
    public String getFullName() {
        return String.format("%s %s", StringUtils.capitalize(this.manufacturer.name), StringUtils.capitalize(this.name));
    }
}
