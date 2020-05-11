package com.unitn.disi.lpsmt.racer.api.adapter.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * {@link JsonElement Json} deserializer for {@link LocalDate}
 *
 * @author Carlo Corradini
 * @see LocalDate
 */
public final class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
    /**
     * Given a {@link JsonElement Json} element deserialize it to {@link LocalDate}
     *
     * @param json    The Json data being deserialized
     * @param typeOfT The {@link LocalDate} type to deserialize to
     * @param context Method to create objects for any non-trivial field of the returned object
     * @return A deserialized {@link LocalDate} object
     * @throws JsonParseException If @param json is not in the expected format of a {@link LocalDate} object
     */
    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return LocalDate.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_DATE);
    }
}
