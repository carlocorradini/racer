package com.unitn.disi.lpsmt.racer.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;

import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.service.ChampionshipService;
import com.unitn.disi.lpsmt.racer.helper.ColorHelper;
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
 * Championships Photos {@link Fragment}
 *
 * @author Carlo Corradini
 */
public final class ChampionshipsPhotosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = ChampionshipsPhotosFragment.class.getName();

    /**
     * {@link ChampionshipsAdapter} adapter reference
     */
    private ChampionshipsAdapter championshipsAdapter = null;

    /**
     * {@link SwipeRefreshLayout} for reloading the {@link Championship} in the championshipsAdapter
     */
    private SwipeRefreshLayout swipeRefreshLayout = null;

    @NotNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_championships_photos, container, false);

        championshipsAdapter = new ChampionshipsAdapter(getContext(), new ArrayList<>());
        championshipsAdapter.setWithPhoto(true);
        ListView listView = root.findViewById(R.id.fragment_championships_photos_list_container);
        swipeRefreshLayout = root.findViewById(R.id.fragment_championships_photos_swipe_refresh);

        listView.setAdapter(championshipsAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            final Bundle bundle = new Bundle();
            bundle.putLong("id", (long) view.getTag());
            Navigation.findNavController(root).navigate(R.id.nav_championships_action_to_nav_championship_photos, bundle);
        });

        swipeRefreshLayout.setColorSchemeColors(ColorHelper.COLOR_BLUE, ColorHelper.COLOR_RED, ColorHelper.COLOR_YELLOW, ColorHelper.COLOR_GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        loadChampionships();

        return root;
    }

    /**
     * Load all available {@link Championship} from the server
     * and display them in the championshipsAdapter
     */
    private void loadChampionships() {
        if (championshipsAdapter == null) return;
        championshipsAdapter.clear();

        API.getInstance().getClient().create(ChampionshipService.class).find().enqueue(new Callback<API.Response<List<Championship>>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<List<Championship>>> call, @NotNull Response<API.Response<List<Championship>>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "Successfully loaded Championships");
                    championshipsAdapter.addAll(response.body().data);
                } else {
                    Log.w(TAG, "Unable to load Championship due to failure response");
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<List<Championship>>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to load Championships due to " + t.getMessage(), t);
                swipeRefreshLayout.setRefreshing(false);
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    @Override
    public void onRefresh() {
        loadChampionships();
    }
}
