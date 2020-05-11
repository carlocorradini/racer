package com.unitn.disi.lpsmt.racer.api.service;

import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.racer.api.AuthManager;
import com.unitn.disi.lpsmt.racer.api.interceptor.AuthInterceptor;
import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.User;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * User API Service
 *
 * @author Carlo Corradini
 * @see User
 */
public interface UserService {
    /**
     * Find all {@link User} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List} of {@link User} founds
     * @see API.Response
     */
    @GET("auth/user")
    Call<API.Response<List<User>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link User} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link User} founds
     */
    @GET("auth/user")
    Call<API.Response<List<User>>> find();

    /**
     * Find the {@link User} that has the given {@link UUID id}
     *
     * @param id The {@link User} {@link UUID id}
     * @return An {@link API.Response} with the {@link User} found
     */
    @GET("auth/user/{id}")
    Call<API.Response<User>> findById(@Path("id") UUID id);

    /**
     * Return the current authenticated {@link User}
     *
     * @return An {@link API.Response} with the current authenticated {@link User}
     * @see AuthManager
     * @see AuthInterceptor
     */
    @GET("auth/user/me")
    Call<API.Response<User>> me();

    /**
     * Create a new {@link User} with the given user
     *
     * @param user The {@link User} to create
     * @return An {@link API.Response} with the newly created {@link User} {@link UUID id}
     */
    @POST("auth/user")
    Call<API.Response<UUID>> create(@Body User user);

    /**
     * Update the current authenticated {@link User} with the given user
     *
     * @param user The {@link User} to update with
     * @return An {@link API.Response} if the update operation succeeded
     * @see AuthManager
     */
    @PATCH("auth/user")
    Call<API.Response> update(@Body User user);

    /**
     * Update the current authenticated {@link User} avatar with the given avatar
     *
     * @param avatar The {@link MultipartBody.Part avatar} to update with
     * @return An {@link API.Response} if the update operation succeeded
     * @see MultipartBody.Part
     */
    @PATCH("auth/user/avatar")
    @Multipart
    Call<API.Response<URL>> updateAvatar(@Part MultipartBody.Part avatar);

    /**
     * Delete the current authenticated {@link User}
     *
     * @return An {@link API.Response} if the delete operation succeeded
     */
    @DELETE("auth/user")
    Call<API.Response> delete();

    /**
     * Perform a sign in authentication operation for the given user and
     * if the operation succeeded return the {@link JWT} for authentication
     *
     * @param user The {@link User} to authenticate
     * @return An {@link API.Response} with the {@link JWT} authentication token
     * @see AuthManager
     */
    @POST("auth/user/sign_in")
    Call<API.Response<JWT>> signIn(@Body User user);

    /**
     * Generate a new password request for the {@link User} that has the given {@link String email}
     *
     * @param email The {@link User} email
     * @return An {@link API.Response} if the password request operation succeeded
     */
    @POST("auth/user/password_reset/{email}")
    Call<API.Response> passwordReset(@Path("email") String email);
}
