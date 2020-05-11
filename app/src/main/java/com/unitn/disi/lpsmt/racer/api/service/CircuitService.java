package com.unitn.disi.lpsmt.racer.api.service;

import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.Circuit;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Circuit API Service
 *
 * @author Carlo Corradini
 * @see Circuit
 */
public interface CircuitService {
    /**
     * Find all {@link Circuit} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List} of {@link Circuit} founds
     */
    @GET("auth/circuit")
    Call<API.Response<List<Circuit>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link Circuit} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link Circuit} founds
     */
    @GET("auth/circuit")
    Call<API.Response<List<Circuit>>> find();

    /**
     * Find the {@link Circuit} that has the given id
     *
     * @param id The {@link Circuit} id
     * @return An {@link API.Response} with the {@link Circuit} found
     */
    @GET("auth/circuit/{id}")
    Call<API.Response<Circuit>> findById(@Path("id") Long id);
}
