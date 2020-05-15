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
import com.unitn.disi.lpsmt.racer.api.entity.Car;
import com.unitn.disi.lpsmt.racer.api.service.CarService;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Car Dialog
 *
 * @author Carlo Corradini
 */
public final class CarDialog extends AppCompatDialogFragment {

    /**
     * Current selected {@link Car}
     */
    private Car currentCar = null;
    /**
     * Current {@link Car} force to set checked
     */
    private Long currentCarForced = null;
    /**
     * Old selected {@link Car car} if the {@link CarDialog} was close and the positive button has been clicked
     */
    private Car savedCar = null;
    /**
     * {@link List} of available {@link Car cars}
     * The {@link List} is populated only once calling the {@link CarService} find method
     */
    private List<Car> cars = new ArrayList<>();
    /**
     * {@link RadioButton Car Radio Button} {@link RadioGroup container}
     */
    private RadioGroup carsContainer;
    /**
     * Data container
     */
    private LinearLayout dataContainer;
    /**
     * {@link ProgressBar} loader
     */
    private ProgressBar loader;
    /**
     * Listener when positive button has been clicked with the selected {@link Car}
     */
    private OnDialogSelectionInterface<Car> listener = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        setRetainInstance(true);
        setCancelable(true);

        @SuppressLint("InflateParams") View view = requireActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_car, null);

        EditText inputSearch = view.findViewById(R.id.fragment_dialog_car_input_search);
        carsContainer = view.findViewById(R.id.fragment_dialog_car_container);
        dataContainer = view.findViewById(R.id.fragment_dialog_car_data_container);
        loader = view.findViewById(R.id.fragment_dialog_car_loader);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                for (int i = 0; i < carsContainer.getChildCount(); i++) {
                    View v = carsContainer.getChildAt(i);
                    RadioButton carRadioButton = v instanceof RadioButton ? (RadioButton) v : null;

                    if (carRadioButton != null
                            && carRadioButton.getText().toString().toLowerCase().contains(s.toString().toLowerCase())) {
                        carRadioButton.setVisibility(View.VISIBLE);
                    } else if (carRadioButton != null) {
                        carRadioButton.setVisibility(View.GONE);
                    }
                }
            }
        });
        carsContainer.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton carRadioButton = group.findViewById(checkedId);
            int index = (int) carRadioButton.getTag();
            currentCar = index >= cars.size() ? null : cars.get(index);
        });

        loadCars();

        return new AlertDialog.Builder(requireActivity())
                .setView(view)
                .setTitle(R.string.choose_car)
                .setNegativeButton(R.string.dismiss, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    savedCar = currentCar;
                    if (currentCar != null && listener != null)
                        listener.onDialogSelection(currentCar);
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
     * Load all available {@link Car cars} calling the {@link CarService} find method
     * If the cars list is not empty no call will be generated
     */
    private void loadCars() {
        if (!cars.isEmpty()) showCars();
        else
            API.getInstance().getClient().create(CarService.class).find().enqueue(new Callback<API.Response<List<Car>>>() {
                @Override
                public void onResponse(@NotNull Call<API.Response<List<Car>>> call, @NotNull Response<API.Response<List<Car>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        cars = response.body().data;
                        showCars();
                    } else {
                        Toasty.error(requireContext(), R.string.error_unknown).show();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<API.Response<List<Car>>> call, @NotNull Throwable t) {
                    ErrorHelper.showFailureError(getContext(), t);
                }
            });
    }

    /**
     * Show the available cars in the {@link List} of cars into the carsContainer
     * After hide the loader and show the dataContainer
     */
    private void showCars() {
        for (int i = 0; i < cars.size(); ++i) {
            RadioButton carRadioButton = new RadioButton(getContext());
            carRadioButton.setText(cars.get(i).getFullName());
            carRadioButton.setTag(i);
            carsContainer.addView(carRadioButton);
            if (currentCarForced == null && savedCar != null && savedCar.equals(cars.get(i)))
                carRadioButton.setChecked(true);
            else if (currentCarForced != null && currentCarForced.equals(cars.get(i).id))
                carRadioButton.setChecked(true);
        }
        currentCarForced = null;
        loader.setVisibility(View.GONE);
        dataContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Set the listener called when the positive button has been clicked with the selected {@link Car}
     *
     * @param listener Listener when positive button has been clicked with the selected {@link Car}
     */
    public void setOnDialogSelectionListener(OnDialogSelectionInterface<Car> listener) {
        this.listener = listener;
    }

    /**
     * Set the current {@link Car} even if currentCar is not null
     *
     * @param currentCarForced The {@link Car} {@link Long id} to force to be checked
     */
    public void setCurrentCarForced(Long currentCarForced) {
        this.currentCarForced = currentCarForced;
    }
}
