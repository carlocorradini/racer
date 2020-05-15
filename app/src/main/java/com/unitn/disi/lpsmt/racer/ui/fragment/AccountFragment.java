package com.unitn.disi.lpsmt.racer.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.racer.R;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.Car;
import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.api.entity.error.ConflictError;
import com.unitn.disi.lpsmt.racer.api.entity.error.UnprocessableEntityError;
import com.unitn.disi.lpsmt.racer.api.service.CarService;
import com.unitn.disi.lpsmt.racer.api.service.CircuitService;
import com.unitn.disi.lpsmt.racer.api.service.UserService;
import com.unitn.disi.lpsmt.racer.filter.NumberMinMaxFilter;
import com.unitn.disi.lpsmt.racer.helper.ColorHelper;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;
import com.unitn.disi.lpsmt.racer.observer.UserObserver;
import com.unitn.disi.lpsmt.racer.api.entity.Circuit;
import com.unitn.disi.lpsmt.racer.ui.component.dialog.CarDialog;
import com.unitn.disi.lpsmt.racer.ui.component.dialog.CircuitDialog;

import java.text.DateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Observable;
import java.time.LocalDate;
import java.util.Observer;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * {@link User} account {@link Fragment}
 *
 * @author Carlo Corradini
 */
public final class AccountFragment extends Fragment implements Observer, SwipeRefreshLayout.OnRefreshListener {

    /**
     * {@link TextView} {@link User} {@link UUID id}
     */
    private TextView txtId;
    /**
     * {@link TextView} {@link User} username
     */
    private TextView txtUsername;
    /**
     * {@link TextView} {@link User} name
     */
    private TextView txtName;
    /**
     * {@link EditText} {@link User} name
     */
    private EditText inputName;
    /**
     * {@link TextView} {@link User} surname
     */
    private TextView txtSurname;
    /**
     * {@link EditText} {@link User} surname
     */
    private EditText inputSurname;
    /**
     * {@link TextView} {@link User} {@link User.Role role}
     */
    private TextView txtRole;
    /**
     * {@link TextView} {@link User} {@link User.Gender gender}
     */
    private TextView txtGender;
    /**
     * {@link Spinner} {@link User} {@link User.Gender gender}
     */
    private Spinner inputGender;
    /**
     * {@link TextView} {@link User} {@link LocalDate date of birth}
     */
    private TextView txtDateOfBirth;
    /**
     * {@link EditText} {@link User} {@link LocalDate date of birth}
     */
    private EditText inputDateOfBirth;
    /**
     * {@link TextView} {@link User} residence
     */
    private TextView txtResidence;
    /**
     * {@link EditText} {@link User} residence
     */
    private EditText inputResidence;
    /**
     * {@link TextView} {@link User} favorite number
     */
    private TextView txtFavoriteNumber;
    /**
     * {@link EditText} {@link User} favorite number
     */
    private EditText inputFavoriteNumber;
    /**
     * {@link TextView} {@link User} favorite {@link Car}
     */
    private TextView txtFavoriteCar;
    /**
     * {@link ProgressBar} {@link User} favorite {@link Car}
     */
    private ProgressBar progressFavoriteCar;
    /**
     * {@link EditText} {@link User} favorite {@link Car}
     */
    private EditText inputFavoriteCar;
    /**
     * {@link TextView} {@link User} favorite {@link Circuit}
     */
    private TextView txtFavoriteCircuit;
    /**
     * {@link ProgressBar} {@link User} favorite {@link Circuit}
     */
    private ProgressBar progressFavoriteCircuit;
    /**
     * {@link EditText} {@link User} favorite {@link Circuit}
     */
    private EditText inputFavoriteCircuit;
    /**
     * {@link TextView} {@link User} hated {@link Circuit}
     */
    private TextView txtHatedCircuit;
    /**
     * {@link ProgressBar} {@link User} hated {@link Circuit}
     */
    private ProgressBar progressHatedCircuit;
    /**
     * {@link EditText} {@link User} hated {@link Circuit}
     */
    private EditText inputHatedCircuit;
    /**
     * {@link ImageView} {@link User} avatar
     */
    private ImageView imageAvatar;
    /**
     * {@link SwipeRefreshLayout} for reloading the {@link User} in the fragment
     */
    private SwipeRefreshLayout swipeRefreshLayout;
    /**
     * {@link FloatingActionButton Button} {@link User} edit
     */
    private FloatingActionButton buttonEdit;
    /**
     * {@link FloatingActionButton Button} cancel {@link User} edit
     */
    private FloatingActionButton buttonCancel;
    /**
     * {@link User} account {@link View Container}
     */
    private View accountContainerView;
    /**
     * {@link UserObserver User Observer} {@link Observable}
     */
    private Observable userObservable;
    /**
     * Flag used to check if the {@link User} is in update mode
     */
    private boolean isUpdateMode = false;

