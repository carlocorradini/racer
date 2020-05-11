package com.unitn.disi.lpsmt.racer.api.service;

import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.ChampionshipCircuit;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;
import com.unitn.disi.lpsmt.racer.api.entity.Circuit;

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
 * Championship Circuit API Service
 *
 * @author Carlo Corradini
 * @see ChampionshipCircuit
 */
public interface ChampionshipCircuitService {
    /**
     * Find all {@link ChampionshipCircuit} available that correspond to the given options
     *
     * @param options Find options
     * @return An {@link API.Response} with the {@link List } of {@link ChampionshipCircuit} founds
     * @see API.Response
     */
    @GET("auth/championship_circuit")
    Call<API.Response<List<ChampionshipCircuit>>> find(@QueryMap Map<String, String> options);

    /**
     * Find all {@link ChampionshipCircuit} available
     *
     * @return An {@link API.Response} with the {@link List} of {@link ChampionshipCircuit} founds
     */
    @GET("auth/championship_circuit")
    Call<API.Response<List<ChampionshipCircuit>>> find();

    /**
     * Find the {@link ChampionshipCircuit} that has the given {@link Championship} {@link Long id} and {@link Circuit} {@link Long id}
     *
     * @param championship The {@link Championship} {@link Long id}
     * @param circuit      The {@link Circuit} {@link Long id}
     * @return An {@link API.Response} with the {@link ChampionshipCircuit} found
     */
    @GET("auth/championship_circuit/{championship}/{circuit}")
    Call<API.Response<ChampionshipCircuit>> findById(@Path("championship") Long championship, @Path("circuit") Long circuit);

    /**
     * Find all {@link ChampionshipCircuit} that correspond to the given {@link Championship} {@link Long id}
     *
     * @param championship The {@link Championship} {@link Long id}
     * @return An {@link API.Response} with the {@link List} of {@link ChampionshipCircuit} founds for the corresponding {@link Championship} {@link Long ID}
     */
    @GET("auth/championship_circuit/{championship}")
    Call<API.Response<List<ChampionshipCircuit>>> findByChampionship(@Path("championship") Long championship);

    /**
     * Create a new {@link ChampionshipCircuit} with the given championshipCircuit
     *
     * @param championshipCircuit The {@link ChampionshipCircuit} to create
     * @return An {@link API.Response} with the newly created {@link ChampionshipCircuit} {@link Circuit} {@link Long id}
     */
    @POST("auth/championship_circuit")
    Call<API.Response<Long>> create(@Body ChampionshipCircuit championshipCircuit);

    /**
     * Update the {@link ChampionshipCircuit} identified by {@link Championship} {@link Long id} and {@link Circuit} {@link Long id} with the given championshipCircuit
     *
     * @param championship        The {@link Championship} {@link Long id}
     * @param circuit             The {@link Circuit} {@link Long id}
     * @param championshipCircuit The {@link ChampionshipCircuit} to update with
     * @return An {@link API.Response} if the update operation succeeded
     */
    @PATCH("auth/championship_circuit/{championship}/{circuit}")
    Call<API.Response> update(@Path("championship") Long championship, @Path("circuit") Long circuit, @Body ChampionshipCircuit championshipCircuit);

    /**
     * Delete the {@link ChampionshipCircuit} identified by {@link Championship} {@link Long id} and {@link Circuit} {@link Long id}
     *
     * @param championship The {@link Championship} {@link Long id}
     * @param circuit      The {@link Circuit} {@link Long id}
     * @return An {@link API.Response} if the delete operation succeeded
     */
    @DELETE("auth/championship_circuit/{championship}/{circuit}")
    Call<API.Response> delete(@Path("championship") Long championship, @Path("circuit") Long circuit);
}
