package com.unitn.disi.lpsmt.racer.ui.dialog;

import android.app.DatePickerDialog;
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

import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.ChampionshipCircuit;

import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;
import com.unitn.disi.lpsmt.racer.api.service.ChampionshipCircuitService;
import com.unitn.disi.lpsmt.racer.api.service.ChampionshipService;
import com.unitn.disi.lpsmt.racer.api.service.CircuitService;
import com.unitn.disi.lpsmt.racer.helper.ColorHelper;
import com.unitn.disi.lpsmt.racer.api.entity.Circuit;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;
import com.unitn.disi.lpsmt.racer.ui.component.dialog.OnDialogSelectionInterface;
import com.unitn.disi.lpsmt.racer.util.InputUtil;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * {@link ChampionshipCircuit} & {@link Circuit} {@link DialogFragment} descriptor
 *
 * @author Carlo Corradini
 */
public final class ChampionshipAdminCircuitsDialog extends DialogFragment implements SwipeRefreshLayout.OnRefreshListener {

    /**
     * {@link Log} TAG of this class
     */
    public static final String TAG = ChampionshipAdminCircuitsDialog.class.getName();

    /**
     * {@link DialogFragment} {@link Toolbar}
     */
    private Toolbar toolbar;

    /**
     * {@link Championship} {@link Spinner}
     */
    private Spinner spinnerChampionships;

    /**
     * {@link Circuit} {@link Spinner}
     */
    private Spinner spinnerCircuits;

    /**
     * {@link Circuit} input value
     */
    private EditText inputValue;

    /**
     * Save {@link Button}
     */
    private Button buttonSave;

    /**
     * {@link SwipeRefreshLayout} reference
     */
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * {@link LocalDate} value
     */
    private LocalDate date = null;

