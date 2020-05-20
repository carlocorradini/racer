package com.unitn.disi.lpsmt.racer.api.service;

import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;
import com.unitn.disi.lpsmt.racer.api.entity.ChampionshipPhoto;

import java.net.URI;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Championship Photo API Service
 *
 * @author Carlo Corradini
 * @see ChampionshipPhotoService
 */
public interface ChampionshipPhotoService {
    /**
     * Find all {@link ChampionshipPhoto} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List} of {@link ChampionshipPhoto} founds
     */
    @GET("auth/championship_photo")
    Call<API.Response<List<ChampionshipPhoto>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link ChampionshipPhoto} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link ChampionshipPhoto} founds
     */
    @GET("auth/championship_photo")
    Call<API.Response<List<ChampionshipPhoto>>> find();

    /**
     * Find the {@link ChampionshipPhoto} that has the given championship and photo
     *
     * @param championship The {@link Championship} {@link Long id}
     * @param photo        The {@link URI photo}
     * @return An {@link API.Response} with the {@link ChampionshipPhoto} found
     */
    @GET("auth/championship_photo/{championship}/{photo}")
    Call<API.Response<ChampionshipPhoto>> findById(@Path("championship") Long championship, @Path("photo") URI photo);

    /**
     * Find all {@link ChampionshipPhoto} that correspond to the given {@link Championship} {@link Long id}
     *
     * @param championship The {@link Championship} {@link Long id}
     * @return An {@link API.Response} with the {@link List} of {@link ChampionshipPhoto} founds for the corresponding {@link Championship} {@link Long id}
     */
    @GET("auth/championship_photo/{championship}")
    Call<API.Response<List<ChampionshipPhoto>>> findByChampionship(@Path("championship") Long championship);
}
