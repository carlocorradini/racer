package com.unitn.disi.lpsmt.racer.api.service;

import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;
import com.unitn.disi.lpsmt.racer.api.entity.Team;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Team API Service
 *
 * @author Carlo Corradini
 * @see Team
 */
public interface TeamService {
    /**
     * Find all {@link Team} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List} of {@link Team} founds
     */
    @GET("auth/team")
    Call<API.Response<List<Team>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link Team} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link Team} founds
     */
    @GET("auth/team")
    Call<API.Response<List<Team>>> find();

    /**
     * Find the {@link Team} that has the given id
     *
     * @param id The {@link Team} id
     * @return An {@link API.Response} with the {@link Team} found
     */
    @GET("auth/team/{id}")
    Call<API.Response<Team>> findById(@Path("id") Long id);

    /**
     * Find all {@link Team} that correspond to the given {@link Championship} {@link Long id}
     *
     * @param championship The {@link Championship} {@link Long id}
     * @return An {@link API.Response} with the {@link List} of {@link Team} founds for the corresponding {@link Championship} {@link Long ID}
     */
    @GET("auth/team/championship/{championship}")
    Call<API.Response<List<Team>>> findByChampionship(@Path("championship") Long championship);
}
