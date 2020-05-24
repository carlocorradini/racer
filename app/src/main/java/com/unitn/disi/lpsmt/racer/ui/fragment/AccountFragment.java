package com.unitn.disi.lpsmt.racer.ui.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
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

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.racer.R;

import org.apache.commons.lang3.tuple.Pair;
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
import com.unitn.disi.lpsmt.racer.util.InputUtil;

import java.io.File;
import java.net.URI;
import java.text.DateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Observable;
import java.time.LocalDate;
import java.util.Observer;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
     * {@link Log} TAG of this class
     */
    private static final String TAG = AccountFragment.class.getName();
    /**
     * {@link User} avatar image max size in KB
     */
    private static final int AVATAR_MAX_SIZE_KB = 2048;
    /**
     * {@link User} avatar image max dimension in pixel
     */
    private static final Pair<Integer, Integer> AVATAR_DIMENSIONS = Pair.of(512, 512);

    /**
     * {@link TextView} {@link User} {@link UUID id}
     */
    private TextView txtId;
    /**
     * {@link TextView} {@link User} username
     */
    private TextView txtUsername;
    /**
     * {@link TextView} {@link User} creation {@link LocalDate date}
     */
    private TextView txtCreatedAt;
    /**
     * {@link EditText} {@link User} new password
     */
    private EditText inputPassword;
    /**
     * {@link EditText} {@link User} new password repeat
     */
    private EditText inputPasswordRepeat;
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
     * {@link ImageView Upload} {@link User} avatar
     */
    private ImageView inputAvatar;
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
     * {@link User} password {@link View Container}
     */
    private View passwordContainerView;
    /**
     * {@link User} password repeat {@link View Container}
     */
    private View passwordRepeatContainerView;
    /**
     * {@link UserObserver User Observer} {@link Observable}
     */
    private Observable userObservable;
    /**
     * Flag used to check if the {@link User} is in update mode
     */
    private boolean isUpdateMode = false;
    /**
     * The {@link File} reference of the new {@link User} avatar
     */
    private File fileAvatar = null;

    @NotNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        accountContainerView = root.findViewById(R.id.fragment_account_container);
        passwordContainerView = root.findViewById(R.id.fragment_account_password_container);
        passwordRepeatContainerView = root.findViewById(R.id.fragment_account_password_repeat_container);
        swipeRefreshLayout = root.findViewById(R.id.fragment_account_swipe_refresh);
        buttonEdit = root.findViewById(R.id.fragment_account_button_edit);
        buttonCancel = root.findViewById(R.id.fragment_account_button_cancel);
        imageAvatar = root.findViewById(R.id.fragment_account_avatar);
        inputAvatar = root.findViewById(R.id.fragment_account_avatar_input);
        txtId = root.findViewById(R.id.fragment_account_id);
        txtUsername = root.findViewById(R.id.fragment_account_username);
        txtCreatedAt = root.findViewById(R.id.fragment_account_created_at);
        inputPassword = root.findViewById(R.id.fragment_account_password_input);
        inputPasswordRepeat = root.findViewById(R.id.fragment_account_password_repeat_input);
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
        imageAvatar.setTag(user.avatar);
        txtId.setText(user.id.toString());
        txtUsername.setText(user.username);
        txtCreatedAt.setText(user.createdAt.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault())));
        if (user.name != null)
            txtName.setText(user.name);
        if (user.surname != null)
            txtSurname.setText(user.surname);
        txtRole.setText(user.role.getValueRes());
        txtGender.setText(user.gender.getValueRes());
        txtGender.setTag(user.gender);
        if (user.dateOfBirth != null)
            txtDateOfBirth.setText(user.dateOfBirth.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        txtDateOfBirth.setTag(user.dateOfBirth);
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

        if (user.favoriteCar != null)
            loadFavoriteCar(user.favoriteCar);
        if (user.favoriteCircuit != null)
            loadFavoriteCircuit(user.favoriteCircuit);
        if (user.hatedCircuit != null)
            loadHatedCircuit(user.hatedCircuit);

        swipeRefreshLayout.setRefreshing(false);
        isUpdateMode = false;
        setEditActionsVisibility();
        accountContainerView.setVisibility(View.VISIBLE);
        buttonEdit.setVisibility(View.VISIBLE);
    }

    /**
     * Set {@link User} edit actions and resets
     */
    private void setEditActions() {
        final User user = new User();
        final CarDialog dialogFavoriteCar = new CarDialog();
        final CircuitDialog dialogFavoriteCircuit = new CircuitDialog();
        final CircuitDialog dialogHatedCircuit = new CircuitDialog();

        buttonEdit.setOnClickListener(v -> {
            if (swipeRefreshLayout.isRefreshing()) return;

            isUpdateMode = !isUpdateMode;

            if (isUpdateMode) {
                // Save mode
                buttonEdit.setImageResource(R.drawable.ic_save);
                buttonCancel.setVisibility(View.VISIBLE);
                user.password = null;
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
                user.dateOfBirth = (LocalDate) txtDateOfBirth.getTag();
                inputResidence.setText(txtResidence.getText().toString());
                inputFavoriteNumber.setText(txtFavoriteNumber.getText().toString());
                inputFavoriteCar.setText(txtFavoriteCar.getText().toString());
                user.favoriteCar = (Long) txtFavoriteCar.getTag();
                dialogFavoriteCar.setCurrentCarForced((Long) txtFavoriteCar.getTag());
                inputFavoriteCircuit.setText(txtFavoriteCircuit.getText().toString());
                user.favoriteCircuit = (Long) txtFavoriteCircuit.getTag();
                dialogFavoriteCircuit.setCurrentCircuitForced((Long) txtFavoriteCircuit.getTag());
                inputHatedCircuit.setText(txtHatedCircuit.getText().toString());
                user.hatedCircuit = (Long) txtHatedCircuit.getTag();
                dialogHatedCircuit.setCurrentCircuitForced((Long) txtHatedCircuit.getTag());
            } else {
                // Save request
                user.password = !inputPassword.getText().toString().isEmpty() ? inputPassword.getText().toString() : null;
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

                if (isValidUser(user)) {
                    InputUtil.hideKeyboard(getActivity());
                    updateUserData(user);
                } else {
                    isUpdateMode = true;
                }
            }

            setEditActionsVisibility();
        });

        buttonCancel.setOnClickListener(v -> {
            InputUtil.hideKeyboard(requireActivity());
            user.reset();
            isUpdateMode = false;
            fileAvatar = null;
            Picasso.get().load(imageAvatar.getTag().toString()).into(imageAvatar);
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

        // Upload avatar image
        inputAvatar.setOnClickListener(v -> ImagePicker
                .Companion
                .with(this)
                .cropSquare()
                .compress(AVATAR_MAX_SIZE_KB)
                .maxResultSize(AVATAR_DIMENSIONS.getLeft(), AVATAR_DIMENSIONS.getRight())
                .start());
    }

    /**
     * Set {@link User} edit {@link View views} visibility
     */
    private void setEditActionsVisibility() {
        if (swipeRefreshLayout.isRefreshing()) return;
        final int visibilityText = isUpdateMode ? View.GONE : View.VISIBLE;
        final int visibilityInput = isUpdateMode ? View.VISIBLE : View.GONE;

        inputAvatar.setVisibility(visibilityInput == View.VISIBLE ? visibilityInput : View.INVISIBLE);
        passwordContainerView.setVisibility(visibilityInput);
        passwordRepeatContainerView.setVisibility(visibilityInput);
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
        if (visibilityInput == View.VISIBLE) {
            txtFavoriteCar.setVisibility(visibilityText);
            progressFavoriteCar.setVisibility(visibilityText);
        } else if (progressFavoriteCar.getVisibility() == View.VISIBLE) {
            progressFavoriteCar.setVisibility(visibilityText);
        } else {
            txtFavoriteCar.setVisibility(visibilityText);
        }
        inputFavoriteCar.setVisibility(visibilityInput);
        if (visibilityInput == View.VISIBLE) {
            txtFavoriteCircuit.setVisibility(visibilityText);
            progressFavoriteCircuit.setVisibility(visibilityText);
        } else if (progressFavoriteCircuit.getVisibility() == View.VISIBLE) {
            progressFavoriteCircuit.setVisibility(visibilityText);
        } else {
            txtFavoriteCircuit.setVisibility(visibilityText);
        }
        inputFavoriteCircuit.setVisibility(visibilityInput);
        if (visibilityInput == View.VISIBLE) {
            txtHatedCircuit.setVisibility(visibilityText);
            progressHatedCircuit.setVisibility(visibilityText);
        } else if (progressHatedCircuit.getVisibility() == View.VISIBLE) {
            progressHatedCircuit.setVisibility(visibilityText);
        } else {
            txtHatedCircuit.setVisibility(visibilityText);
        }
        inputHatedCircuit.setVisibility(visibilityInput);
    }

    /**
     * Update the current authenticated {@link User} with the given user
     *
     * @param user The current authenticated {@link User} data to update
     */
    private void updateUserData(final User user) {
        if (user == null || swipeRefreshLayout.isRefreshing()) return;
        swipeRefreshLayout.setRefreshing(true);

        API.getInstance().getClient().create(UserService.class).update(user).enqueue(new Callback<API.Response>() {
            @Override
            public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (fileAvatar != null) {
                        updateUserAvatar(fileAvatar);
                    } else {
                        Toasty.success(requireContext(), R.string.success_update).show();
                        buttonEdit.setImageResource(R.drawable.ic_edit);
                        buttonCancel.setVisibility(View.GONE);
                        UserObserver.getInstance().refreshUser();
                    }
                } else if (response.errorBody() != null) {
                    swipeRefreshLayout.setRefreshing(false);
                    isUpdateMode = true;
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
                    swipeRefreshLayout.setRefreshing(false);
                    isUpdateMode = true;
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to update User data due to " + t.getMessage(), t);
                swipeRefreshLayout.setRefreshing(false);
                isUpdateMode = true;
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    /**
     * Update the current {@link User} avatar image with the given avatar {@link File}.
     * This method can be called only from updateUserData.
     *
     * @param avatar The avatar {@link File} to change to
     */
    private void updateUserAvatar(final File avatar) {
        if (avatar == null || !swipeRefreshLayout.isRefreshing()) return;
        MultipartBody.Part avatarPart = MultipartBody.Part.createFormData("image", fileAvatar.getName(), RequestBody.create(avatar, MediaType.parse("image/*")));

        API.getInstance().getClient().create(UserService.class).updateAvatar(avatarPart).enqueue(new Callback<API.Response<URI>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<URI>> call, @NotNull Response<API.Response<URI>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toasty.success(requireContext(), R.string.success_update).show();
                    fileAvatar = null;
                    buttonEdit.setImageResource(R.drawable.ic_edit);
                    buttonCancel.setVisibility(View.GONE);
                    UserObserver.getInstance().refreshUser();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    isUpdateMode = true;
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<URI>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to update User avatar due to " + t.getMessage(), t);
                swipeRefreshLayout.setRefreshing(false);
                isUpdateMode = true;
                Toasty.error(requireContext(), R.string.error_unknown).show();
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

    /**
     * Validate the given user and show warning message if not
     *
     * @param user The {@link User} to validate
     * @return True if user is valid, false otherwise
     */
    private boolean isValidUser(final User user) {
        if (user.password != null && user.password.isEmpty()) {
            Toasty.warning(requireContext(), R.string.warning_empty_password).show();
            return false;
        }
        if (user.password != null && user.password.length() < User.PASSWORD_MIN_LENGTH) {
            Toasty.warning(requireContext(), R.string.warning_password_min_length).show();
            return false;
        }
        if (user.password != null && !user.password.equals(inputPasswordRepeat.getText().toString())) {
            Toasty.warning(requireContext(), R.string.warning_password_mismatch).show();
            return false;
        }
        if (user.name.isEmpty()) {
            Toasty.warning(requireContext(), R.string.warning_empty_name).show();
            return false;
        }
        if (user.surname.isEmpty()) {
            Toasty.warning(requireContext(), R.string.warning_empty_surname).show();
            return false;
        }
        if (user.dateOfBirth == null) {
            Toasty.warning(requireContext(), R.string.warning_empty_date_of_birth).show();
            return false;
        }
        if (user.residence.isEmpty()) {
            Toasty.warning(requireContext(), R.string.warning_empty_residence).show();
            return false;
        }
        if (user.favoriteNumber == null) {
            Toasty.warning(requireContext(), R.string.warning_empty_favorite_number).show();
            return false;
        }
        if (user.favoriteCar == null) {
            Toasty.warning(requireContext(), R.string.warning_empty_favorite_car).show();
            return false;
        }
        if (user.favoriteCircuit == null) {
            Toasty.warning(requireContext(), R.string.warning_empty_favorite_circuit).show();
            return false;
        }
        if (user.hatedCircuit == null) {
            Toasty.warning(requireContext(), R.string.warning_empty_hated_circuit).show();
            return false;
        }
        if (user.favoriteCircuit.equals(user.hatedCircuit)) {
            Toasty.warning(requireContext(), R.string.warning_favorite_hated_circuits_equals).show();
            return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri avatarUri;

        if (resultCode == Activity.RESULT_OK && data != null && (avatarUri = data.getData()) != null) {
            Log.d(TAG, "Image picked successfully");
            fileAvatar = avatarUri.getPath() != null ? new File(avatarUri.getPath()) : null;
            imageAvatar.setImageURI(avatarUri);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Log.e(TAG, "Error picking the image due to " + ImagePicker.Companion.getError(data));
            Toasty.error(requireContext(), ImagePicker.Companion.getError(data)).show();
        } else {
            Log.i(TAG, "Image picker task cancelled");
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        accountContainerView.setVisibility(View.GONE);
        passwordContainerView.setVisibility(View.GONE);
        passwordRepeatContainerView.setVisibility(View.GONE);
        buttonCancel.setVisibility(View.GONE);
        buttonEdit.setVisibility(View.GONE);
        buttonEdit.setImageResource(R.drawable.ic_edit);
        UserObserver.getInstance().refreshUser();
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
}
