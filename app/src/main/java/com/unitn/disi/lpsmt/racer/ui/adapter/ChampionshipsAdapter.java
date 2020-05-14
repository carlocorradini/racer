package com.unitn.disi.lpsmt.racer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;

import java.util.ArrayList;

import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * {@link Championship} {@link ArrayAdapter Adapter}
 *
 * @author Carlo Corradini
 */
public class ChampionshipsAdapter extends ArrayAdapter<Championship> {
    /**
     * Construct a {@link ChampionshipsAdapter} with the given context and championships
     *
     * @param context       The current {@link Context}
     * @param championships The {@link List} of {@link Championship} to represent
     */
    public ChampionshipsAdapter(Context context, ArrayList<Championship> championships) {
        super(context, 0, championships);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Championship championship = getItem(position);
        assert championship != null;
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_championship, parent, false);

        ImageView imageLogo = convertView.findViewById(R.id.item_championship_logo);
        TextView txtName = convertView.findViewById(R.id.item_championship_name);

        Picasso.get().load(championship.logo.toString()).into(imageLogo);
        txtName.setText(championship.name);

        convertView.setOnClickListener(v -> {
            Toasty.success(getContext(), "hiii").show();
        });

        return convertView;
    }
}
