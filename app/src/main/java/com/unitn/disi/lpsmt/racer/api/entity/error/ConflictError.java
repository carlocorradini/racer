package com.unitn.disi.lpsmt.racer.api.entity.error;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.http.HttpStatus;

/**
 * Conflict error mapping for {@link HttpStatus#SC_CONFLICT CONFLICT} http status code
 *
 * @author Carlo Corradini
 * @see HttpStatus#SC_CONFLICT
 */
public class ConflictError {
    /**
     * Name of the property that caused the error
     */
    @SerializedName("property")
    @Expose
    public String property;

    /**
     * Value of the property that caused the error
     */
    @SerializedName("value")
    @Expose
    public String value;

    /**
     * Construct a Conflict error class
     *
     * @param property Name of the property that caused the error
     * @param value    Value of the property that caused the error
     */
    public ConflictError(String property, String value) {
        this.property = property;
        this.value = value;
    }

    /**
     * Construct an empty Conflict error class
     */
    public ConflictError() {
    }
}
