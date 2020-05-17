package com.unitn.disi.lpsmt.racer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.AuthManager;
import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.api.service.UserService;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;
import com.unitn.disi.lpsmt.racer.util.InputUtil;

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

    /**
     * Loader
     */
    private FrameLayout loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final User user = new User();

        EditText inputUsername = findViewById(R.id.sign_in_input_username);
        EditText inputPassword = findViewById(R.id.sign_in_input_password);
        Button buttonSignIn = findViewById(R.id.sign_in_button_sign_in);
        Button buttonSignUp = findViewById(R.id.sign_in_button_sign_up);
        Button buttonForgotPassword = findViewById(R.id.sign_in_button_forgot_password);
        loader = findViewById(R.id.sign_in_loader);

        buttonSignUp.setOnClickListener(v -> startActivity(new Intent(v.getContext(), SignUp.class)));
        buttonForgotPassword.setOnClickListener(v -> startActivity(new Intent(v.getContext(), ForgotPassword.class)));
        buttonSignIn.setOnClickListener(v -> {
            user.username = inputUsername.getText().toString();
            user.password = inputPassword.getText().toString();

            if (isValidUser(user)) {
                InputUtil.hideKeyboard(this);
                doSignIn(user);
            }
        });

        checkAction();
    }

    /**
     * Perform the Sign In call
     *
     * @param user The {@link User} to authenticate
     * @see UserService#signIn(User)
     */
    private void doSignIn(final User user) {
        if (user == null) return;
        loader.setVisibility(View.VISIBLE);
        API.getInstance().getClient().create(UserService.class).signIn(user).enqueue(new Callback<API.Response<JWT>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<JWT>> call, @NotNull Response<API.Response<JWT>> response) {
                loader.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    AuthManager.getInstance().setToken(response.body().data);
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("ACTION_SIGN_IN", true);
                    startActivity(intent);
                    finish();
                } else {
                    Toasty.error(getBaseContext(), R.string.sign_in_unauthorized).show();
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
        if (user.password.isEmpty()) {
            Toasty.warning(getBaseContext(), R.string.warning_empty_password).show();
            return false;
        }
        if (user.password.length() < User.PASSWORD_MIN_LENGTH) {
            Toasty.warning(getBaseContext(), R.string.warning_password_min_length).show();
            return false;
        }

        return true;
    }

    /**
     * Check if the activity has a correlated action
     * and show the status to the user
     */
    private void checkAction() {
        if (getIntent().getBooleanExtra("ACTION_SIGN_OUT", false)) {
            // Sign Out action
            Toasty.success(this, R.string.sign_out_success).show();
        }
    }
}
