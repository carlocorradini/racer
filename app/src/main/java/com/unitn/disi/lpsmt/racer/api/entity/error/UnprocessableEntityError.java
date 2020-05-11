package com.unitn.disi.lpsmt.racer.api.entity.error;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.http.HttpStatus;

import java.util.Map;

/**
 * Unprocessable entity error mapping for {@link HttpStatus#SC_UNPROCESSABLE_ENTITY} http status code
 *
 * @author Carlo Corradini
 * @see HttpStatus#SC_UNPROCESSABLE_ENTITY
 */
public class UnprocessableEntityError {
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
     * Constraints that failed validation with error messages
     */
    @SerializedName("constraints")
    @Expose
    public Map<String, String> constraints;

    /**
     * Construct an Unprocessable Entity error class
     *
     * @param property    Name of the property that caused the error
     * @param value       Value of the property that caused the error
     * @param constraints Constraints that failed validation with error messages
     */
    public UnprocessableEntityError(String property, String value, Map<String, String> constraints) {
        this.property = property;
        this.value = value;
        this.constraints = constraints;
    }

    /**
     * Construct an empty Unprocessable Entity error class
     */
    public UnprocessableEntityError() {
    }
}
