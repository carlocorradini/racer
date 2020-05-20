package com.unitn.disi.lpsmt.racer.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.chip.Chip;
import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;

import java.util.List;

/**
 * {@link Championship} {@link ArrayAdapter Adapter}
 *
 * @author Carlo Corradini
 */
public final class ChampionshipsAdapter extends ArrayAdapter<Championship> {

    /**
     * Flag to check if the item contain photos
     */
    private boolean withPhoto = false;

    /**
     * Construct a {@link ChampionshipsAdapter} with the given context and championships
     *
     * @param context       The current {@link Context}
     * @param championships The {@link List} of {@link Championship} to represent
     */
    public ChampionshipsAdapter(Context context, List<Championship> championships) {
        super(context, 0, championships);
    }

    /**
     * Set the variable flag with photo
     *
     * @param withPhoto The value to set the value to
     */
    public void setWithPhoto(boolean withPhoto) {
        this.withPhoto = withPhoto;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Championship championship = getItem(position);
        assert championship != null;
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_championship, parent, false);

        convertView.setTag(championship.id);
        ImageView imageLogo = convertView.findViewById(R.id.item_championship_logo);
        TextView txtName = convertView.findViewById(R.id.item_championship_name);
        Chip txtPhotos = convertView.findViewById(R.id.item_championship_photos);
        Chip txtUsers = convertView.findViewById(R.id.item_championship_users);
        Chip txtCircuits = convertView.findViewById(R.id.item_championship_circuits);
        Chip txtCars = convertView.findViewById(R.id.item_championship_cars);
        Chip txtTeams = convertView.findViewById(R.id.item_championship_teams);
        Chip txtSettings = convertView.findViewById(R.id.item_championship_settings);
        Chip buttonForum = convertView.findViewById(R.id.item_championship_forum);

        Picasso.get().load(championship.logo.toString()).into(imageLogo);
        txtName.setText(championship.name);
        txtPhotos.setText(String.valueOf(championship.photos.size()));
        txtUsers.setText(String.valueOf(championship.users.size()));
        txtCircuits.setText(String.valueOf(championship.circuits.size()));
        txtCars.setText(String.valueOf(championship.cars.size()));
        txtTeams.setText(String.valueOf(championship.teams.size()));
        txtSettings.setText(String.valueOf(championship.game_settings.size()));
        buttonForum.setOnClickListener(v -> getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(championship.forum.toString()))));

        if (withPhoto) {
            txtPhotos.setVisibility(View.VISIBLE);
            buttonForum.setVisibility(View.GONE);
        } else {
            txtPhotos.setVisibility(View.GONE);
            buttonForum.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
