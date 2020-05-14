package com.unitn.disi.lpsmt.racer.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;
import com.unitn.disi.lpsmt.racer.api.service.ChampionshipService;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;
import com.unitn.disi.lpsmt.racer.ui.adapter.ChampionshipsAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * {@link Championship Championships} {@link Fragment}
 *
 * @author Carlo Corradini
 */
public final class ChampionshipsFragment extends Fragment {

    /**
     * {@link ChampionshipsAdapter} adapter reference
     */
    private ChampionshipsAdapter championshipsAdapter = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_championships, container, false);

        championshipsAdapter = new ChampionshipsAdapter(getContext(), new ArrayList<>());
        ListView listView = root.findViewById(R.id.fragment_championships_championship_container);
        listView.setAdapter(championshipsAdapter);

        loadChampionships();

        return root;
    }

    /**
     * Load all available {@link Championship} from the server
     * and display them in the championshipsAdapter
     */
    private void loadChampionships() {
        if (championshipsAdapter == null) return;

        API.getInstance().getClient().create(ChampionshipService.class).find().enqueue(new Callback<API.Response<List<Championship>>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<List<Championship>>> call, @NotNull Response<API.Response<List<Championship>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    championshipsAdapter.addAll(response.body().data);
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<List<Championship>>> call, @NotNull Throwable t) {
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

}
