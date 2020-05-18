package com.unitn.disi.lpsmt.racer.api.service;

import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.Car;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Car API Service
 *
 * @author Carlo Corradini
 * @see Car
 */
public interface CarService {
    /**
     * Find all {@link Car} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List} of {@link Car} founds
     */
    @GET("car")
    Call<API.Response<List<Car>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link Car} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link Car} founds
     */
    @GET("car")
    Call<API.Response<List<Car>>> find();

    /**
     * Find the {@link Car} that has the given id
     *
     * @param id The {@link Car} id
     * @return An {@link API.Response} with the {@link Car} found
     */
    @GET("car/{id}")
    Call<API.Response<Car>> findById(@Path("id") Long id);

    /**
     * Find all {@link Car} that correspond to the given {@link Championship} {@link Long id}
     *
     * @param championship The {@link Championship} {@link Long id}
     * @return An {@link API.Response} with the {@link List} of {@link Car} founds for the corresponding {@link Championship} {@link Long ID}
     */
    @GET("car/championship/{championship}")
    Call<API.Response<List<Car>>> findByChampionship(@Path("championship") Long championship);
}
