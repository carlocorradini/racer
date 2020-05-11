package com.unitn.disi.lpsmt.racer.api.service;

import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Championship API Service
 *
 * @author Carlo Corradini
 * @see Championship
 */
public interface ChampionshipService {
    /**
     * Find all {@link Championship} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List} of {@link Championship} founds
     */
    @GET("auth/championship")
    Call<API.Response<List<Championship>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link Championship} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link Championship} founds
     */
    @GET("auth/championship")
    Call<API.Response<List<Championship>>> find();

    /**
     * Find the {@link Championship} that has the given id
     *
     * @param id The {@link Championship} id
     * @return An {@link API.Response} with the {@link Championship} found
     */
    @GET("auth/championship/{id}")
    Call<API.Response<Championship>> findById(@Path("id") Long id);
}
