package com.unitn.disi.lpsmt.racer.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.mateware.snacky.Snacky;
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

    @NotNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_championship_photos, container, false);

        championshipPhotosAdapter = new ChampionshipPhotosAdapter(getContext(), new ArrayList<>());
        GridView gridView = root.findViewById(R.id.fragment_championship_photos_grid_container);
        swipeRefreshLayout = root.findViewById(R.id.fragment_championship_photos_swipe_refresh);

        gridView.setAdapter(championshipPhotosAdapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
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

        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            Uri imageUri;
            if (!(view instanceof ImageView)) return false;
            if ((imageUri = getLocalBitmapUri((ImageView) view)) == null) return false;

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

    /**
     * Save the {@link Bitmap} image retrieved from imageView and return the {@link Uri}
     *
     * @param imageView The {@link ImageView}
     * @return Uri image
     */
    private Uri getLocalBitmapUri(ImageView imageView) {
        Uri bmpUri = null;

        if (imageView.getDrawable() instanceof BitmapDrawable) {
            Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            try {
                File file = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".png");
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                bmpUri = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bmpUri;
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
                        Snacky.builder().setView(swipeRefreshLayout).warning().setText(R.string.no_photos).show();
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
