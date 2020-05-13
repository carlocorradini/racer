package com.unitn.disi.lpsmt.racer.api.entity;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Circuit mapping
 *
 * @author Carlo Corradini
 */
public class Circuit {
    /**
     * Id of the Circuit
     */
    @SerializedName("id")
    @Expose
    public Long id;

    /**
     * Name of the Circuit
     */
    @SerializedName("name")
    @Expose
    public String name;

    /**
     * Creation {@link LocalDateTime} of the Circuit
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the Circuit
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct a Circuit class
     *
     * @param id        Id of the Circuit
     * @param name      Name of the Circuit
     * @param createdAt Creation {@link LocalDateTime} of the Circuit
     * @param updatedAt Update {@link LocalDateTime} of the Circuit
     */
    public Circuit(Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty Circuit class
     */
    public Circuit() {
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Circuit circuit = (Circuit) obj;
        return Objects.equals(id, circuit.id);
    }
}