    @NotNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        accountContainerView = root.findViewById(R.id.fragment_account_container);
        swipeRefreshLayout = root.findViewById(R.id.fragment_account_swipe_refresh);
        buttonEdit = root.findViewById(R.id.fragment_account_button_edit);
        buttonCancel = root.findViewById(R.id.fragment_account_button_cancel);
        imageAvatar = root.findViewById(R.id.fragment_account_avatar);
        txtId = root.findViewById(R.id.fragment_account_id);
        txtUsername = root.findViewById(R.id.fragment_account_username);
        txtName = root.findViewById(R.id.fragment_account_name);
        inputName = root.findViewById(R.id.fragment_account_name_input);
        txtSurname = root.findViewById(R.id.fragment_account_surname);
        inputSurname = root.findViewById(R.id.fragment_account_surname_input);
        txtRole = root.findViewById(R.id.fragment_account_role);
        txtGender = root.findViewById(R.id.fragment_account_gender);
        inputGender = root.findViewById(R.id.fragment_account_gender_input);
        txtDateOfBirth = root.findViewById(R.id.fragment_account_date_of_birth);
        inputDateOfBirth = root.findViewById(R.id.fragment_account_date_of_birth_input);
        txtResidence = root.findViewById(R.id.fragment_account_residence);
        inputResidence = root.findViewById(R.id.fragment_account_residence_input);
        txtFavoriteNumber = root.findViewById(R.id.fragment_account_favorite_number);
        inputFavoriteNumber = root.findViewById(R.id.fragment_account_favorite_number_input);
        inputFavoriteNumber.setFilters(new InputFilter[]{new NumberMinMaxFilter(User.FAVORITE_NUMBER_MIN, User.FAVORITE_NUMBER_MAX)});
        txtFavoriteCar = root.findViewById(R.id.fragment_account_favorite_car);
        progressFavoriteCar = root.findViewById(R.id.fragment_account_favorite_car_progress);
        inputFavoriteCar = root.findViewById(R.id.fragment_account_favorite_car_input);
        txtFavoriteCircuit = root.findViewById(R.id.fragment_account_favorite_circuit);
        progressFavoriteCircuit = root.findViewById(R.id.fragment_account_favorite_circuit_progress);
        inputFavoriteCircuit = root.findViewById(R.id.fragment_account_favorite_circuit_input);
        txtHatedCircuit = root.findViewById(R.id.fragment_account_hated_circuit);
        progressHatedCircuit = root.findViewById(R.id.fragment_account_hated_circuit_progress);
        inputHatedCircuit = root.findViewById(R.id.fragment_account_hated_circuit_input);

        swipeRefreshLayout.setColorSchemeColors(ColorHelper.COLOR_BLUE, ColorHelper.COLOR_RED, ColorHelper.COLOR_YELLOW, ColorHelper.COLOR_GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        userObservable = UserObserver.getInstance();
        userObservable.addObserver(this);

        setUIData(UserObserver.getInstance().getUser());

        setEditActions();

        return root;
    }

