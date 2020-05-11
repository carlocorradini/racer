package com.unitn.disi.lpsmt.racer.api.service;

import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.GameSetting;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Game Setting API Service
 *
 * @author Carlo Corradini
 * @see GameSetting
 */
public interface GameSettingService {
    /**
     * Find all {@link GameSetting} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List} of {@link GameSetting} founds
     */
    @GET("auth/game_setting")
    Call<API.Response<List<GameSetting>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link GameSetting} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link GameSetting} founds
     */
    @GET("auth/game_setting")
    Call<API.Response<List<GameSetting>>> find();

    /**
     * Find the {@link GameSetting} that has the given id
     *
     * @param id The {@link GameSetting} id
     * @return An {@link API.Response} with the {@link GameSetting} found
     */
    @GET("auth/game_setting/{id}")
    Call<API.Response<GameSetting>> findById(@Path("id") Long id);
}
