package com.unitn.disi.lpsmt.racer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.entity.ChampionshipPhoto;

import java.util.List;

/**
 * {@link ChampionshipPhoto} {@link ArrayAdapter Adapter}
 *
 * @author Carlo Corradini
 */
public final class ChampionshipPhotosAdapter extends ArrayAdapter<ChampionshipPhoto> {

    /**
     * Construct a {@link ChampionshipPhotosAdapter} with the given context and championships
     *
     * @param context       The current {@link Context}
     * @param championshipPhotos The {@link List} of {@link ChampionshipPhoto} to represent
     */
    public ChampionshipPhotosAdapter(Context context, List<ChampionshipPhoto> championshipPhotos) {
        super(context, 0, championshipPhotos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ChampionshipPhoto championshipPhoto = getItem(position);
        assert championshipPhoto != null;
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_championship_photo, parent, false);

        ImageView imageLogo = convertView.findViewById(R.id.item_championship_photo_logo);

        Picasso.get().load(championshipPhoto.photo.toString()).into(imageLogo);

        return convertView;
    }
}
