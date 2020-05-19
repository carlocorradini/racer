package com.unitn.disi.lpsmt.racer.ui.component.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.Circuit;
import com.unitn.disi.lpsmt.racer.api.service.CircuitService;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Circuit Dialog
 *
 * @author Carlo Corradini
 */
public final class CircuitDialog extends AppCompatDialogFragment {

    /**
     * Current selected {@link Circuit}
     */
    private Circuit currentCircuit = null;
    /**
     * Current {@link Circuit} force to set checked
     */
    private Long currentCircuitForced = null;
    /**
     * Old selected {@link Circuit circuit} if the {@link CircuitDialog} was close and the positive button has been clicked
     */
    private Circuit savedCircuit = null;
    /**
     * {@link List} of available {@link Circuit circuits}
     * The {@link List} is populated only once calling the {@link CircuitService} find method
     */
    private List<Circuit> circuits = new ArrayList<>();
    /**
     * {@link RadioButton Circuit Radio Button} {@link RadioGroup container}
     */
    private RadioGroup circuitsContainer;
    /**
     * Data container
     */
    private LinearLayout dataContainer;
    /**
     * {@link ProgressBar} loader
     */
    private ProgressBar loader;
    /**
     * Listener when positive button has been clicked with the selected {@link Circuit}
     */
    private OnDialogSelectionInterface<Circuit> listener = null;
    /**
     * Current {@link View} view
     */
    private View view = null;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        setRetainInstance(true);
        setCancelable(true);

        view = requireActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_circuit, null);

        EditText inputSearch = view.findViewById(R.id.fragment_dialog_circuit_input_search);
        circuitsContainer = view.findViewById(R.id.fragment_dialog_circuit_container);
        dataContainer = view.findViewById(R.id.fragment_dialog_circuit_data_container);
        loader = view.findViewById(R.id.fragment_dialog_circuit_loader);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                for (int i = 0; i < circuitsContainer.getChildCount(); i++) {
                    View v = circuitsContainer.getChildAt(i);
                    RadioButton circuitRadioButton = v instanceof RadioButton ? (RadioButton) v : null;

                    if (circuitRadioButton != null
                            && circuitRadioButton.getText().toString().toLowerCase().contains(s.toString().toLowerCase())) {
                        circuitRadioButton.setVisibility(View.VISIBLE);
                    } else if (circuitRadioButton != null) {
                        circuitRadioButton.setVisibility(View.GONE);
                    }
                }
            }
        });
        circuitsContainer.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton circuitRadioButton = group.findViewById(checkedId);
            int index = (int) circuitRadioButton.getTag();
            currentCircuit = index >= circuits.size() ? null : circuits.get(index);
        });

        loadCircuits();

        return new AlertDialog.Builder(requireActivity())
                .setView(view)
                .setTitle(R.string.choose_circuit)
                .setNegativeButton(R.string.dismiss, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    savedCircuit = currentCircuit;
                    if (currentCircuit != null && listener != null)
                        listener.onDialogSelection(currentCircuit);
                })
                .create();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    /**
     * Load all available {@link Circuit circuits} calling the {@link CircuitService} find method
     * If the circuits list is not empty no call will be generated
     */
    private void loadCircuits() {
        if (!circuits.isEmpty()) showCircuits();
        else
            API.getInstance().getClient().create(CircuitService.class).find().enqueue(new Callback<API.Response<List<Circuit>>>() {
                @Override
                public void onResponse(@NotNull Call<API.Response<List<Circuit>>> call, @NotNull Response<API.Response<List<Circuit>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        circuits = response.body().data;
                        showCircuits();
                    } else {
                        Toasty.error(view.getContext(), R.string.error_unknown).show();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<API.Response<List<Circuit>>> call, @NotNull Throwable t) {
                    ErrorHelper.showFailureError(view.getContext(), t);
                }
            });
    }

    /**
     * Show the available circuits in the {@link List} of circuits into the circuitsContainer
     * After hide the loader and show the dataContainer
     */
    private void showCircuits() {
        for (int i = 0; i < circuits.size(); ++i) {
            RadioButton circuitRadioButton = new RadioButton(getContext());
            circuitRadioButton.setText(circuits.get(i).name);
            circuitRadioButton.setTag(i);
            circuitsContainer.addView(circuitRadioButton);
            if (savedCircuit != null && savedCircuit.equals(circuits.get(i)))
                circuitRadioButton.setChecked(true);
            else if (currentCircuitForced != null && currentCircuitForced.equals(circuits.get(i).id)) {
                savedCircuit = circuits.get(i);
                currentCircuitForced = null;
                circuitRadioButton.setChecked(true);
            }
        }
        currentCircuitForced = null;
        loader.setVisibility(View.GONE);
        dataContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Set the listener called when the positive button has been clicked with the selected {@link Circuit}
     *
     * @param listener Listener when positive button has been clicked with the selected {@link Circuit}
     */
    public void setOnDialogSelectionListener(OnDialogSelectionInterface<Circuit> listener) {
        this.listener = listener;
    }

    /**
     * Set the current {@link Circuit} even if currentCircuit is not null
     *
     * @param currentCircuitForced The {@link Circuit} {@link Long id} to force to be checked
     */
    public void setCurrentCircuitForced(Long currentCircuitForced) {
        this.currentCircuitForced = currentCircuitForced;
    }
}
