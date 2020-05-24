package com.unitn.disi.lpsmt.racer.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.ChampionshipCircuit;

import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;
import com.unitn.disi.lpsmt.racer.api.entity.UserChampionship;
import com.unitn.disi.lpsmt.racer.api.service.ChampionshipService;
import com.unitn.disi.lpsmt.racer.api.service.UserChampionshipService;
import com.unitn.disi.lpsmt.racer.api.service.UserService;
import com.unitn.disi.lpsmt.racer.filter.NumberMinMaxFilter;
import com.unitn.disi.lpsmt.racer.helper.ColorHelper;
import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;
import com.unitn.disi.lpsmt.racer.ui.component.dialog.OnDialogSelectionInterface;
import com.unitn.disi.lpsmt.racer.util.InputUtil;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * {@link ChampionshipCircuit} & {@link User} {@link DialogFragment} descriptor
 *
 * @author Carlo Corradini
 */
public final class ChampionshipAdminUsersDialog extends DialogFragment implements SwipeRefreshLayout.OnRefreshListener {

    /**
     * {@link Log} TAG of this class
     */
    public static final String TAG = ChampionshipAdminCircuitsDialog.class.getName();

    /**
     * {@link DialogFragment} {@link Toolbar}
     */
    private Toolbar toolbar;

    /**
     * {@link Championship} {@link Spinner}
     */
    private Spinner spinnerChampionships;

    /**
     * {@link User} {@link Spinner}
     */
    private Spinner spinnerUsers;

    /**
     * {@link User} input value
     */
    private EditText inputValue;

    /**
     * Save {@link Button}
     */
    private Button buttonSave;

