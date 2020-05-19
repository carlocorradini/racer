package com.unitn.disi.lpsmt.racer.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;
import com.unitn.disi.lpsmt.racer.api.entity.Car;

import com.unitn.disi.lpsmt.racer.R;
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
 * {@link Championship} subscribe {@link DialogFragment dialog}
 *
 * @author Carlo Corradini
 */
public class ChampionshipSubscribeDialog extends DialogFragment {
    /**
     * {@link Log} TAG of this class
     */
    public static final String TAG = DialogFragment.class.getName();

    /**
     * {@link DialogFragment} {@link Toolbar}
     */
    private Toolbar toolbar;

    /**
     * {@link Championship} data
     */
    private Championship championship;

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
     * {@link EditText} {@link Championship} {@link Car}
     */
    private EditText inputCar;

    /**
     * {@link EditText} {@link Championship}
     */
    private EditText inputTeam;

    /**
     * {@link Button} {@link Championship} save
     */
    private Button buttonSave;

    /**
     * Listener when save button has been clicked with the saved {@link UserChampionship}
     */
    private OnDialogSelectionInterface<UserChampionship> listener = null;

    /**
     * Construct a new {@link ChampionshipSubscribeDialog} given the championship
     *
     * @param championship The {@link Championship} to display subscription
     * @throws NullPointerException If the given championship is null
     */
    public ChampionshipSubscribeDialog(final Championship championship) throws NullPointerException {
        if (championship == null) throw new NullPointerException("Championship is null");
        this.championship = championship;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_championship_dialog_subscribe, container, false);

        toolbar = root.findViewById(R.id.fragment_championship_dialog_subscribe_toolbar);
        imageChampionshipLogo = root.findViewById(R.id.fragment_championship_dialog_subscribe_championship_logo);
        txtChampionshipName = root.findViewById(R.id.fragment_championship_dialog_subscribe_championship_name);
        txtChampionshipId = root.findViewById(R.id.fragment_championship_dialog_subscribe_championship_id);
        inputCar = root.findViewById(R.id.fragment_championship_dialog_subscribe_input_car);
        inputTeam = root.findViewById(R.id.fragment_championship_dialog_subscribe_input_team);
        buttonSave = root.findViewById(R.id.fragment_championship_dialog_subscribe_button_subscribe);

        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final UserChampionship userChampionship = new UserChampionship();
        userChampionship.championship = championship.id;
        final CarDialog dialogCar = new CarDialog();
        dialogCar.setVisibleCarsForced(championship.cars);
        final TeamDialog dialogTeam = new TeamDialog();

        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle(R.string.subscribe);
        toolbar.setOnMenuItemClickListener(item -> {
            dismiss();
            return true;
        });

        Picasso.get().load(championship.logo.toString()).into(imageChampionshipLogo);
        txtChampionshipName.setText(championship.name);
        txtChampionshipId.setText(String.valueOf(championship.id));

        inputCar.setOnClickListener(v -> {
            if (!dialogCar.isAdded())
                dialogCar.show(getParentFragmentManager(), CarDialog.class.getName());
        });
        inputTeam.setOnClickListener(v -> {
            if (!dialogTeam.isAdded())
                dialogTeam.show(getParentFragmentManager(), TeamDialog.class.getName());
        });

        dialogCar.setOnDialogSelectionListener(car -> {
            userChampionship.car = car.id;
            inputCar.setText(car.getFullName());
        });
        dialogTeam.setOnDialogSelectionListener(team -> {
            userChampionship.team = team.id;
            inputTeam.setText(team.name);
        });

        buttonSave.setOnClickListener(v -> {
            if (isValidUserChampionship(userChampionship)) {
                InputUtil.hideKeyboard(this.requireActivity());
                doSubscribe(userChampionship);
            }
        });
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
     * Perform a Subscribe call
     *
     * @param userChampionship The {@link UserChampionship} to save
     * @see UserChampionshipService#create(UserChampionship)
     */
    private void doSubscribe(final UserChampionship userChampionship) {
        if (userChampionship == null) return;

        API.getInstance().getClient().create(UserChampionshipService.class).create(userChampionship).enqueue(new Callback<API.Response<Long>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<Long>> call, @NotNull Response<API.Response<Long>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if(listener != null) listener.onDialogSelection(userChampionship);
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<Long>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to create User Championship due to " + t.getMessage(), t);
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

        if (userChampionship.car == null) {
            Toasty.warning(getContext(), R.string.warning_empty_car).show();
            return false;
        }
        if (userChampionship.team == null) {
            Toasty.warning(getContext(), R.string.warning_empty_team).show();
            return false;
        }

        return true;
    }

    /**
     * Set the listener called when the positive button has been clicked with the saved {@link UserChampionship}
     *
     * @param listener Listener when positive button has been clicked with the selected {@link UserChampionship}
     */
    public void setOnDialogSelectionListener(OnDialogSelectionInterface<UserChampionship> listener) {
        this.listener = listener;
    }
}
