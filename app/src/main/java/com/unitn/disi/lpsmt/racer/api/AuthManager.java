package com.unitn.disi.lpsmt.racer.api;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.auth0.android.jwt.DecodeException;
import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.racer.App;
import com.unitn.disi.lpsmt.racer.api.entity.User;

import java.util.UUID;

/**
 * Authentication Manager class
 *
 * @author Carlo Corradini
 */
public final class AuthManager {

    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = AuthManager.class.getName();

    /**
     * Key name of {@link SharedPreferences} field where to save the token
     */
    private static final String AUTH_TOKEN = "auth_token";

    /**
     * Key name of {@link JWT} claim for {@link User} {@link UUID id}
     */
    private static final String CLAIM_USER_ID = "id";

    /**
     * Instance of the current {@link AuthManager} class assigned when the first {@link AuthManager#getInstance()} is called
     */
    private static AuthManager instance = null;

    /**
     * {@link SharedPreferences} instance used to retrieve the token
     */
    private final SharedPreferences sharedPreferences;

    /**
     * Construct an Authentication Manager class.
     * Authentication Manager is constructed only once when the first {@link AuthManager#getInstance()} is called
     */
    private AuthManager() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());

        Log.i(TAG, "Initialized");
    }

    /**
     * Return the current {@link AuthManager} class instance.
     * The instance is constructed when this is the first call
     *
     * @return The {@link AuthManager} instance
     */
    public static AuthManager getInstance() {
        if (instance == null) {
            synchronized (AuthManager.class) {
                if (instance == null) {
                    instance = new AuthManager();
                }
            }
        }
        return instance;
    }

    /**
     * Return the {@link JWT token} saved from {@link SharedPreferences}.
     * If no token found, null is returned
     *
     * @return The {@link JWT token} saved in {@link SharedPreferences}, null otherwise
     * @throws DecodeException If the token cannot be decoded
     */
    public JWT getToken() throws DecodeException {
        JWT token = null;
        String tokenString = sharedPreferences.getString(AUTH_TOKEN, null);

        if (tokenString != null) {
            try {
                token = new JWT(tokenString);
            } catch (DecodeException ex) {
                Log.w(TAG, "Error retrieving token due to " + ex.getMessage());
            }
        }

        return token;
    }

    /**
     * Save the given {@link JWT token} into {@link SharedPreferences}
     *
     * @param token The {@link JWT token} to save
     */
    public void setToken(JWT token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN, token.toString());
        editor.apply();

        Log.i(TAG, "Token set");
    }

    /**
     * Remove the {@link JWT token} from {@link SharedPreferences}
     */
    public void clearToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(AUTH_TOKEN);
        editor.apply();

        Log.i(TAG, "Token cleared");
    }

    /**
     * Return the {@link User} {@link UUID id} decoded from the {@link JWT token} if it's present
     *
     * @return {@link User} {@link UUID id} if present, null otherwise
     */
    public UUID getAuthUserId() {
        UUID id = null;
        try {
            id = UUID.fromString(getToken().getClaim(CLAIM_USER_ID).asString());
        } catch (IllegalArgumentException ignored) {
        }

        return id;
    }

    /**
     * Check if the current {@link User} is authenticated
     *
     * @return True if {@link User} is authenticated, false otherwise
     * @see AuthManager#getToken()
     */
    public boolean isAuth() {
        return getToken() != null;
    }

    /**
     * Check if the current authenticated {@link User} {@link UUID ID} decoded from {@link JWT token} is the same as the given id
     *
     * @param id The {@link User} {@link UUID id} to compare with
     * @return True if is the same {@link User} {@link UUID id}, false otherwise
     */
    public boolean isCurrentAuthUser(UUID id) {
        return isAuth() && id != null && id.equals(getAuthUserId());
    }
}
