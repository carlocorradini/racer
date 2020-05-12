package com.unitn.disi.lpsmt.racer;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.unitn.disi.lpsmt.racer.api.entity.User;

public final class SignUp extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        User user = new User();
        EditText inputUsername = findViewById(R.id.sign_up_input_username);
        EditText inputPassword = findViewById(R.id.sign_up_input_password);
        EditText inputPasswordRepeat = findViewById(R.id.sign_up_input_password_repeat);
        EditText inputName = findViewById(R.id.sign_up_input_name);
        EditText inputSurname = findViewById(R.id.sign_up_input_surname);
        Spinner spinnerGender = findViewById(R.id.sign_up_spinner_gender);
        spinnerGender.setAdapter(ArrayAdapter.createFromResource(this, R.array.user_genders_array, android.R.layout.simple_spinner_item));
        spinnerGender.setSelection(0);
    }
}
