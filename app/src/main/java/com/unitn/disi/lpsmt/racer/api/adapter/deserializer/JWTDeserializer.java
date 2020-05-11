package com.unitn.disi.lpsmt.racer.api.adapter.deserializer;

import com.auth0.android.jwt.JWT;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * {@link JsonElement Json} deserializer for {@link JWT}
 *
 * @author Carlo Corradini
 * @see JWT
 */
public final class JWTDeserializer implements JsonDeserializer<JWT> {

    /**
     * Given a {@link JsonElement Json} element deserialize it to {@link JWT}
     *
     * @param json    The Json data being deserialized
     * @param typeOfT The {@link JWT} type to deserialize to
     * @param context Method to create objects for any non-trivial field of the returned object
     * @return A deserialized {@link JWT} object
     * @throws JsonParseException If @param json is not in the expected format of a {@link JWT} object
     */
    @Override
    public JWT deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new JWT(json.getAsJsonPrimitive().getAsString());
    }
}
