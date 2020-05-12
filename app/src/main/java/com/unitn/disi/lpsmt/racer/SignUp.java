package com.unitn.disi.lpsmt.racer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.ui.component.DialogCar;
import com.unitn.disi.lpsmt.racer.ui.component.DialogCircuit;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

public final class SignUp extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        User user = new User();

        EditText inputUsername = findViewById(R.id.sign_up_input_username);
        EditText inputEmail = findViewById(R.id.sign_up_input_email);
        EditText inputPassword = findViewById(R.id.sign_up_input_password);
        EditText inputPasswordRepeat = findViewById(R.id.sign_up_input_password_repeat);
        EditText inputName = findViewById(R.id.sign_up_input_name);
        EditText inputSurname = findViewById(R.id.sign_up_input_surname);
        Spinner spinnerGender = findViewById(R.id.sign_up_spinner_gender);
        spinnerGender.setSelection(0);
        EditText inputDateOfBirth = findViewById(R.id.sign_up_input_date_of_birth);
        EditText inputFavoriteNumber = findViewById(R.id.sign_up_input_favorite_number);
        TextView inputFavoriteCar = findViewById(R.id.sign_up_input_favorite_car);
        DialogCar dialogFavoriteCar = new DialogCar();
        TextView inputFavoriteCircuit = findViewById(R.id.sign_up_input_favorite_circuit);
        DialogCircuit dialogFavoriteCircuit = new DialogCircuit();
        TextView inputHatedCircuit = findViewById(R.id.sign_up_input_hated_circuit);
        DialogCircuit dialogHatedCircuit = new DialogCircuit();
        Button buttonSignUp = findViewById(R.id.sign_up_button_sign_up);

        inputDateOfBirth.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(v.getContext(), (view, year, month, dayOfMonth) -> {
                Calendar dateOfBirth = Calendar.getInstance();
                dateOfBirth.set(year, month, dayOfMonth);
                user.dateOfBirth = LocalDate.of(year, month, dayOfMonth);
                inputDateOfBirth.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateOfBirth.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        inputFavoriteCar.setOnClickListener(v -> dialogFavoriteCar.show(getSupportFragmentManager(), DialogCar.class.getName()));
        dialogFavoriteCar.setOnDialogSelectionListener(car -> {
            if (car == null) return;
            inputFavoriteCar.setText(car.getFullName());
            user.favoriteCar = car.id;
        });

        inputFavoriteCircuit.setOnClickListener(v -> dialogFavoriteCircuit.show(getSupportFragmentManager(), DialogCircuit.class.getName()));
        dialogFavoriteCircuit.setOnDialogSelectionListener(circuit -> {
            if (circuit == null) return;
            inputFavoriteCircuit.setText(circuit.name);
            user.favoriteCircuit = circuit.id;
        });

        inputHatedCircuit.setOnClickListener(v -> dialogHatedCircuit.show(getSupportFragmentManager(), DialogCircuit.class.getName()));
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
            user.favoriteNumber = !inputFavoriteNumber.getText().toString().isEmpty() ? Short.valueOf(inputFavoriteNumber.getText().toString()) : null;

            System.out.println(user.dateOfBirth.toString());
        });
    }
}
