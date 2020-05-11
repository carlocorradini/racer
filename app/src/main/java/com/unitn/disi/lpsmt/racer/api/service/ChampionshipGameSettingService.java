package com.unitn.disi.lpsmt.racer.api.service;

import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.ChampionshipGameSetting;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;
import com.unitn.disi.lpsmt.racer.api.entity.GameSetting;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Championship Game API Service
 *
 * @author Carlo Corradini
 * @see ChampionshipGameSetting
 */
public interface ChampionshipGameSettingService {
    /**
     * Find all {@link ChampionshipGameSetting} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List } of {@link ChampionshipGameSetting} founds
     * @see API.Response
     */
    @GET("auth/championship_game_setting")
    Call<API.Response<List<ChampionshipGameSetting>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link ChampionshipGameSetting} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link ChampionshipGameSetting} founds
     */
    @GET("auth/championship_game_setting")
    Call<API.Response<List<ChampionshipGameSetting>>> find();

    /**
     * Find the {@link ChampionshipGameSetting} that has the given {@link Championship} {@link Long id} and {@link GameSetting} {@link Long id}
     *
     * @param championship The {@link Championship} {@link Long id}
     * @param gameSetting  The {@link GameSetting} {@link Long id}
     * @return An {@link API.Response} with the {@link ChampionshipGameSetting} found
     */
    @GET("auth/championship_game_setting/{championship}/{game_setting}")
    Call<API.Response<ChampionshipGameSetting>> findById(@Path("championship") Long championship, @Path("game_setting") Long gameSetting);

    /**
     * Find all {@link ChampionshipGameSetting} that correspond to the given {@link Championship} {@link Long id}
     *
     * @param championship The {@link Championship} {@link Long id}
     * @return An {@link API.Response} with the {@link List} of {@link ChampionshipGameSetting} founds for the corresponding {@link Championship} {@link Long ID}
     */
    @GET("auth/championship_game_setting/{championship}")
    Call<API.Response<List<ChampionshipGameSetting>>> findByChampionship(@Path("championship") Long championship);

    /**
     * Create a new {@link ChampionshipGameSetting} with the given championshipGameSetting
     *
     * @param championshipGameSetting The {@link ChampionshipGameSetting} to create
     * @return An {@link API.Response} with the newly created {@link ChampionshipGameSetting} {@link GameSetting }{@link Long id}
     */
    @POST("auth/championship_game_setting")
    Call<API.Response<Long>> create(@Body ChampionshipGameSetting championshipGameSetting);

    /**
     * Update the {@link ChampionshipGameSetting} identified by {@link Championship} {@link Long id} and {@link GameSetting} {@link Long id} with the given championshipGameSetting
     *
     * @param championship            The {@link Championship} {@link Long id}
     * @param gameSetting             The {@link GameSetting} {@link Long id}
     * @param championshipGameSetting The {@link ChampionshipGameSetting} to update with
     * @return An {@link API.Response} if the update operation succeeded
     */
    @PATCH("auth/championship_game_setting/{championship}/{game_setting}")
    Call<API.Response> update(@Path("championship") Long championship, @Path("game_setting") Long gameSetting, @Body ChampionshipGameSetting championshipGameSetting);

    /**
     * Delete the {@link ChampionshipGameSetting} identified by {@link Championship} {@link Long id} and {@link GameSetting} {@link Long id}
     *
     * @param championship The {@link Championship} {@link Long id}
     * @param gameSetting  The {@link GameSetting} {@link Long id}
     * @return An {@link API.Response} if the delete operation succeeded
     */
    @DELETE("auth/championship_game_setting/{championship}/{game_setting}")
    Call<API.Response> delete(@Path("championship") Long championship, @Path("game_setting") Long gameSetting);
}