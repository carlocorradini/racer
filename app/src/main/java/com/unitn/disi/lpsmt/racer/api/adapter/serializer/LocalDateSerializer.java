package com.unitn.disi.lpsmt.racer.api.adapter.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * {@link JsonElement Json} serializer for {@link LocalDate}
 *
 * @author Carlo Corradini
 * @see LocalDate
 */
public final class LocalDateSerializer implements JsonSerializer<LocalDate> {
    /**
     * Given a {@link LocalDate} object serialize it to {@link JsonElement Json}
     *
     * @param src       The {@link LocalDate} that needs to be converted to {@link JsonElement Json}
     * @param typeOfSrc The {@link LocalDate} (fully genericized version) type of the source object
     * @param context   Method to create JsonElements for any non-trivial field of the {@code src} object
     * @return A serialized {@link JsonElement Json} corresponding to the {@link LocalDate} object
     */
    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.format(DateTimeFormatter.ISO_DATE));
    }
}