    /**
     * Listener when save button has been clicked with the saved {@link CircuitDescriptor}
     */
    private OnDialogSelectionInterface<Pair<CircuitDescriptor, ChampionshipCircuit>> listener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_admin_circuits_dialog, container, false);

        toolbar = root.findViewById(R.id.fragment_admin_circuits_dialog_subscribe_toolbar);
        swipeRefreshLayout = root.findViewById(R.id.fragment_admin_circuits_dialog_swipe_refresh);
        spinnerChampionships = root.findViewById(R.id.fragment_admin_circuits_dialog_championship_spinner);
        spinnerCircuits = root.findViewById(R.id.fragment_admin_circuits_dialog_circuits);
        inputValue = root.findViewById(R.id.fragment_admin_circuits_input);
        buttonSave = root.findViewById(R.id.fragment_admin_circuits_button_save);

        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle(R.string.circuits);
        if (toolbar.getNavigationIcon() != null)
            toolbar.getNavigationIcon().setTint(Color.WHITE);
        toolbar.setOnMenuItemClickListener(item -> {
            dismiss();
            return true;
        });

        swipeRefreshLayout.setColorSchemeColors(ColorHelper.COLOR_BLUE, ColorHelper.COLOR_RED, ColorHelper.COLOR_YELLOW, ColorHelper.COLOR_GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        spinnerChampionships.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Championship championship = (Championship) spinnerChampionships.getItemAtPosition(position);

                swipeRefreshLayout.setRefreshing(true);
                spinnerCircuits.setEnabled(false);
                inputValue.setEnabled(false);
                inputValue.setText("");
                buttonSave.setEnabled(false);
                loadCircuits(championship.id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCircuits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final CircuitDescriptor circuitDescriptor = (CircuitDescriptor) spinnerCircuits.getItemAtPosition(position);

                inputValue.setText(circuitDescriptor.championshipCircuit.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                inputValue.setEnabled(true);
                buttonSave.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        inputValue.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (_view, year, month, dayOfMonth) -> {
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.set(year, month, dayOfMonth);
                date = LocalDate.of(year, month + 1, dayOfMonth);
                inputValue.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateCalendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        });

        buttonSave.setOnClickListener(v -> {
            if (isValidUpdate((CircuitDescriptor) spinnerCircuits.getSelectedItem())) {
                InputUtil.hideKeyboard(requireActivity());
                saveChanges((CircuitDescriptor) spinnerCircuits.getSelectedItem());
            }
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

    /**
     * Load all available {@link Championship}
     */
    private void loadChampionships() {
        API.getInstance().getAdminClient().create(ChampionshipService.class).find().enqueue(new Callback<API.Response<List<Championship>>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<List<Championship>>> call, @NotNull Response<API.Response<List<Championship>>> response) {
                swipeRefreshLayout.setRefreshing(false);
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
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Load available {@link Circuit} given the championshipId
     *
     * @param championshipId The {@link Championship} id
     */
    private void loadCircuits(final long championshipId) {
        API.getInstance().getAdminClient().create(ChampionshipCircuitService.class).findByChampionship(championshipId).enqueue(new Callback<API.Response<List<ChampionshipCircuit>>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<List<ChampionshipCircuit>>> call, @NotNull Response<API.Response<List<ChampionshipCircuit>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final List<ChampionshipCircuit> championshipCircuits = response.body().data;

                    API.getInstance().getAdminClient().create(CircuitService.class).findByChampionship(championshipId).enqueue(new Callback<API.Response<List<Circuit>>>() {
                        @Override
                        public void onResponse(@NotNull Call<API.Response<List<Circuit>>> call, @NotNull Response<API.Response<List<Circuit>>> response) {
                            swipeRefreshLayout.setRefreshing(false);
                            if (response.isSuccessful() && response.body() != null) {
                                final List<Circuit> circuits = response.body().data;
                                final List<CircuitDescriptor> circuitDescriptors = new ArrayList<>(championshipCircuits.size());

                                for (ChampionshipCircuit championshipCircuit : championshipCircuits) {
                                    for (Circuit circuit : circuits) {
                                        if (!championshipCircuit.circuit.equals(circuit.id))
                                            continue;
                                        circuitDescriptors.add(new CircuitDescriptor(championshipCircuit, circuit));

                                        break;
                                    }
                                }

                                final ArrayAdapter<CircuitDescriptor> circuitDescriptorArrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, circuitDescriptors);
                                circuitDescriptorArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerCircuits.setEnabled(true);
                                spinnerCircuits.setAdapter(circuitDescriptorArrayAdapter);
                            } else {
                                Toasty.error(requireContext(), R.string.error_unknown).show();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<API.Response<List<Circuit>>> call, @NotNull Throwable t) {
                            Log.e(TAG, "Unable to find Circuits due to " + t.getMessage(), t);
                            ErrorHelper.showFailureError(getContext(), t);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<List<ChampionshipCircuit>>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to find Championship Circuits to " + t.getMessage(), t);
                ErrorHelper.showFailureError(getContext(), t);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Save changes {@link Circuit} value given the {@link CircuitDescriptor}
     *
     * @param circuitDescriptor The {@link CircuitDescriptor} to save information
     */
    private void saveChanges(final CircuitDescriptor circuitDescriptor) {
        if (circuitDescriptor == null || swipeRefreshLayout.isRefreshing() || date == null) return;
        swipeRefreshLayout.setRefreshing(true);

        final ChampionshipCircuit updateChampionshipCircuit = new ChampionshipCircuit();
        updateChampionshipCircuit.date = date;

        API.getInstance().getAdminClient().create(ChampionshipCircuitService.class).update(circuitDescriptor.championshipCircuit.championship, circuitDescriptor.championshipCircuit.circuit, updateChampionshipCircuit).enqueue(new Callback<API.Response>() {
            @Override
            public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    if (listener != null)
                        listener.onDialogSelection(Pair.of(circuitDescriptor, updateChampionshipCircuit));
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to update Championship Circuits due to " + t.getMessage(), t);
                ErrorHelper.showFailureError(getContext(), t);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Set the listener called when the positive button has been clicked with the saved {@link CircuitDescriptor}
     *
     * @param listener Listener when positive button has been clicked with the selected {@link CircuitDescriptor}
     */
    public void setOnDialogSelectionListener(OnDialogSelectionInterface<Pair<CircuitDescriptor, ChampionshipCircuit>> listener) {
        this.listener = listener;
    }

    /**
     * Check if the update is valid
     *
     * @param circuitDescriptor Current {@link CircuitDescriptor}
     * @return True if valid, false otherwise
     */
    private boolean isValidUpdate(final CircuitDescriptor circuitDescriptor) {
        if (date == null || inputValue.getText().toString().isEmpty()) {
            Toasty.warning(requireContext(), R.string.warning_empty_value).show();
            return false;
        }
        if (date.equals(circuitDescriptor.championshipCircuit.date)) {
            Toasty.warning(requireContext(), R.string.warning_same_value).show();
            return false;
        }

        return true;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * {@link ChampionshipCircuit} & {@link Circuit} class descriptor
     *
     * @author Carlo Corradini
     */
    public static class CircuitDescriptor {
        /**
         * {@link ChampionshipCircuit} reference
         */
        private final ChampionshipCircuit championshipCircuit;
        /**
         * {@link Circuit} reference
         */
        private final Circuit circuit;

        /**
         * Construct a {@link CircuitDescriptor} given the {@link ChampionshipCircuit} & {@link Circuit}
         *
         * @param championshipCircuit The {@link ChampionshipCircuit}
         * @param circuit             The {@link Circuit}
         */
        public CircuitDescriptor(final ChampionshipCircuit championshipCircuit, final Circuit circuit) {
            this.championshipCircuit = championshipCircuit;
            this.circuit = circuit;
        }

        /**
         * Return the {@link ChampionshipCircuit} reference
         *
         * @return {@link ChampionshipCircuit} reference
         */
        public ChampionshipCircuit getChampionshipCircuit() {
            return championshipCircuit;
        }

        /**
         * Return the {@link Circuit} reference
         *
         * @return {@link Circuit} reference
         */
        public Circuit getCircuit() {
            return circuit;
        }

        @NonNull
        @Override
        public String toString() {
            return String.format(Locale.getDefault(), "%d - %s -%s", circuit.id, circuit.name, championshipCircuit.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        }
    }
}
