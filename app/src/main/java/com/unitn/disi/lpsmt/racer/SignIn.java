package com.unitn.disi.lpsmt.racer;

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
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;

import org.jetbrains.annotations.NotNull;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Sign In activity
 *
 * @author Carlo Corradini
 */
public final class SignIn extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        User user = new User();
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
            doSignIn(user);
        });
    }

    /**
     * Perform the Sign In call
     *
     * @param user The {@link User} to authenticate
     * @see UserService#signIn(User)
     */
    private void doSignIn(User user) {
        if (user == null) user = new User();
        API.getInstance().getClient().create(UserService.class).signIn(user).enqueue(new Callback<API.Response<JWT>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<JWT>> call, @NotNull Response<API.Response<JWT>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthManager.getInstance().setToken(response.body().data);
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                    finish();
                } else {
                    Toasty.error(getBaseContext(), R.string.sign_in_unauthorized).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<JWT>> call, @NotNull Throwable t) {
                ErrorHelper.showFailureError(getBaseContext(), t);
            }
        });
    }
}
