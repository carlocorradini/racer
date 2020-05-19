package com.unitn.disi.lpsmt.racer.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.Car;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;
import com.unitn.disi.lpsmt.racer.api.entity.Team;
import com.unitn.disi.lpsmt.racer.api.entity.UserChampionship;
import com.unitn.disi.lpsmt.racer.api.service.UserChampionshipService;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;
import com.unitn.disi.lpsmt.racer.ui.component.dialog.CarDialog;
import com.unitn.disi.lpsmt.racer.ui.component.dialog.OnDialogSelectionInterface;
import com.unitn.disi.lpsmt.racer.ui.component.dialog.TeamDialog;
import com.unitn.disi.lpsmt.racer.util.InputUtil;

import org.jetbrains.annotations.NotNull;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * {@link Championship} view subscription {@link DialogFragment dialog}
 *
 * @author Carlo Corradini
 */
public final class ChampionshipViewSubscriptionDialog extends DialogFragment {
    /**
     * {@link Log} TAG of this class
     */
    public static final String TAG = ChampionshipViewSubscriptionDialog.class.getName();

    /**
     * {@link DialogFragment} {@link Toolbar}
     */
    private Toolbar toolbar;

    /**
     * {@link Championship} data
     */
    private Championship championship;

    /**
     * {@link UserChampionship} data
     */
    private UserChampionship userChampionship;

    /**
     * {@link UserChampionship} {@link Car}
     */
    private Car car;

    /**
     * {@link UserChampionship} {@link Team}
     */
    private Team team;

    /**
     * {@link ImageView} {@link Championship} logo
     */
    private ImageView imageChampionshipLogo;

    /**
     * {@link TextView} {@link Championship} name
     */
    private TextView txtChampionshipName;

    /**
     * {@link TextView} {@link Championship} id
     */
    private TextView txtChampionshipId;

    /**
     * {@link TextView} {@link UserChampionship} {@link Car}
     */
    private TextView txtCar;

    /**
     * {@link EditText} {@link Championship} {@link Car}
     */
    private EditText inputCar;

    /**
     * {@link TextView} {@link UserChampionship} {@link Team}
     */
    private TextView txtTeam;

    /**
     * {@link EditText} {@link Championship}
     */
    private EditText inputTeam;

    /**
     * {@link FloatingActionButton Button} {@link UserChampionship} edit
     */
    private FloatingActionButton buttonEdit;
    /**
     * {@link FloatingActionButton Button} cancel {@link UserChampionship} edit
     */
    private FloatingActionButton buttonCancel;

    /**
     * Flag used to check if the {@link UserChampionship} is in update mode
     */
    private boolean isUpdateMode = false;

    /**
     * Boolean flag to check if the {@link Championship} has been already started
     */
    private boolean isChampionshipStarted = false;

    /**
     * Listener when update button has been clicked with the updated {@link UserChampionship}
     */
    private OnDialogSelectionInterface<UserChampionship> listener = null;

    /**
     * Construct a new {@link ChampionshipViewSubscriptionDialog} given the championship and current {@link UserChampionship} of the authenticated user
     *
     * @param championship     The {@link Championship} to display subscription
     * @param userChampionship The {@link UserChampionship} of the current authenticated User
     * @param car              The {@link Car} chosen in the {@link UserChampionship}
     * @param team             The {@link Team} chosen in {@link UserChampionship}
     * @throws NullPointerException If the given championship or userChampionship is null
     */
    public ChampionshipViewSubscriptionDialog(final Championship championship, final UserChampionship userChampionship, final Car car, final Team team, boolean isChampionshipStarted) throws NullPointerException {
        if (championship == null) throw new NullPointerException("Championship is null");
        this.championship = championship;
        this.userChampionship = userChampionship;
        this.car = car;
        this.team = team;
        this.isChampionshipStarted = isChampionshipStarted;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_championship_dialog_view_subscription, container, false);

