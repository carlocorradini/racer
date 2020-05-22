package com.unitn.disi.lpsmt.racer.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.AuthManager;
import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.api.service.UserService;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;
import com.unitn.disi.lpsmt.racer.ui.dialog.ChampionshipAdminGameSettingsDialog;
import com.unitn.disi.lpsmt.racer.util.InputUtil;

import org.jetbrains.annotations.NotNull;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Administrator panel {@link Fragment}
 *
 * @author Carlo Corradini
 */
public final class AdminPanelFragment extends Fragment {

    /**
     * Sing In {@link View container}
     */
    private View signInContainer;

    /**
     * Allowed Action {@link View container}
     */
    private View actionContainer;

    @NotNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_admin_panel, container, false);

        final User user = new User();
        final ChampionshipAdminGameSettingsDialog gameSettingsDialog = new ChampionshipAdminGameSettingsDialog();

        signInContainer = root.findViewById(R.id.fragment_admin_panel_sign_in_container);
        actionContainer = root.findViewById(R.id.fragment_admin_panel_action_container);
        EditText inputUsername = root.findViewById(R.id.fragment_admin_panel_sign_in_input_username);
        EditText inputPassword = root.findViewById(R.id.fragment_admin_panel_sign_in_input_password);
        Button buttonSignIn = root.findViewById(R.id.fragment_admin_panel_sign_in_button_sign_in);
        Button buttonActionGameSettings = root.findViewById(R.id.fragment_admin_panel_button_game_settings);
        Button buttonActionCircuits = root.findViewById(R.id.fragment_admin_panel_button_circuits);
        Button buttonActionUsers = root.findViewById(R.id.fragment_admin_panel_button_users);

        buttonSignIn.setOnClickListener(v -> {
            user.username = inputUsername.getText().toString();
            user.password = inputPassword.getText().toString();

            if (isValidUser(user)) {
                InputUtil.hideKeyboard(getActivity());
                doSignIn(user);
            }
        });

        buttonActionGameSettings.setOnClickListener(v -> {
            if (gameSettingsDialog.isAdded()) return;
            gameSettingsDialog.show(getParentFragmentManager(), ChampionshipAdminGameSettingsDialog.class.getName());
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (AuthManager.getInstance().isAdmin() && AuthManager.getInstance().getAdminToken() == null)
            AuthManager.getInstance().setAdminToken(AuthManager.getInstance().getToken());

        if (AuthManager.getInstance().isAdmin() || AuthManager.getInstance().getAdminToken() != null) {
            signInContainer.setVisibility(View.GONE);
            actionContainer.setVisibility(View.VISIBLE);
        } else {
            signInContainer.setVisibility(View.VISIBLE);
            actionContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Perform the Sign In call
     *
     * @param user The {@link User} to authenticate
     * @see UserService#signIn(User)
     */
    private void doSignIn(final User user) {
        if (user == null) return;

        API.getInstance().getClient().create(UserService.class).signIn(user).enqueue(new Callback<API.Response<JWT>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<JWT>> call, @NotNull Response<API.Response<JWT>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (AuthManager.getUserRole(response.body().data) == User.Role.ADMIN) {
                        AuthManager.getInstance().setAdminToken(response.body().data);
                    } else {
                        Toasty.warning(requireContext(), R.string.warning_no_admin_role).show();
                    }
                } else {
                    Toasty.error(requireContext(), R.string.sign_in_unauthorized).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<JWT>> call, @NotNull Throwable t) {
                ErrorHelper.showFailureError(requireContext(), t);
            }
        });
    }

    /**
     * Validate the given user and show warning message if not
     *
     * @param user The {@link User} to validate
     * @return True if user is valid, false otherwise
     */
    private boolean isValidUser(final User user) {
        if (user.username.isEmpty()) {
            Toasty.warning(requireContext(), R.string.warning_empty_username).show();
            return false;
        }
        if (user.password.isEmpty()) {
            Toasty.warning(requireContext(), R.string.warning_empty_password).show();
            return false;
        }
        if (user.password.length() < User.PASSWORD_MIN_LENGTH) {
            Toasty.warning(requireContext(), R.string.warning_password_min_length).show();
            return false;
        }

        return true;
    }
}
