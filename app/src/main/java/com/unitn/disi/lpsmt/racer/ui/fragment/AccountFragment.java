package com.unitn.disi.lpsmt.racer.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.unitn.disi.lpsmt.racer.R;

import org.jetbrains.annotations.NotNull;

import com.unitn.disi.lpsmt.racer.api.entity.User;

/**
 * {@link User} account {@link Fragment}
 *
 * @author Carlo Corradini
 */
public final class AccountFragment extends Fragment {
    @NotNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        return root;
    }
}
