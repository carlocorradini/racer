package com.unitn.disi.lpsmt.racer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.AuthManager;
import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.api.service.UserService;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignIn extends AppCompatActivity {
    private User user = new User();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        EditText inputUsername = findViewById(R.id.sign_in_input_username);
        EditText inputPassword = findViewById(R.id.sign_in_input_password);
        Button buttonSignIn = findViewById(R.id.sign_in_button_sign_in);
        Button buttonSignUp = findViewById(R.id.sign_in_button_sign_up);
        Button buttonForgotPassword = findViewById(R.id.sign_in_button_forgot_password);

        buttonSignUp.setOnClickListener(v -> startActivity(new Intent(v.getContext(), SignUp.class)));
        buttonForgotPassword.setOnClickListener(v -> startActivity(new Intent(v.getContext(), ForgotPassword.class)));

        buttonSignIn.setOnClickListener(v -> {
            user.username = inputUsername.getText().toString();
            user.password = inputPassword.getText().toString();
            signIn();
        });
    }

    private void signIn() {
        final Context context = this.getBaseContext();
        Call<API.Response<JWT>> call = API.getInstance().getClient().create(UserService.class).signIn(user);
        call.enqueue(new Callback<API.Response<JWT>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<JWT>> call, @NotNull Response<API.Response<JWT>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthManager.getInstance().setToken(response.body().data);
                    startActivity(new Intent(context, MainActivity.class));
                    finish();
                } else if (response.errorBody() != null && response.code() == HttpStatus.SC_UNAUTHORIZED) {

                } else {
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<JWT>> call, @NotNull Throwable t) {

            }
        });
    }
}
