package com.unitn.disi.lpsmt.racer.ui.fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.auth0.android.jwt.JWT;
import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.AuthManager;
import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.api.service.UserService;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;
import com.unitn.disi.lpsmt.racer.ui.activity.MainActivity;
import com.unitn.disi.lpsmt.racer.ui.dialog.ChampionshipAdminCircuitsDialog;
import com.unitn.disi.lpsmt.racer.ui.dialog.ChampionshipAdminGameSettingsDialog;
import com.unitn.disi.lpsmt.racer.ui.dialog.ChampionshipAdminUsersDialog;
import com.unitn.disi.lpsmt.racer.util.InputUtil;

import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Administrator panel {@link Fragment}
 *
 * @author Carlo Corradini
 */
public final class AdminPanelFragment extends Fragment {

    /**
     * Notification id intent put extra
     */
    private static final String FROM_NOTIFICATION_ID = "FROM_NOTIFICATION_ID";

    /**
     * Notification channel id used for Game Setting update
     */
    private static final String NOTIFICATION_CHANNEL_ID_GAME_SETTING = "notification_channel_game_setting";

    /**
     * Notification channel id used for Circuit update
     */
    private static final String NOTIFICATION_CHANNEL_ID_CIRCUIT = "notification_channel_circuit";

    /**
     * Notification channel id used for User update
     */
    private static final String NOTIFICATION_CHANNEL_ID_USER = "notification_channel_user";

    /**
     * Notification id counter so there will be non equals notification ids
     */
    private static int NOTIFICATION_ID_COUNTER = 1;

    /**
     * Sing In {@link View container}
     */
    private View signInContainer;

    /**
     * Allowed Action {@link View container}
     */
    private View actionContainer;