        toolbar = root.findViewById(R.id.fragment_championship_dialog_view_subscription_toolbar);
        imageChampionshipLogo = root.findViewById(R.id.fragment_championship_dialog_view_subscription_championship_logo);
        txtChampionshipName = root.findViewById(R.id.fragment_championship_dialog_view_subscription_championship_name);
        txtChampionshipId = root.findViewById(R.id.fragment_championship_dialog_view_subscription_championship_id);
        txtCar = root.findViewById(R.id.fragment_championship_dialog_view_subscription_txt_car);
        inputCar = root.findViewById(R.id.fragment_championship_dialog_view_subscription_input_car);
        txtTeam = root.findViewById(R.id.fragment_championship_dialog_view_subscription_txt_team);
        inputTeam = root.findViewById(R.id.fragment_championship_dialog_view_subscription_input_team);
        buttonEdit = root.findViewById(R.id.fragment_championship_dialog_view_subscription_button_edit);
        buttonCancel = root.findViewById(R.id.fragment_championship_dialog_view_subscription_button_cancel);

        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final UserChampionship userChampionshipUpdated = new UserChampionship();
        final CarDialog dialogCar = new CarDialog();
        dialogCar.setCurrentCarForced(userChampionship.car);
        dialogCar.setVisibleCarsForced(championship.cars);
        final TeamDialog dialogTeam = new TeamDialog();
        dialogTeam.setCurrentTeamForced(userChampionship.team);

        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle(R.string.subscription);
        if (toolbar.getNavigationIcon() != null)
            toolbar.getNavigationIcon().setTint(Color.WHITE);
        toolbar.setOnMenuItemClickListener(item -> {
            dismiss();
            return true;
        });

        Picasso.get().load(championship.logo.toString()).into(imageChampionshipLogo);
        txtChampionshipName.setText(championship.name);
        txtChampionshipId.setText(String.valueOf(championship.id));
        txtCar.setText(car.getFullName());
        txtTeam.setText(team.name);

        inputCar.setOnClickListener(v -> {
            if (!dialogCar.isAdded())
                dialogCar.show(getParentFragmentManager(), CarDialog.class.getName());
        });
        inputTeam.setOnClickListener(v -> {
            if (!dialogTeam.isAdded())
                dialogTeam.show(getParentFragmentManager(), TeamDialog.class.getName());
        });

        dialogCar.setOnDialogSelectionListener(car -> {
            userChampionshipUpdated.car = car.id;
            inputCar.setText(car.getFullName());
        });
        dialogTeam.setOnDialogSelectionListener(team -> {
            userChampionshipUpdated.team = team.id;
            inputTeam.setText(team.name);
        });

        buttonEdit.setOnClickListener(v -> {
            isUpdateMode = !isUpdateMode;

            if (isUpdateMode) {
                // Save mode
                userChampionshipUpdated.car = null;
                userChampionshipUpdated.team = null;

                buttonEdit.setImageResource(R.drawable.ic_save);
                buttonCancel.setVisibility(View.VISIBLE);
                inputCar.setText(txtCar.getText().toString());
                inputTeam.setText(txtTeam.getText().toString());
                dialogCar.setCurrentCarForced(car.id);
                dialogTeam.setCurrentTeamForced(team.id);
            } else {
                // Save request
                if (isValidUserChampionship(userChampionshipUpdated)) {
                    InputUtil.hideKeyboard(getActivity());
                    updateUserChampionship(userChampionshipUpdated);
                } else {
                    isUpdateMode = true;
                }
            }

            setEditActionsVisibility();
        });

        buttonCancel.setOnClickListener(v -> {
            InputUtil.hideKeyboard(requireActivity());
            isUpdateMode = false;
            buttonEdit.setImageResource(R.drawable.ic_edit);
            buttonCancel.setVisibility(View.GONE);
            setEditActionsVisibility();
        });

        if (isChampionshipStarted) buttonEdit.setVisibility(View.GONE);
    }

    /**
     * Set {@link UserChampionship} edit {@link View views} visibility
     */
    private void setEditActionsVisibility() {
        final int visibilityText = isUpdateMode ? View.GONE : View.VISIBLE;
        final int visibilityInput = isUpdateMode ? View.VISIBLE : View.GONE;

        txtCar.setVisibility(visibilityText);
        inputCar.setVisibility(visibilityInput);
        txtTeam.setVisibility(visibilityText);
        inputTeam.setVisibility(visibilityInput);
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
     * Update the given {@link UserChampionship userChampionship}
     *
     * @param userChampionship The {@link UserChampionship} to update
     */
    private void updateUserChampionship(final UserChampionship userChampionship) {
        if (userChampionship == null) return;

        API.getInstance().getClient().create(UserChampionshipService.class).update(championship.id, userChampionship).enqueue(new Callback<API.Response>() {
            @Override
            public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (listener != null) listener.onDialogSelection(userChampionship);
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to update User Championship due to " + t.getMessage(), t);
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    /**
     * Validate the given userChampionship and show warning message if not
     *
     * @param userChampionship The {@link UserChampionship} to validate
     * @return True if userChampionship is valid, false otherwise
     */
    private boolean isValidUserChampionship(final UserChampionship userChampionship) {
        return true;
    }

    /**
     * Set the listener called when the update button has been clicked with the updated {@link UserChampionship}
     *
     * @param listener Listener when update button has been clicked with the selected {@link UserChampionship}
     */
    public void setOnDialogSelectionListener(OnDialogSelectionInterface<UserChampionship> listener) {
        this.listener = listener;
    }
}
