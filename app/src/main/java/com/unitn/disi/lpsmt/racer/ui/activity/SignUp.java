package com.unitn.disi.lpsmt.racer.ui.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.AuthManager;
import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.api.entity.error.ConflictError;
import com.unitn.disi.lpsmt.racer.api.entity.error.UnprocessableEntityError;
import com.unitn.disi.lpsmt.racer.api.service.UserService;
import com.unitn.disi.lpsmt.racer.filter.NumberMinMaxFilter;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;
import com.unitn.disi.lpsmt.racer.ui.component.dialog.CarDialog;
import com.unitn.disi.lpsmt.racer.ui.component.dialog.CircuitDialog;
import com.unitn.disi.lpsmt.racer.util.InputUtil;

import org.apache.http.HttpStatus;
import org.apache.commons.validator.routines.EmailValidator;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Sign Up activity
 *
 * @author Carlo Corradini
 */
public final class SignUp extends AppCompatActivity {

    /**
     * Input used for double password verification
     */
    private EditText inputPasswordRepeat;
    /**
     * Loader
     */
    private FrameLayout loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final User user = new User();

        EditText inputUsername = findViewById(R.id.sign_up_input_username);
        EditText inputEmail = findViewById(R.id.sign_up_input_email);
        EditText inputPassword = findViewById(R.id.sign_up_input_password);
        inputPasswordRepeat = findViewById(R.id.sign_up_input_password_repeat);
        EditText inputName = findViewById(R.id.sign_up_input_name);
        EditText inputSurname = findViewById(R.id.sign_up_input_surname);
        Spinner spinnerGender = findViewById(R.id.sign_up_spinner_gender);
        spinnerGender.setSelection(0);
        EditText inputDateOfBirth = findViewById(R.id.sign_up_input_date_of_birth);
        EditText inputResidence = findViewById(R.id.sign_up_input_residence);
        EditText inputFavoriteNumber = findViewById(R.id.sign_up_input_favorite_number);
        inputFavoriteNumber.setFilters(new InputFilter[]{new NumberMinMaxFilter(User.FAVORITE_NUMBER_MIN, User.FAVORITE_NUMBER_MAX)});
        EditText inputFavoriteCar = findViewById(R.id.sign_up_input_favorite_car);
        CarDialog dialogFavoriteCar = new CarDialog();
        EditText inputFavoriteCircuit = findViewById(R.id.sign_up_input_favorite_circuit);
        CircuitDialog dialogFavoriteCircuit = new CircuitDialog();
        EditText inputHatedCircuit = findViewById(R.id.sign_up_input_hated_circuit);
        CircuitDialog dialogHatedCircuit = new CircuitDialog();
        Button buttonSignUp = findViewById(R.id.sign_up_button_sign_up);
        loader = findViewById(R.id.sign_up_loader);

