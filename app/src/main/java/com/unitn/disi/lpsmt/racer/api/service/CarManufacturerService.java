package com.unitn.disi.lpsmt.racer.api.service;

import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.CarManufacturer;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Car Manufacturer API Service
 *
 * @author Carlo Corradini
 * @see CarManufacturer
 */
public interface CarManufacturerService {
    /**
     * Find all {@link CarManufacturer} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List} of {@link CarManufacturer} founds
     */
    @GET("car_manufacturer")
    Call<API.Response<List<CarManufacturer>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link CarManufacturer} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link CarManufacturer} founds
     */
    @GET("car_manufacturer")
    Call<API.Response<List<CarManufacturer>>> find();

    /**
     * Find the {@link CarManufacturer} that has the given id
     *
     * @param id The {@link CarManufacturer} id
     * @return An {@link API.Response} with the {@link CarManufacturer} found
     */
    @GET("car_manufacturer/{id}")
    Call<API.Response<CarManufacturer>> findById(@Path("id") Long id);
}
