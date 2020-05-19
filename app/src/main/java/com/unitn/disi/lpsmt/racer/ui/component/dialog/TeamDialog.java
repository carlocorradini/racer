package com.unitn.disi.lpsmt.racer.ui.component.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.Team;
import com.unitn.disi.lpsmt.racer.api.service.TeamService;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Team Dialog
 *
 * @author Carlo Corradini
 */
public final class TeamDialog extends AppCompatDialogFragment {

    /**
     * Current selected {@link Team}
     */
    private Team currentTeam = null;
    /**
     * Current {@link Team} force to set checked
     */
    private Long currentTeamForced = null;
    /**
     * Visible {@link Team teams}
     */
    private List<Long> visibleTeamsForced = null;
    /**
     * Old selected {@link Team team} if the {@link TeamDialog} was close and the positive button has been clicked
     */
    private Team savedTeam = null;
    /**
     * {@link List} of available {@link Team teams}
     * The {@link List} is populated only once calling the {@link TeamService} find method
     */
    private List<Team> teams = new ArrayList<>();
    /**
     * {@link RadioButton Team Radio Button} {@link RadioGroup container}
     */
    private RadioGroup teamsContainer;
    /**
     * Data container
     */
    private LinearLayout dataContainer;
    /**
     * {@link ProgressBar} loader
     */
    private ProgressBar loader;
    /**
     * Listener when positive button has been clicked with the selected {@link Team}
     */
    private OnDialogSelectionInterface<Team> listener = null;
    /**
     * Current {@link View} view
     */
    private View view = null;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        setRetainInstance(true);
        setCancelable(true);

        view = requireActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_team, null);

        EditText inputSearch = view.findViewById(R.id.fragment_dialog_team_input_search);
        teamsContainer = view.findViewById(R.id.fragment_dialog_team_container);
        dataContainer = view.findViewById(R.id.fragment_dialog_team_data_container);
        loader = view.findViewById(R.id.fragment_dialog_team_loader);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                for (int i = 0; i < teamsContainer.getChildCount(); i++) {
                    View v = teamsContainer.getChildAt(i);
                    RadioButton teamRadioButton = v instanceof RadioButton ? (RadioButton) v : null;

                    if (teamRadioButton != null
                            && teamRadioButton.getText().toString().toLowerCase().contains(s.toString().toLowerCase())) {
                        teamRadioButton.setVisibility(View.VISIBLE);
                    } else if (teamRadioButton != null) {
                        teamRadioButton.setVisibility(View.GONE);
                    }
                }
            }
        });
        teamsContainer.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton teamRadioButton = group.findViewById(checkedId);
            int index = (int) teamRadioButton.getTag();
            currentTeam = index >= teams.size() ? null : teams.get(index);
        });

        loadTeams();

        return new AlertDialog.Builder(requireActivity())
                .setView(view)
                .setTitle(R.string.choose_team)
                .setNegativeButton(R.string.dismiss, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    savedTeam = currentTeam;
                    if (currentTeam != null && listener != null)
                        listener.onDialogSelection(currentTeam);
                })
                .create();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    /**
     * Load all available {@link Team teams} calling the {@link TeamService} find method
     * If the teams list is not empty no call will be generated
     */
    private void loadTeams() {
        if (!teams.isEmpty()) showTeams();
        else
            API.getInstance().getClient().create(TeamService.class).find().enqueue(new Callback<API.Response<List<Team>>>() {
                @Override
                public void onResponse(@NotNull Call<API.Response<List<Team>>> call, @NotNull Response<API.Response<List<Team>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        teams = response.body().data;
                        showTeams();
                    } else {
                        Toasty.error(view.getContext(), R.string.error_unknown).show();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<API.Response<List<Team>>> call, @NotNull Throwable t) {
                    ErrorHelper.showFailureError(view.getContext(), t);
                }
            });
    }

    /**
     * Show the available teams in the {@link List} of teams into the teamsContainer
     * After hide the loader and show the dataContainer
     */
    private void showTeams() {
        for (int i = 0; i < teams.size(); ++i) {
            if (visibleTeamsForced != null && !visibleTeamsForced.contains(teams.get(i).id)) continue;

            RadioButton teamRadioButton = new RadioButton(getContext());
            teamRadioButton.setText(teams.get(i).name);
            teamRadioButton.setTag(i);
            teamsContainer.addView(teamRadioButton);
            if (currentTeamForced == null && savedTeam != null && savedTeam.equals(teams.get(i)))
                teamRadioButton.setChecked(true);
            else if (currentTeamForced != null && currentTeamForced.equals(teams.get(i).id)) {
                savedTeam = teams.get(i);
                currentTeamForced = null;
                teamRadioButton.setChecked(true);
            }
        }
        currentTeamForced = null;
        loader.setVisibility(View.GONE);
        dataContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Set the listener called when the positive button has been clicked with the selected {@link Team}
     *
     * @param listener Listener when positive button has been clicked with the selected {@link Team}
     */
    public void setOnDialogSelectionListener(OnDialogSelectionInterface<Team> listener) {
        this.listener = listener;
    }

    /**
     * Set the current {@link Team} even if currentTeam is not null
     *
     * @param currentTeamForced The {@link Team} {@link Long id} to force to be checked
     */
    public void setCurrentTeamForced(Long currentTeamForced) {
        this.currentTeamForced = currentTeamForced;
    }

    /**
     * Set the {@link List} of visible {@link Team teams} forced
     *
     * @param visibleTeamsForced The {@link List} of {@link Team} {@link Long ids} to be forced to show
     */
    public void setVisibleTeamsForced(final List<Long> visibleTeamsForced) {
        this.visibleTeamsForced = visibleTeamsForced;
    }
}