        inputDateOfBirth.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (view, year, month, dayOfMonth) -> {
                Calendar dateOfBirth = Calendar.getInstance();
                dateOfBirth.set(year, month, dayOfMonth);
                System.out.println(dateOfBirth.toString());
                user.dateOfBirth = LocalDate.of(year, month + 1, dayOfMonth);
                System.out.println(user.dateOfBirth
                );
                inputDateOfBirth.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateOfBirth.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        });

        inputFavoriteCar.setOnClickListener(v -> {
            if (!dialogFavoriteCar.isAdded())
                dialogFavoriteCar.show(getSupportFragmentManager(), CarDialog.class.getName());
        });
        dialogFavoriteCar.setOnDialogSelectionListener(car -> {
            if (car == null) return;
            inputFavoriteCar.setText(car.getFullName());
            user.favoriteCar = car.id;
        });

        inputFavoriteCircuit.setOnClickListener(v -> {
            if (!dialogFavoriteCircuit.isAdded())
                dialogFavoriteCircuit.show(getSupportFragmentManager(), CircuitDialog.class.getName());
        });
        dialogFavoriteCircuit.setOnDialogSelectionListener(circuit -> {
            if (circuit == null) return;
            inputFavoriteCircuit.setText(circuit.name);
            user.favoriteCircuit = circuit.id;
        });

        inputHatedCircuit.setOnClickListener(v -> {
            if (!dialogHatedCircuit.isAdded())
                dialogHatedCircuit.show(getSupportFragmentManager(), CircuitDialog.class.getName());
        });
        dialogHatedCircuit.setOnDialogSelectionListener(circuit -> {
            if (circuit == null) return;
            inputHatedCircuit.setText(circuit.name);
            user.hatedCircuit = circuit.id;
        });

        buttonSignUp.setOnClickListener(v -> {
            user.username = inputUsername.getText().toString();
            user.email = inputEmail.getText().toString();
            user.password = inputPassword.getText().toString();
            user.name = inputName.getText().toString();
            user.surname = inputSurname.getText().toString();
            switch (spinnerGender.getSelectedItemPosition()) {
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
            user.favoriteNumber = !inputFavoriteNumber.getText().toString().isEmpty() ? Short.valueOf(inputFavoriteNumber.getText().toString()) : null;

            if (isValidUser(user)) {
                InputUtil.hideKeyboard(this);
                doSignUp(user);
            }
        });
    }

    /**
     * Perform the Sign Up call
     *
     * @param user The {@link User} to create
     * @see UserService#create(User)
     */
    private void doSignUp(final User user) {
        if (user == null) return;
        loader.setVisibility(View.VISIBLE);
        API.getInstance().getClient().create(UserService.class).create(user).enqueue(new Callback<API.Response<JWT>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<JWT>> call, @NotNull Response<API.Response<JWT>> response) {
                loader.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    AuthManager.getInstance().setToken(response.body().data);
                    startActivity(new Intent(getBaseContext(), Home.class));
                    finish();
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
                            Toasty.warning(getBaseContext(), getResources().getString(R.string.warning_unprocessable_entity) + "\n" + entities).show();
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
                            Toasty.warning(getBaseContext(), getResources().getString(R.string.warning_conflict) + "\n" + conflicts).show();
                            break;
                        }
                        default: {
                            Toasty.warning(getBaseContext(), R.string.error_internal_server_error).show();
                            break;
                        }
                    }
                } else {
                    Toasty.error(getBaseContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<JWT>> call, @NotNull Throwable t) {
                loader.setVisibility(View.GONE);
                ErrorHelper.showFailureError(getBaseContext(), t);
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
            Toasty.warning(getBaseContext(), R.string.warning_empty_username).show();
            return false;
        }
        if (user.email.isEmpty()) {
            Toasty.warning(getBaseContext(), R.string.warning_empty_email).show();
            return false;
        }
        if (!EmailValidator.getInstance().isValid(user.email)) {
            Toasty.warning(getBaseContext(), R.string.warning_invalid_email).show();
            return false;
        }
        if (user.password.isEmpty()) {
            Toasty.warning(getBaseContext(), R.string.warning_empty_password).show();
            return false;
        }
        if (user.password.length() < User.PASSWORD_MIN_LENGTH) {
            Toasty.warning(getBaseContext(), R.string.warning_password_min_length).show();
            return false;
        }
        if (!user.password.equals(inputPasswordRepeat.getText().toString())) {
            Toasty.warning(getBaseContext(), R.string.warning_password_mismatch).show();
            return false;
        }
        if (user.name.isEmpty()) {
            Toasty.warning(getBaseContext(), R.string.warning_empty_name).show();
            return false;
        }
        if (user.surname.isEmpty()) {
            Toasty.warning(getBaseContext(), R.string.warning_empty_surname).show();
            return false;
        }
        if (user.dateOfBirth == null) {
            Toasty.warning(getBaseContext(), R.string.warning_empty_date_of_birth).show();
            return false;
        }
        if (user.residence.isEmpty()) {
            Toasty.warning(getBaseContext(), R.string.warning_empty_residence).show();
            return false;
        }
        if (user.favoriteNumber == null) {
            Toasty.warning(getBaseContext(), R.string.warning_empty_favorite_number).show();
            return false;
        }
        if (user.favoriteCar == null) {
            Toasty.warning(getBaseContext(), R.string.warning_empty_favorite_car).show();
            return false;
        }
        if (user.favoriteCircuit == null) {
            Toasty.warning(getBaseContext(), R.string.warning_empty_favorite_circuit).show();
            return false;
        }
        if (user.hatedCircuit == null) {
            Toasty.warning(getBaseContext(), R.string.warning_empty_hated_circuit).show();
            return false;
        }
        if (user.favoriteCircuit.equals(user.hatedCircuit)) {
            Toasty.warning(getBaseContext(), R.string.warning_favorite_hated_circuits_equals).show();
            return false;
        }

        return true;
    }
}
