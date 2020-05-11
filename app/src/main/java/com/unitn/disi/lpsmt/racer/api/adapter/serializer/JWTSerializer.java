package com.unitn.disi.lpsmt.racer.api.adapter.serializer;

import com.auth0.android.jwt.JWT;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * {@link JsonElement Json} serializer for {@link JWT}
 *
 * @author Carlo Corradini
 * @see JWT
 */
public final class JWTSerializer implements JsonSerializer<JWT> {
    /**
     * Given a {@link JWT} object serialize it to {@link JsonElement Json}
     *
     * @param src       The {@link JWT} that needs to be converted to {@link JsonElement Json}
     * @param typeOfSrc The {@link JWT} (fully genericized version) type of the source object
     * @param context   Method to create JsonElements for any non-trivial field of the {@code src} object
     * @return A serialized {@link JsonElement Json} corresponding to the {@link JWT} object
     */
    @Override
    public JsonElement serialize(JWT src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
