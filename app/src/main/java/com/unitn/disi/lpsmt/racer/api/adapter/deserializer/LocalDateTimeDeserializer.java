package com.unitn.disi.lpsmt.racer.api.adapter.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * {@link JsonElement Json} deserializer for {@link LocalDateTime}
 *
 * @author Carlo Corradini
 * @see LocalDateTime
 */
public final class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
    /**
     * Given a {@link JsonElement Json} element deserialize it to {@link LocalDateTime}
     *
     * @param json    The Json data being deserialized
     * @param typeOfT The {@link LocalDateTime} type to deserialize to
     * @param context Method to create objects for any non-trivial field of the returned object
     * @return A deserialized {@link LocalDateTime} object
     * @throws JsonParseException If @param json is not in the expected format of a {@link LocalDateTime} object
     */
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString()).toLocalDateTime();
    }
}
