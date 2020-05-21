package com.unitn.disi.lpsmt.racer.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.picasso.Picasso;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;
import com.unitn.disi.lpsmt.racer.api.entity.ChampionshipPhoto;
import com.unitn.disi.lpsmt.racer.api.service.ChampionshipPhotoService;
import com.unitn.disi.lpsmt.racer.helper.ColorHelper;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;
import com.unitn.disi.lpsmt.racer.ui.adapter.ChampionshipPhotosAdapter;
import com.unitn.disi.lpsmt.racer.util.ImageUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Championship Photo {@link Fragment}
 *
 * @author Carlo Corradini
 */
public final class ChampionshipPhotosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = ChampionshipPhotosFragment.class.getName();

    /**
     * Invalid {@link Championship} identifier
     */
    private static final long INVALID_CHAMPIONSHIP_ID = 0;

    /**
     * Current {@link Championship} {@link Long id} to show
     */
    private long idChampionship = INVALID_CHAMPIONSHIP_ID;

    /**
     * {@link ChampionshipPhotosAdapter} adapter reference
     */
    private ChampionshipPhotosAdapter championshipPhotosAdapter = null;

    /**
     * {@link List} of {@link ChampionshipPhoto}
     */
    private List<ChampionshipPhoto> photos = new ArrayList<>();

    /**
     * {@link SwipeRefreshLayout} for reloading the {@link Championship} in the championshipsAdapter
     */
    private SwipeRefreshLayout swipeRefreshLayout = null;

    /**
     * {@link GridView Container} used to display the {@link ChampionshipPhoto Photos} using the championshipPhotosAdapter
     */
    private GridView championshipPhotosContainer = null;

    /**
     * {@link View Container} used to display no photos message
     */
    private View noPhotosContainer = null;

    @NotNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_championship_photos, container, false);

        championshipPhotosAdapter = new ChampionshipPhotosAdapter(getContext(), new ArrayList<>());
        championshipPhotosContainer = root.findViewById(R.id.fragment_championship_photos_grid_container);
        swipeRefreshLayout = root.findViewById(R.id.fragment_championship_photos_swipe_refresh);
        noPhotosContainer = root.findViewById(R.id.fragment_championship_photos_no_photo);

        championshipPhotosContainer.setAdapter(championshipPhotosAdapter);
        championshipPhotosContainer.setOnItemClickListener((parent, view, position, id) -> {
            if (!(view instanceof ImageView)) return;
            final ImageView gridImageItem = (ImageView) view;

            final StfalconImageViewer imageViewer = new StfalconImageViewer.Builder<>(requireContext(), photos, (imageView, championshipPhoto) -> {
                Picasso.get().load(championshipPhoto.photo.toString()).into(imageView);
            })
                    .withTransitionFrom(gridImageItem)
                    .build();

            imageViewer.setCurrentPosition(position);
            imageViewer.show();
        });

        championshipPhotosContainer.setOnItemLongClickListener((parent, view, position, id) -> {
            Uri imageUri;
            if (!(view instanceof ImageView)) return false;
            if ((imageUri = ImageUtil.toLocalBitmapUri(requireContext(), view)) == null)
                return false;

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_photo)));

            return true;
        });

        swipeRefreshLayout.setColorSchemeColors(ColorHelper.COLOR_BLUE, ColorHelper.COLOR_RED, ColorHelper.COLOR_YELLOW, ColorHelper.COLOR_GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        idChampionship = getArguments() != null ? getArguments().getLong("id", INVALID_CHAMPIONSHIP_ID) : INVALID_CHAMPIONSHIP_ID;

        loadChampionshipPhotos(idChampionship);
    }

    /**
     * Load all available {@link ChampionshipPhoto} of the given idChampionship from the server
     * and display them in the championshipPhotosAdapter
     *
     * @param id The {@link Championship} id
     */
    private void loadChampionshipPhotos(long id) {
        if (id == INVALID_CHAMPIONSHIP_ID) {
            Toasty.error(requireContext(), R.string.championship_invalid).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (championshipPhotosAdapter == null) return;
        championshipPhotosAdapter.clear();

        API.getInstance().getClient().create(ChampionshipPhotoService.class).findByChampionship(idChampionship).enqueue(new Callback<API.Response<List<ChampionshipPhoto>>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<List<ChampionshipPhoto>>> call, @NotNull Response<API.Response<List<ChampionshipPhoto>>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "Successfully loaded Championship Photos");
                    championshipPhotosAdapter.addAll(response.body().data);
                    photos = response.body().data;

                    if (photos.isEmpty()) {
                        noPhotosContainer.setVisibility(View.VISIBLE);
                        championshipPhotosContainer.setVisibility(View.GONE);
                    } else {
                        noPhotosContainer.setVisibility(View.GONE);
                        championshipPhotosContainer.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.w(TAG, "Unable to load Championship Photos due to failure response");
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<List<ChampionshipPhoto>>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to load Championship Photos due to " + t.getMessage(), t);
                swipeRefreshLayout.setRefreshing(false);
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    @Override
    public void onRefresh() {
        loadChampionshipPhotos(idChampionship);
    }
}