    /**
     * {@link SwipeRefreshLayout} reference
     */
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Listener when save button has been clicked with the saved {@link UserDescriptor}
     */
    private OnDialogSelectionInterface<Pair<UserDescriptor, UserChampionship>> listener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_admin_users_dialog, container, false);

        toolbar = root.findViewById(R.id.fragment_admin_users_dialog_subscribe_toolbar);
        swipeRefreshLayout = root.findViewById(R.id.fragment_admin_users_dialog_swipe_refresh);
        spinnerChampionships = root.findViewById(R.id.fragment_admin_users_dialog_championship_spinner);
        spinnerUsers = root.findViewById(R.id.fragment_admin_users_dialog_users);
        inputValue = root.findViewById(R.id.fragment_admin_users_input);
        buttonSave = root.findViewById(R.id.fragment_admin_users_button_save);

        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle(R.string.pilots);
        if (toolbar.getNavigationIcon() != null)
            toolbar.getNavigationIcon().setTint(Color.WHITE);
        toolbar.setOnMenuItemClickListener(item -> {
            dismiss();
            return true;
        });

        swipeRefreshLayout.setColorSchemeColors(ColorHelper.COLOR_BLUE, ColorHelper.COLOR_RED, ColorHelper.COLOR_YELLOW, ColorHelper.COLOR_GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        inputValue.setFilters(new InputFilter[]{new NumberMinMaxFilter(0, Short.MAX_VALUE)});

        spinnerChampionships.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Championship championship = (Championship) spinnerChampionships.getItemAtPosition(position);

                swipeRefreshLayout.setRefreshing(true);
                spinnerUsers.setEnabled(false);
                inputValue.setEnabled(false);
                inputValue.setText("");
                buttonSave.setEnabled(false);
                loadUsers(championship.id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final UserDescriptor userDescriptor = (UserDescriptor) spinnerUsers.getItemAtPosition(position);

                inputValue.setText(String.valueOf(userDescriptor.userChampionship.points));
                inputValue.setEnabled(true);
                buttonSave.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonSave.setOnClickListener(v -> {
            if (isValidUpdate((UserDescriptor) spinnerUsers.getSelectedItem())) {
                InputUtil.hideKeyboard(requireActivity());
                saveChanges((UserDescriptor) spinnerUsers.getSelectedItem());
            }
        });

        loadChampionships();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    /**
     * Load all available {@link Championship}
     */
    private void loadChampionships() {
        API.getInstance().getAdminClient().create(ChampionshipService.class).find().enqueue(new Callback<API.Response<List<Championship>>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<List<Championship>>> call, @NotNull Response<API.Response<List<Championship>>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    final ArrayAdapter<Championship> championshipArrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, response.body().data);
                    championshipArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerChampionships.setEnabled(true);
                    spinnerChampionships.setAdapter(championshipArrayAdapter);
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<List<Championship>>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to find Championships due to " + t.getMessage(), t);
                ErrorHelper.showFailureError(getContext(), t);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Load available {@link User} given the championshipId
     *
     * @param championshipId The {@link Championship} id
     */
    private void loadUsers(final long championshipId) {
        API.getInstance().getAdminClient().create(UserChampionshipService.class).findByChampionship(championshipId).enqueue(new Callback<API.Response<List<UserChampionship>>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<List<UserChampionship>>> call, @NotNull Response<API.Response<List<UserChampionship>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final List<UserChampionship> userChampionships = response.body().data;

                    API.getInstance().getAdminClient().create(UserService.class).findByChampionship(championshipId).enqueue(new Callback<API.Response<List<User>>>() {
                        @Override
                        public void onResponse(@NotNull Call<API.Response<List<User>>> call, @NotNull Response<API.Response<List<User>>> response) {
                            swipeRefreshLayout.setRefreshing(false);
                            if (response.isSuccessful() && response.body() != null) {
                                final List<User> users = response.body().data;
                                final List<UserDescriptor> userDescriptors = new ArrayList<>(userChampionships.size());

                                for (UserChampionship userChampionship : userChampionships) {
                                    for (User user : users) {
                                        if (!userChampionship.user.equals(user.id))
                                            continue;
                                        userDescriptors.add(new UserDescriptor(userChampionship, user));

                                        break;
                                    }
                                }

                                final ArrayAdapter<UserDescriptor> userDescriptorArrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, userDescriptors);
                                userDescriptorArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerUsers.setEnabled(true);
                                spinnerUsers.setAdapter(userDescriptorArrayAdapter);
                            } else {
                                Toasty.error(requireContext(), R.string.error_unknown).show();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<API.Response<List<User>>> call, @NotNull Throwable t) {
                            Log.e(TAG, "Unable to find Users due to " + t.getMessage(), t);
                            ErrorHelper.showFailureError(getContext(), t);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<List<UserChampionship>>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to find Championship Users to " + t.getMessage(), t);
                ErrorHelper.showFailureError(getContext(), t);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Save changes {@link User} value given the {@link UserDescriptor}
     *
     * @param userDescriptor The {@link UserDescriptor} to save information
     */
    private void saveChanges(final UserDescriptor userDescriptor) {
        if (userDescriptor == null || swipeRefreshLayout.isRefreshing()) return;
        swipeRefreshLayout.setRefreshing(true);

        final UserChampionship updateUserChampionship = new UserChampionship();
        updateUserChampionship.points = Short.valueOf(inputValue.getText().toString());

        API.getInstance().getAdminClient().create(UserChampionshipService.class).update(userDescriptor.userChampionship.championship, userDescriptor.userChampionship.user, updateUserChampionship).enqueue(new Callback<API.Response>() {
            @Override
            public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    if (listener != null)
                        listener.onDialogSelection(Pair.of(userDescriptor, updateUserChampionship));
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to update Championship Users due to " + t.getMessage(), t);
                ErrorHelper.showFailureError(getContext(), t);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Set the listener called when the positive button has been clicked with the saved {@link UserDescriptor}
     *
     * @param listener Listener when positive button has been clicked with the selected {@link UserDescriptor}
     */
    public void setOnDialogSelectionListener(OnDialogSelectionInterface<Pair<UserDescriptor, UserChampionship>> listener) {
        this.listener = listener;
    }

    /**
     * Check if the update is valid
     *
     * @param userDescriptor Current {@link UserDescriptor}
     * @return True if valid, false otherwise
     */
    private boolean isValidUpdate(final UserDescriptor userDescriptor) {
        if (inputValue.getText().toString().isEmpty()) {
            Toasty.warning(requireContext(), R.string.warning_empty_value).show();
            return false;
        }
        if (Short.valueOf(inputValue.getText().toString()).equals(userDescriptor.userChampionship.points)) {
            Toasty.warning(requireContext(), R.string.warning_same_value).show();
            return false;
        }

        return true;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * {@link UserChampionship} & {@link User} class descriptor
     *
     * @author Carlo Corradini
     */
    public static class UserDescriptor {
        /**
         * {@link UserChampionship} reference
         */
        private final UserChampionship userChampionship;
        /**
         * {@link User} reference
         */
        private final User user;

        /**
         * Construct a {@link UserDescriptor} given the {@link UserDescriptor} & {@link User}
         *
         * @param userChampionship The {@link UserChampionship}
         * @param user             The {@link User}
         */
        public UserDescriptor(final UserChampionship userChampionship, final User user) {
            this.userChampionship = userChampionship;
            this.user = user;
        }

        /**
         * Return the {@link UserChampionship} reference
         *
         * @return {@link UserChampionship} reference
         */
        public UserChampionship getUserChampionship() {
            return userChampionship;
        }

        /**
         * Return the {@link User} reference
         *
         * @return {@link User} reference
         */
        public User getUser() {
            return user;
        }

        @NonNull
        @Override
        public String toString() {
            return String.format(Locale.getDefault(), "%s - %s - %d", user.username, user.id, userChampionship.points);
        }
    }
}
