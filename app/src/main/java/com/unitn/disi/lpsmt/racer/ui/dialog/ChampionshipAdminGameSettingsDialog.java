package com.unitn.disi.lpsmt.racer.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;
import com.unitn.disi.lpsmt.racer.api.entity.ChampionshipGameSetting;
import com.unitn.disi.lpsmt.racer.api.entity.GameSetting;
import com.unitn.disi.lpsmt.racer.api.entity.UserChampionship;
import com.unitn.disi.lpsmt.racer.api.service.ChampionshipGameSettingService;
import com.unitn.disi.lpsmt.racer.api.service.ChampionshipService;
import com.unitn.disi.lpsmt.racer.api.service.GameSettingService;
import com.unitn.disi.lpsmt.racer.api.service.UserChampionshipService;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;
import com.unitn.disi.lpsmt.racer.ui.component.dialog.OnDialogSelectionInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class ChampionshipAdminGameSettingsDialog extends DialogFragment {
    /**
     * {@link Log} TAG of this class
     */
    public static final String TAG = ChampionshipSubscribeDialog.class.getName();

    /**
     * {@link DialogFragment} {@link Toolbar}
     */
    private Toolbar toolbar;

    private Spinner spinnerChampionships;

    private Spinner spinnerGameSettings;

    private EditText inputValue;

    private Button buttonSave;

    /**
     * Listener when save button has been clicked with the saved {@link ChampionshipGameSetting}
     */
    private OnDialogSelectionInterface<ChampionshipGameSetting> listener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_admin_game_settings_dialog, container, false);

        toolbar = root.findViewById(R.id.fragment_admin_game_settings_dialog_subscribe_toolbar);
        spinnerChampionships = root.findViewById(R.id.fragment_admin_game_settings_dialog_championship_spinner);
        spinnerGameSettings = root.findViewById(R.id.fragment_admin_game_settings_dialog_game_settings);
        inputValue = root.findViewById(R.id.fragment_admin_game_settings_input);
        buttonSave = root.findViewById(R.id.fragment_admin_game_settings_button_save);

        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle(R.string.subscribe);
        if (toolbar.getNavigationIcon() != null)
            toolbar.getNavigationIcon().setTint(Color.WHITE);
        toolbar.setOnMenuItemClickListener(item -> {
            dismiss();
            return true;
        });

        spinnerChampionships.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Championship championship = (Championship) spinnerChampionships.getItemAtPosition(position);

                spinnerGameSettings.setEnabled(false);
                inputValue.setEnabled(false);
                buttonSave.setEnabled(false);
                loadGameSettings(championship.id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerGameSettings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final GameSettingDescriptor gameSettingDescriptor = (GameSettingDescriptor) spinnerGameSettings.getItemAtPosition(position);

                inputValue.setText(gameSettingDescriptor.championshipGameSetting.value);
                inputValue.setEnabled(true);
                buttonSave.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonSave.setOnClickListener(v -> {
            saveChanges((GameSettingDescriptor) spinnerGameSettings.getSelectedItem());
        });

        loadChampionships();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    private void loadChampionships() {
        API.getInstance().getAdminClient().create(ChampionshipService.class).find().enqueue(new Callback<API.Response<List<Championship>>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<List<Championship>>> call, @NotNull Response<API.Response<List<Championship>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final ArrayAdapter<Championship> championshipArrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, response.body().data);
                    championshipArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerChampionships.setEnabled(true);
                    spinnerChampionships.setAdapter(championshipArrayAdapter);
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<List<Championship>>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to find Championships due to " + t.getMessage(), t);
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    private void loadGameSettings(final long championshipId) {
        API.getInstance().getAdminClient().create(ChampionshipGameSettingService.class).findByChampionship(championshipId).enqueue(new Callback<API.Response<List<ChampionshipGameSetting>>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<List<ChampionshipGameSetting>>> call, @NotNull Response<API.Response<List<ChampionshipGameSetting>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final List<ChampionshipGameSetting> championshipGameSettings = response.body().data;

                    API.getInstance().getAdminClient().create(GameSettingService.class).findByChampionship(championshipId).enqueue(new Callback<API.Response<List<GameSetting>>>() {
                        @Override
                        public void onResponse(@NotNull Call<API.Response<List<GameSetting>>> call, @NotNull Response<API.Response<List<GameSetting>>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                final List<GameSetting> gameSettings = response.body().data;
                                final List<GameSettingDescriptor> gameSettingDescriptors = new ArrayList<>(championshipGameSettings.size());

                                for (ChampionshipGameSetting championshipGameSetting : championshipGameSettings) {
                                    for (GameSetting gameSetting : gameSettings) {
                                        if (!championshipGameSetting.gameSetting.equals(gameSetting.id))
                                            continue;
                                        gameSettingDescriptors.add(new GameSettingDescriptor(championshipGameSetting, gameSetting));

                                        break;
                                    }
                                }

                                final ArrayAdapter<GameSettingDescriptor> gameSettingDescriptorArrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, gameSettingDescriptors);
                                gameSettingDescriptorArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerGameSettings.setEnabled(true);
                                spinnerGameSettings.setAdapter(gameSettingDescriptorArrayAdapter);
                            } else {
                                Toasty.error(requireContext(), R.string.error_unknown).show();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<API.Response<List<GameSetting>>> call, @NotNull Throwable t) {
                            Log.e(TAG, "Unable to find Game Settings due to " + t.getMessage(), t);
                            ErrorHelper.showFailureError(getContext(), t);
                        }
                    });
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<List<ChampionshipGameSetting>>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to find Championship Game Settings due to " + t.getMessage(), t);
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    private void saveChanges(final GameSettingDescriptor gameSettingDescriptor) {
        if (gameSettingDescriptor == null) return;

        final ChampionshipGameSetting updateChampionshipGameSetting = new ChampionshipGameSetting();
        updateChampionshipGameSetting.value = inputValue.getText().toString();

        API.getInstance().getAdminClient().create(ChampionshipGameSettingService.class).update(gameSettingDescriptor.championshipGameSetting.championship, gameSettingDescriptor.championshipGameSetting.gameSetting, updateChampionshipGameSetting).enqueue(new Callback<API.Response>() {
            @Override
            public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (listener != null) listener.onDialogSelection(updateChampionshipGameSetting);
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to update Championship Game Settings due to " + t.getMessage(), t);
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    /**
     * Set the listener called when the positive button has been clicked with the saved {@link ChampionshipGameSetting}
     *
     * @param listener Listener when positive button has been clicked with the selected {@link ChampionshipGameSetting}
     */
    public void setOnDialogSelectionListener(OnDialogSelectionInterface<ChampionshipGameSetting> listener) {
        this.listener = listener;
    }

    private static class GameSettingDescriptor {
        private final ChampionshipGameSetting championshipGameSetting;
        private final GameSetting gameSetting;

        public GameSettingDescriptor(final ChampionshipGameSetting championshipGameSetting, final GameSetting gameSetting) {
            this.championshipGameSetting = championshipGameSetting;
            this.gameSetting = gameSetting;
        }

        public ChampionshipGameSetting getChampionshipGameSetting() {
            return championshipGameSetting;
        }

        public GameSetting getGameSetting() {
            return gameSetting;
        }

        @NonNull
        @Override
        public String toString() {
            return String.format(Locale.getDefault(), "%d - %s", gameSetting.id, gameSetting.name);
        }
    }
}
