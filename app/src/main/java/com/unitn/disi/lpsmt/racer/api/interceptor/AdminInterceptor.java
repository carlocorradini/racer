package com.unitn.disi.lpsmt.racer.api.interceptor;

import android.util.Log;

import com.auth0.android.jwt.JWT;

import org.jetbrains.annotations.NotNull;

import com.unitn.disi.lpsmt.racer.api.AuthManager;

import java.io.IOException;

import com.unitn.disi.lpsmt.racer.api.entity.User;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Authentication Admin Interceptor class
 * Intercept a http request and inject the {@link JWT} if the {@link User} is authenticated and an admin using the {@link AuthManager}
 *
 * @author Carlo Corradini
 * @see Interceptor
 * @see AuthManager
 */
public final class AdminInterceptor implements Interceptor {

    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = AuthInterceptor.class.getName();

    /**
     * Name of the Authorization http header key
     */
    private static final String AUTHORIZATION = "Authorization";

    /**
     * Intercept a http request and inject the {@link JWT} if the {@link User} is authenticated and an admin using the {@link AuthManager}
     *
     * @param chain Request chain
     * @return The Chain Response
     * @throws IOException If the modification fails
     * @see JWT
     * @see User
     * @see AuthManager
     */
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        JWT token = AuthManager.getInstance().getAdminToken();

        if (token != null) {
            request = chain.request().newBuilder().addHeader(AUTHORIZATION, toBearerToken(token)).build();
            Log.d(TAG, "Sending request with auth admin token");
        }

        return chain.proceed(request);
    }

    /**
     * Transform a {@link JWT} into a Bearer authorization string
     *
     * @param token The {@link JWT} to transform
     * @return The Bearer string with the {@link JWT} attached
     */
    private static String toBearerToken(JWT token) {
        if (token == null) return null;

        return String.format("Bearer %s", token.toString());
    }
}