    @NotNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_admin_panel, container, false);

        final User user = new User();
        final ChampionshipAdminGameSettingsDialog gameSettingsDialog = new ChampionshipAdminGameSettingsDialog();
        final ChampionshipAdminCircuitsDialog circuitsDialog = new ChampionshipAdminCircuitsDialog();
        final ChampionshipAdminUsersDialog usersDialog = new ChampionshipAdminUsersDialog();

        signInContainer = root.findViewById(R.id.fragment_admin_panel_sign_in_container);
        actionContainer = root.findViewById(R.id.fragment_admin_panel_action_container);
        EditText inputUsername = root.findViewById(R.id.fragment_admin_panel_sign_in_input_username);
        EditText inputPassword = root.findViewById(R.id.fragment_admin_panel_sign_in_input_password);
        Button buttonSignIn = root.findViewById(R.id.fragment_admin_panel_sign_in_button_sign_in);
        Button buttonActionGameSettings = root.findViewById(R.id.fragment_admin_panel_button_game_settings);
        Button buttonActionCircuits = root.findViewById(R.id.fragment_admin_panel_button_circuits);
        Button buttonActionUsers = root.findViewById(R.id.fragment_admin_panel_button_users);

        buttonSignIn.setOnClickListener(v -> {
            user.username = inputUsername.getText().toString();
            user.password = inputPassword.getText().toString();

            if (isValidUser(user)) {
                InputUtil.hideKeyboard(getActivity());
                doSignIn(user);
            }
        });

        buttonActionGameSettings.setOnClickListener(v -> {
            if (gameSettingsDialog.isAdded()) return;
            gameSettingsDialog.show(getParentFragmentManager(), ChampionshipAdminGameSettingsDialog.class.getName());
        });
        gameSettingsDialog.setOnDialogSelectionListener(update -> {
            gameSettingsDialog.dismiss();

            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(FROM_NOTIFICATION_ID, 1);
            intent.putExtra("CHAMPIONSHIP_ID", update.getLeft().getChampionshipGameSetting().championship);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID_GAME_SETTING)
                    .setSmallIcon(R.drawable.ic_settings_color)
                    .setContentTitle(getString(R.string.update_notification_title))
                    .setContentText(getString(R.string.update_notification_game_setting))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(String.format(Locale.getDefault(), "Championship [%d] -> Game Setting [%d - %s] updated from %s to %s", update.getLeft().getChampionshipGameSetting().championship, update.getLeft().getGameSetting().id, update.getLeft().getGameSetting().name, update.getLeft().getChampionshipGameSetting().value, update.getRight().value)))
                    .setContentIntent(PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setAutoCancel(true);
            createNotificationChannel(NOTIFICATION_CHANNEL_ID_GAME_SETTING);
            NotificationManagerCompat.from(requireContext()).notify(NOTIFICATION_ID_COUNTER++, builder.build());

            Toasty.success(requireContext(), R.string.update_success).show();
        });

        buttonActionCircuits.setOnClickListener(v -> {
            if (circuitsDialog.isAdded()) return;
            circuitsDialog.show(getParentFragmentManager(), ChampionshipAdminCircuitsDialog.class.getName());
        });
        circuitsDialog.setOnDialogSelectionListener(update -> {
            circuitsDialog.dismiss();

            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(FROM_NOTIFICATION_ID, 2);
            intent.putExtra("CHAMPIONSHIP_ID", update.getLeft().getChampionshipCircuit().championship);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID_GAME_SETTING)
                    .setSmallIcon(R.drawable.ic_circuit)
                    .setContentTitle(getString(R.string.update_notification_title))
                    .setContentText(getString(R.string.update_notification_circuit))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(String.format(Locale.getDefault(), "Championship [%d] -> Circuit [%d - %s] date update from %s to %s", update.getLeft().getChampionshipCircuit().championship, update.getLeft().getCircuit().id, update.getLeft().getCircuit().name, update.getLeft().getChampionshipCircuit().date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)), update.getRight().date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))))
                    .setContentIntent(PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setAutoCancel(true);
            createNotificationChannel(NOTIFICATION_CHANNEL_ID_CIRCUIT);
            NotificationManagerCompat.from(requireContext()).notify(NOTIFICATION_ID_COUNTER++, builder.build());

            Toasty.success(requireContext(), R.string.update_success).show();
        });

        buttonActionUsers.setOnClickListener(v -> {
            if(usersDialog.isAdded()) return;
            usersDialog.show(getParentFragmentManager(), ChampionshipAdminUsersDialog.class.getName());
        });
        usersDialog.setOnDialogSelectionListener(update -> {
            usersDialog.dismiss();

            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(FROM_NOTIFICATION_ID, 2);
            intent.putExtra("CHAMPIONSHIP_ID", update.getLeft().getUserChampionship().championship);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID_GAME_SETTING)
                    .setSmallIcon(R.drawable.ic_user)
                    .setContentTitle(getString(R.string.update_notification_title))
                    .setContentText(getString(R.string.update_notification_user))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(String.format(Locale.getDefault(), "Championship [%d] -> User [%s - %s] points update from %d to %d", update.getLeft().getUserChampionship().championship, update.getLeft().getUser().username, update.getLeft().getUser().id, update.getLeft().getUserChampionship().points, update.getRight().points)))
                    .setContentIntent(PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setAutoCancel(true);
            createNotificationChannel(NOTIFICATION_CHANNEL_ID_USER);
            NotificationManagerCompat.from(requireContext()).notify(NOTIFICATION_ID_COUNTER++, builder.build());

            Toasty.success(requireContext(), R.string.update_success).show();
        });

        return root;
    }

    /**
     * Create the notification Channel only for Android version >= O
     *
     * @param channelId The channel id
     */
    private void createNotificationChannel(final String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Admin Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (AuthManager.getInstance().isAdmin() && AuthManager.getInstance().getAdminToken() == null)
            AuthManager.getInstance().setAdminToken(AuthManager.getInstance().getToken());

        if (AuthManager.getInstance().isAdmin() || AuthManager.getInstance().getAdminToken() != null) {
            signInContainer.setVisibility(View.GONE);
            actionContainer.setVisibility(View.VISIBLE);
        } else {
            signInContainer.setVisibility(View.VISIBLE);
            actionContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Perform the Sign In call
     *
     * @param user The {@link User} to authenticate
     * @see UserService#signIn(User)
     */
    private void doSignIn(final User user) {
        if (user == null) return;

        API.getInstance().getClient().create(UserService.class).signIn(user).enqueue(new Callback<API.Response<JWT>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<JWT>> call, @NotNull Response<API.Response<JWT>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (AuthManager.getUserRole(response.body().data) == User.Role.ADMIN) {
                        AuthManager.getInstance().setAdminToken(response.body().data);
                    } else {
                        Toasty.warning(requireContext(), R.string.warning_no_admin_role).show();
                    }
                } else {
                    Toasty.error(requireContext(), R.string.sign_in_unauthorized).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<JWT>> call, @NotNull Throwable t) {
                ErrorHelper.showFailureError(requireContext(), t);
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
            Toasty.warning(requireContext(), R.string.warning_empty_username).show();
            return false;
        }
        if (user.password.isEmpty()) {
            Toasty.warning(requireContext(), R.string.warning_empty_password).show();
            return false;
        }
        if (user.password.length() < User.PASSWORD_MIN_LENGTH) {
            Toasty.warning(requireContext(), R.string.warning_password_min_length).show();
            return false;
        }

        return true;
    }
}