    /**
     * Set UI from the data given
     *
     * @param user The data {@link User}
     */
    private void setUIData(final User user) {
        if (user == null) return;

        Picasso.get().load(user.avatar.toString()).into(imageAvatar);
        txtId.setText(user.id.toString());
        txtUsername.setText(user.username);
        txtName.setText(user.name);
        txtSurname.setText(user.surname);
        txtRole.setText(user.role.getValueRes());
        txtGender.setText(user.gender.getValueRes());
        txtGender.setTag(user.gender);
        txtDateOfBirth.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date.from(user.dateOfBirth.atStartOfDay(ZoneId.systemDefault()).toInstant())));
        txtResidence.setText(user.residence);
        txtFavoriteNumber.setText(String.valueOf(user.favoriteNumber));
        txtFavoriteCar.setVisibility(View.GONE);
        txtFavoriteCar.setTag(user.favoriteCar);
        progressFavoriteCar.setVisibility(View.VISIBLE);
        txtFavoriteCircuit.setVisibility(View.GONE);
        txtFavoriteCircuit.setTag(user.favoriteCircuit);
        progressFavoriteCircuit.setVisibility(View.VISIBLE);
        txtHatedCircuit.setVisibility(View.GONE);
        txtHatedCircuit.setTag(user.hatedCircuit);
        progressHatedCircuit.setVisibility(View.VISIBLE);

        loadFavoriteCar(user.favoriteCar);
        loadFavoriteCircuit(user.favoriteCircuit);
        loadHatedCircuit(user.hatedCircuit);

        swipeRefreshLayout.setRefreshing(false);
        accountContainerView.setVisibility(View.VISIBLE);
    }

    private void setEditActions() {
        final User user = new User();
        final CarDialog dialogFavoriteCar = new CarDialog();
        final CircuitDialog dialogFavoriteCircuit = new CircuitDialog();
        final CircuitDialog dialogHatedCircuit = new CircuitDialog();

        buttonEdit.setOnClickListener(v -> {
            isUpdateMode = !isUpdateMode;
            setEditActionsVisibility();

            if (isUpdateMode) {
                // Save mode
                buttonEdit.setImageResource(R.drawable.ic_save);
                buttonCancel.setVisibility(View.VISIBLE);
                inputName.setText(txtName.getText().toString());
                inputSurname.setText(txtSurname.getText().toString());
                switch ((User.Gender) txtGender.getTag()) {
                    case MALE: {
                        inputGender.setSelection(1);
                        break;
                    }
                    case FEMALE: {
                        inputGender.setSelection(2);
                        break;
                    }
                    default: {
                        inputGender.setSelection(0);
                        break;
                    }
                }
                inputDateOfBirth.setText(txtDateOfBirth.getText().toString());
                inputResidence.setText(txtResidence.getText().toString());
                inputFavoriteNumber.setText(txtFavoriteNumber.getText().toString());
                inputFavoriteCar.setText(txtFavoriteCar.getText().toString());
                dialogFavoriteCar.setCurrentCarForced((Long) txtFavoriteCar.getTag());
                inputFavoriteCircuit.setText(txtFavoriteCircuit.getText().toString());
                dialogFavoriteCircuit.setCurrentCircuitForced((Long) txtFavoriteCircuit.getTag());
                inputHatedCircuit.setText(txtHatedCircuit.getText().toString());
                dialogHatedCircuit.setCurrentCircuitForced((Long) txtHatedCircuit.getTag());
            } else {
                // Save request
                user.name = inputName.getText().toString();
                user.surname = inputSurname.getText().toString();
                switch (inputGender.getSelectedItemPosition()) {
                    case 1: {
                        user.gender = User.Gender.MALE;
                        break;
                    }
                    case 2: {
                        user.gender = User.Gender.FEMALE;
                        break;
                    }
                    default: {
                        user.gender = User.Gender.UNKNOWN;
                    }
                }
                user.residence = inputResidence.getText().toString();
                user.favoriteNumber = Short.valueOf(inputFavoriteNumber.getText().toString());
                updateUserData(user);
            }
        });

        buttonCancel.setOnClickListener(v -> {
            isUpdateMode = false;
            user.reset();
            buttonEdit.setImageResource(R.drawable.ic_edit);
            buttonCancel.setVisibility(View.GONE);
            setEditActionsVisibility();
        });

        // Date of birth
        inputDateOfBirth.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (view, year, month, dayOfMonth) -> {
                Calendar dateOfBirth = Calendar.getInstance();
                dateOfBirth.set(year, month, dayOfMonth);
                user.dateOfBirth = LocalDate.of(year, month + 1, dayOfMonth);
                inputDateOfBirth.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateOfBirth.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        });

        // Favorite Car
        inputFavoriteCar.setOnClickListener(v -> {
            if (!dialogFavoriteCar.isAdded())
                dialogFavoriteCar.show(getChildFragmentManager(), CarDialog.class.getName());
        });
        dialogFavoriteCar.setOnDialogSelectionListener(car -> {
            if (car == null) return;
            user.favoriteCar = car.id;
            inputFavoriteCar.setText(car.getFullName());
        });

        // Favorite Circuit
        inputFavoriteCircuit.setOnClickListener(v -> {
            if (!dialogFavoriteCircuit.isAdded())
                dialogFavoriteCircuit.show(getChildFragmentManager(), CircuitDialog.class.getName());
        });
        dialogFavoriteCircuit.setOnDialogSelectionListener(circuit -> {
            if (circuit == null) return;
            user.favoriteCircuit = circuit.id;
            inputFavoriteCircuit.setText(circuit.name);
        });

        // Hated Circuit
        inputHatedCircuit.setOnClickListener(v -> {
            if (!dialogHatedCircuit.isAdded())
                dialogHatedCircuit.show(getChildFragmentManager(), CircuitDialog.class.getName());
        });
        dialogHatedCircuit.setOnDialogSelectionListener(circuit -> {
            if (circuit == null) return;
            user.hatedCircuit = circuit.id;
            inputHatedCircuit.setText(circuit.name);
        });
    }

    private void setEditActionsVisibility() {
        final int visibilityText = isUpdateMode ? View.GONE : View.VISIBLE;
        final int visibilityInput = isUpdateMode ? View.VISIBLE : View.GONE;

        txtName.setVisibility(visibilityText);
        inputName.setVisibility(visibilityInput);
        txtSurname.setVisibility(visibilityText);
        inputSurname.setVisibility(visibilityInput);
        txtGender.setVisibility(visibilityText);
        inputGender.setVisibility(visibilityInput);
        txtDateOfBirth.setVisibility(visibilityText);
        inputDateOfBirth.setVisibility(visibilityInput);
        txtResidence.setVisibility(visibilityText);
        inputResidence.setVisibility(visibilityInput);
        txtFavoriteNumber.setVisibility(visibilityText);
        inputFavoriteNumber.setVisibility(visibilityInput);
        txtFavoriteCar.setVisibility(visibilityText);
        inputFavoriteCar.setVisibility(visibilityInput);
        txtFavoriteCircuit.setVisibility(visibilityText);
        inputFavoriteCircuit.setVisibility(visibilityInput);
        txtHatedCircuit.setVisibility(visibilityText);
        inputHatedCircuit.setVisibility(visibilityInput);
    }

    private void updateUserData(final User user) {
        if (user == null) return;

        API.getInstance().getClient().create(UserService.class).update(user).enqueue(new Callback<API.Response>() {
            @Override
            public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toasty.success(requireContext(), R.string.success_update).show();
                    buttonEdit.setImageResource(R.drawable.ic_edit);
                    buttonCancel.setVisibility(View.GONE);
                    UserObserver.getInstance().refreshUser();
                } else if (response.errorBody() != null) {
                    switch (response.code()) {
                        case HttpStatus.SC_UNPROCESSABLE_ENTITY: {
                            API.Response<List<UnprocessableEntityError>> error = API.ErrorConverter.convert(response.errorBody(), API.ErrorConverter.TYPE_UNPROCESSABLE_ENTITY_LIST);
                            StringBuilder entities = new StringBuilder();
                            for (int i = 0; i < error.data.size(); ++i) {
                                entities.append(error.data.get(i).property);
                                if (i != error.data.size() - 1) {
                                    entities.append(", ");
                                }
                            }
                            Toasty.warning(requireContext(), getResources().getString(R.string.warning_unprocessable_entity) + "\n" + entities).show();
                            break;
                        }
                        case HttpStatus.SC_CONFLICT: {
                            API.Response<List<ConflictError>> error = API.ErrorConverter.convert(response.errorBody(), API.ErrorConverter.TYPE_CONFLICT_LIST);
                            StringBuilder conflicts = new StringBuilder();
                            for (int i = 0; i < error.data.size(); ++i) {
                                conflicts.append(error.data.get(i).property);
                                if (i != error.data.size() - 1) {
                                    conflicts.append(", ");
                                }
                            }
                            Toasty.warning(requireContext(), getResources().getString(R.string.warning_conflict) + "\n" + conflicts).show();
                            break;
                        }
                        default: {
                            Toasty.warning(requireContext(), R.string.error_internal_server_error).show();
                            break;
                        }
                    }
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    /**
     * Load {@link User} favorite {@link Car} and set the UI
     *
     * @param id {@link Car} id
     */
    private void loadFavoriteCar(Long id) {
        API.getInstance().getClient().create(CarService.class).findById(id).enqueue(new Callback<API.Response<Car>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<Car>> call, @NotNull Response<API.Response<Car>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Car favoriteCar = response.body().data;
                    txtFavoriteCar.setText(favoriteCar.getFullName());
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }

                progressFavoriteCar.setVisibility(View.GONE);
                txtFavoriteCar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<Car>> call, @NotNull Throwable t) {
                progressFavoriteCar.setVisibility(View.GONE);
                txtFavoriteCar.setVisibility(View.VISIBLE);
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    /**
     * Load {@link User} favorite {@link Circuit} and set the UI
     *
     * @param id {@link Circuit} id
     */
    private void loadFavoriteCircuit(Long id) {
        API.getInstance().getClient().create(CircuitService.class).findById(id).enqueue(new Callback<API.Response<Circuit>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<Circuit>> call, @NotNull Response<API.Response<Circuit>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Circuit favoriteCircuit = response.body().data;
                    txtFavoriteCircuit.setText(favoriteCircuit.name);
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }

                progressFavoriteCircuit.setVisibility(View.GONE);
                txtFavoriteCircuit.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<Circuit>> call, @NotNull Throwable t) {
                progressFavoriteCircuit.setVisibility(View.GONE);
                txtFavoriteCircuit.setVisibility(View.VISIBLE);
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    /**
     * Load {@link User} hated {@link Circuit} and set the UI
     *
     * @param id {@link Circuit} id
     */
    private void loadHatedCircuit(Long id) {
        API.getInstance().getClient().create(CircuitService.class).findById(id).enqueue(new Callback<API.Response<Circuit>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<Circuit>> call, @NotNull Response<API.Response<Circuit>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Circuit hatedCircuit = response.body().data;
                    txtHatedCircuit.setText(hatedCircuit.name);
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }

                progressHatedCircuit.setVisibility(View.GONE);
                txtHatedCircuit.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<Circuit>> call, @NotNull Throwable t) {
                progressHatedCircuit.setVisibility(View.GONE);
                txtHatedCircuit.setVisibility(View.VISIBLE);
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userObservable.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof UserObserver) {
            setUIData(((UserObserver) o).getUser());
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        accountContainerView.setVisibility(View.GONE);
        UserObserver.getInstance().refreshUser();
    }
}
