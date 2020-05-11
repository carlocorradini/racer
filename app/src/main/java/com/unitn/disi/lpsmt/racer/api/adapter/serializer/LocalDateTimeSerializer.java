package com.unitn.disi.lpsmt.racer.api.adapter.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * {@link JsonElement Json} serializer for {@link LocalDateTime}
 *
 * @author Carlo Corradini
 * @see LocalDateTime
 */
public final class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
    /**
     * Given a {@link LocalDateTime} object serialize it to {@link JsonElement Json}
     *
     * @param src       The {@link LocalDateTime} that needs to be converted to {@link JsonElement Json}
     * @param typeOfSrc The {@link LocalDateTime} (fully genericized version) type of the source object
     * @param context   Method to create JsonElements for any non-trivial field of the {@code src} object
     * @return A serialized {@link JsonElement Json} corresponding to the {@link LocalDateTime} object
     */
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT));
    }
}
