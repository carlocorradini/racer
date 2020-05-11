package com.unitn.disi.lpsmt.racer.api.service;

import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;
import com.unitn.disi.lpsmt.racer.api.entity.UserChampionship;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * User Championship API Service
 *
 * @author Carlo Corradini
 * @see UserChampionship
 */
public interface UserChampionshipService {
    /**
     * Find all {@link UserChampionship} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List } of {@link UserChampionship} founds
     * @see API.Response
     */
    @GET("auth/user_championship")
    Call<API.Response<List<UserChampionship>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link UserChampionship} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link UserChampionship} founds
     */
    @GET("auth/user_championship")
    Call<API.Response<List<UserChampionship>>> find();

    /**
     * Find the {@link UserChampionship} that has the given {@link Championship} {@link Long id} and {@link User} {@link UUID id}
     *
     * @param championship The {@link Championship} {@link Long id}
     * @param user         The {@link User} {@link UUID id}
     * @return An {@link API.Response} with the {@link UserChampionship} found
     */
    @GET("auth/user_championship/{championship}/{user}")
    Call<API.Response<UserChampionship>> findById(@Path("championship") Long championship, @Path("user") UUID user);

    /**
     * Find all {@link UserChampionship} that correspond to the given {@link Championship} {@link Long id}
     *
     * @param championship The {@link Championship} {@link Long id}
     * @return An {@link API.Response} with the {@link List} of {@link UserChampionship} founds for the corresponding {@link Championship} {@link Long ID}
     */
    @GET("auth/user_championship/{championship}")
    Call<API.Response<List<UserChampionship>>> findByChampionship(@Path("championship") Long championship);

    /**
     * Create a new {@link UserChampionship} with the given userChampionship
     *
     * @param userChampionship The {@link UserChampionship} to create
     * @return An {@link API.Response} with the newly created {@link UserChampionship} {@link Championship} {@link Long id}
     */
    @POST("auth/user_championship")
    Call<API.Response<Long>> create(@Body UserChampionship userChampionship);

    /**
     * Update the {@link UserChampionship} identified by {@link Championship} {@link Long id} with the given userChampionship
     *
     * @param championship     The {@link Championship} {@link Long id}
     * @param userChampionship The {@link UserChampionship} to update with
     * @return An {@link API.Response} if the update operation succeeded
     */
    @PATCH("auth/user_championship/{championship}")
    Call<API.Response> update(@Path("championship") Long championship, @Body UserChampionship userChampionship);

    /**
     * Delete the {@link UserChampionship} identified by {@link Championship} {@link Long id}
     *
     * @param championship The {@link Championship} {@link Long id}
     * @return An {@link API.Response} if the delete operation succeeded
     */
    @DELETE("auth/user_championship/{championship}")
    Call<API.Response> delete(@Path("championship") Long championship);
}
