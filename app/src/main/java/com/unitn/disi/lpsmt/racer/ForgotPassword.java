package com.unitn.disi.lpsmt.racer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.service.UserService;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;
import com.unitn.disi.lpsmt.racer.util.InputUtil;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Forgot Password Activity
 *
 * @author Carlo Corradini
 */
public final class ForgotPassword extends AppCompatActivity {

    /**
     * Loader
     */
    private FrameLayout loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        EditText inputEmail = findViewById(R.id.forgot_password_input_email);
        Button buttonSend = findViewById(R.id.forgot_password_button_send);
        loader = findViewById(R.id.forgot_password_loader);

        buttonSend.setOnClickListener(v -> {
            String email = inputEmail.getText().toString();

            if (isValidEmail(email)) {
                InputUtil.hideKeyboard(this);
                doForgotPassword(email);
            }
        });
    }

    /**
     * Perform the Password Reset call
     *
     * @param email The email to send the password reset link
     */
    private void doForgotPassword(final String email) {
        if (email == null) return;
        loader.setVisibility(View.VISIBLE);
        API.getInstance().getClient().create(UserService.class).passwordReset(email).enqueue(new Callback<API.Response>() {
            @Override
            public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                loader.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Toasty.success(getBaseContext(), R.string.recover_password_email_sent).show();
                } else if (response.errorBody() != null) {
                    if (response.code() == HttpStatus.SC_NOT_FOUND) {
                        Toasty.warning(getBaseContext(), R.string.warning_email_not_found).show();
                    } else {
                        Toasty.warning(getBaseContext(), R.string.error_internal_server_error).show();
                    }
                } else {
                    Toasty.error(getBaseContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                loader.setVisibility(View.GONE);
                ErrorHelper.showFailureError(getBaseContext(), t);
            }
        });
    }

    /**
     * Validate the given email and show warning message if not
     *
     * @param email The {@link String email} to validate
     * @return True if email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            Toasty.warning(getBaseContext(), R.string.warning_empty_email).show();
            return false;
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            Toasty.warning(getBaseContext(), R.string.warning_invalid_email).show();
            return false;
        }

        return true;
    }
}
