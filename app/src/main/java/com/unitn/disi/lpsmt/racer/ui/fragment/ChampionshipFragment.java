package com.unitn.disi.lpsmt.racer.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.AuthManager;
import com.unitn.disi.lpsmt.racer.api.entity.Car;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;
import com.unitn.disi.lpsmt.racer.api.entity.ChampionshipCircuit;
import com.unitn.disi.lpsmt.racer.api.entity.ChampionshipGameSetting;
import com.unitn.disi.lpsmt.racer.api.entity.Circuit;
import com.unitn.disi.lpsmt.racer.api.entity.GameSetting;
import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.api.entity.UserChampionship;
import com.unitn.disi.lpsmt.racer.api.service.CarService;
import com.unitn.disi.lpsmt.racer.api.service.ChampionshipCircuitService;
import com.unitn.disi.lpsmt.racer.api.service.ChampionshipGameSettingService;
import com.unitn.disi.lpsmt.racer.api.service.ChampionshipService;
import com.unitn.disi.lpsmt.racer.api.service.CircuitService;
import com.unitn.disi.lpsmt.racer.api.service.GameSettingService;
import com.unitn.disi.lpsmt.racer.api.service.TeamService;
import com.unitn.disi.lpsmt.racer.api.service.UserChampionshipService;
import com.unitn.disi.lpsmt.racer.api.service.UserService;
import com.unitn.disi.lpsmt.racer.helper.ColorHelper;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;
import com.unitn.disi.lpsmt.racer.api.entity.Team;
import com.unitn.disi.lpsmt.racer.ui.adapter.ChampionshipAdapter;
import com.unitn.disi.lpsmt.racer.ui.dialog.ChampionshipSubscribeDialog;
import com.unitn.disi.lpsmt.racer.ui.dialog.ChampionshipViewSubscriptionDialog;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * {@link Championship} information {@link Fragment}
 *
 * @author Carlo Corradini
 */
public final class ChampionshipFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = ChampionshipFragment.class.getName();

    /**
     * Invalid {@link Championship} identifier
     */
    private static final long INVALID_CHAMPIONSHIP_ID = 0;

    /**
     * Current {@link Championship} {@link Long id} to show
     */
    private long idChampionship = INVALID_CHAMPIONSHIP_ID;

    /**
     * {@link Championship} loaded from server using idChampionship
     */
    private Championship championship = null;

    /**
     * {@link UserChampionship} of current authenticated {@link User}
     */
    private UserChampionship userChampionship = null;

    /**
     * {@link UserChampionship} {@link Car}
     */
    private Car userCar = null;

    /**
     * {@link UserChampionship} {@link Team}
     */
    private Team userTeam = null;

    /**
     * {@link Championship} {@link ImageView} logo
     */
    private ImageView imageLogo;

    /**
     * {@link Championship} {@link TextView} name
     */
    private TextView txtName;

    /**
     * {@link Championship} {@link Chip} id
     */
    private Chip txtId;

    /**
     * {@link Championship} {@link Chip} forum {@link java.net.URL}
     */
    private Chip btnForum;

    /**
     * {@link Button} subscribe
     */
    private Button btnSubscribe;

    /**
     * {@link Button} unsubscribe
     */
    private Button btnUnsubscribe;

    /**
     * {@link View} subscription container
     */
    private View subscriptionContainer;

    /**
     * The {@link ExpandableListView} information of the current {@link Championship}
     */
    private ExpandableListView expandableListView;

    /**
     * Adapter for {@link ExpandableListView expandableListView}
     */
    private ChampionshipAdapter championshipAdapter;

    /**
     * The {@link View} container
     */
    private View championshipContainer;

    /**
     * {@link SwipeRefreshLayout} for reloading the {@link Championship} in the fragment
     */
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * {@link ChampionshipSubscribeDialog} for subscription
     */
    private ChampionshipSubscribeDialog championshipSubscribeDialog = null;

    /**
     * {@link ChampionshipViewSubscriptionDialog} for view and update subscription
     */
    private ChampionshipViewSubscriptionDialog championshipViewSubscriptionDialog = null;

    /**
     * Flag to check if the {@link Championship} has started
     */
    private boolean isChampionshipStarted = false;

    /**
     * Flag to check if the {@link User} has been loaded
     */
    private boolean isUsersLoaded = false;
    /**
     * Flag to check if the {@link Circuit} has been loaded
     */
    private boolean isCircuitsLoaded = false;
    /**
     * Flag to check if the {@link Car} has been loaded
     */
    private boolean isCarsLoaded = false;
    /**
     * Flag to check if the {@link Team} has been loaded
     */
    private boolean isTeamsLoaded = false;
    /**
     * Flag to check if the {@link GameSetting} has been loaded
     */
    private boolean isGameSettingsLoaded = false;

    @NotNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_championship, container, false);

        championshipContainer = root.findViewById(R.id.fragment_championship_container);
        swipeRefreshLayout = root.findViewById(R.id.fragment_championship_swipe_refresh);
        btnSubscribe = root.findViewById(R.id.fragment_championship_button_subscribe);
        btnUnsubscribe = root.findViewById(R.id.fragment_championship_button_unsubscribe);
        final Button btnViewSubscription = root.findViewById(R.id.fragment_championship_button_view_subscription);
        subscriptionContainer = root.findViewById(R.id.fragment_championship_button_subscription_container);
        imageLogo = root.findViewById(R.id.fragment_championship_logo);
        txtName = root.findViewById(R.id.fragment_championship_name);
        txtId = root.findViewById(R.id.fragment_championship_id);
        final Chip btnRanking = root.findViewById(R.id.fragment_championship_ranking);
        final Chip btnPhotos = root.findViewById(R.id.fragment_championship_photos);
        btnForum = root.findViewById(R.id.fragment_championship_forum);
        expandableListView = root.findViewById(R.id.fragment_championship_list_view);
        championshipAdapter = new ChampionshipAdapter(requireContext(), new HashMap<>());

        swipeRefreshLayout.setColorSchemeColors(ColorHelper.COLOR_BLUE, ColorHelper.COLOR_RED, ColorHelper.COLOR_YELLOW, ColorHelper.COLOR_GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        final int[] prevExpandPosition = {-1};
        expandableListView.setOnGroupExpandListener(groupPosition -> {
            if (prevExpandPosition[0] >= 0 && prevExpandPosition[0] != groupPosition)
                expandableListView.collapseGroup(prevExpandPosition[0]);
            prevExpandPosition[0] = groupPosition;
        });
        championshipAdapter = new ChampionshipAdapter(requireContext(), new HashMap<>());
        expandableListView.setAdapter(championshipAdapter);

        btnSubscribe.setOnClickListener(v -> {
            if (championshipSubscribeDialog == null || championshipSubscribeDialog.isAdded())
                return;
            championshipSubscribeDialog.show(getParentFragmentManager(), ChampionshipSubscribeDialog.class.getName());
        });
        btnUnsubscribe.setOnClickListener(v -> unsubscribe());

        btnViewSubscription.setOnClickListener(v -> {
            if (championshipViewSubscriptionDialog == null || championshipViewSubscriptionDialog.isAdded())
                return;
            championshipViewSubscriptionDialog.show(getParentFragmentManager(), ChampionshipViewSubscriptionDialog.class.getName());
        });

        btnRanking.setOnClickListener(v -> {
            final Bundle bundle = new Bundle();
            bundle.putLong("id", idChampionship);
            Navigation.findNavController(root).navigate(R.id.nav_championship_action_to_nav_championship_ranking, bundle);
        });

        btnPhotos.setOnClickListener(v -> {
            final Bundle bundle = new Bundle();
            bundle.putLong("id", idChampionship);
            Navigation.findNavController(root).navigate(R.id.nav_championship_action_to_nav_championship_photos, bundle);
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        idChampionship = getArguments() != null ? getArguments().getLong("id", INVALID_CHAMPIONSHIP_ID) : INVALID_CHAMPIONSHIP_ID;

        loadChampionship(idChampionship);
    }

    /**
     * Load the {@link Championship} given the {@link Long id}
     *
     * @param id The {@link Championship} {@link Long id} to load
     */
    private void loadChampionship(final long id) {
        if (id == INVALID_CHAMPIONSHIP_ID) {
            Toasty.error(requireContext(), R.string.championship_invalid).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        API.getInstance().getClient().create(ChampionshipService.class).findById(id).enqueue(new Callback<API.Response<Championship>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<Championship>> call, @NotNull Response<API.Response<Championship>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "Successfully loaded Championship " + id);
                    setChampionshipUI(response.body().data);
                } else if (response.code() == HttpStatus.SC_NOT_FOUND) {
                    Log.w(TAG, "Unable to find Championship " + id);
                    Toasty.error(requireContext(), R.string.championship_not_found).show();
                } else {
                    Log.w(TAG, "Unable to load Championship " + id + "due to failure response");
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<Championship>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to load Championship " + id + "due to " + t.getMessage(), t);
                swipeRefreshLayout.setRefreshing(false);
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    /**
     * Set the {@link Championship} UI information
     *
     * @param championship The {@link Championship to show}
     */
    private void setChampionshipUI(final Championship championship) {
        if (championship == null) return;
        this.championship = championship;
        this.championshipSubscribeDialog = new ChampionshipSubscribeDialog(championship);

        championshipSubscribeDialog.setOnDialogSelectionListener(newUserChampionship -> {
            if (newUserChampionship == null) return;
            championshipSubscribeDialog.dismiss();
            Toasty.success(requireContext(), R.string.subscribe_success).show();
            refresh();
        });

        Picasso.get().load(championship.logo.toString()).into(imageLogo);
        txtName.setText(championship.name);
        txtId.setText(String.valueOf(championship.id));
        btnForum.setOnClickListener(v -> requireContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(championship.forum.toString()))));

        if (championship.users.contains(AuthManager.getInstance().getAuthUserId())) {
            // User already subscribed
            btnSubscribe.setVisibility(View.GONE);
            subscriptionContainer.setVisibility(View.VISIBLE);
        } else {
            // User is not subscribed
            btnSubscribe.setVisibility(View.VISIBLE);
            subscriptionContainer.setVisibility(View.GONE);
        }

        loadChampionshipUsersAndCarsAndTeams(championship.id);
        loadChampionshipCircuits(championship.id);
        loadChampionshipGameSettings(championship.id);
    }

    /**
     * Show the UI  only if loaded
     */
    private void showUI() {
        if (!isUsersLoaded || !isCircuitsLoaded || !isCarsLoaded || !isTeamsLoaded || !isGameSettingsLoaded || championship == null)
            return;

        if (userChampionship != null && userCar != null && userTeam != null) {
            this.championshipViewSubscriptionDialog = new ChampionshipViewSubscriptionDialog(championship, userChampionship, userCar, userTeam, isChampionshipStarted);
            championshipViewSubscriptionDialog.setOnDialogSelectionListener(updatedUserChampionship -> {
                if (updatedUserChampionship == null) return;
                championshipViewSubscriptionDialog.dismiss();
                Toasty.success(requireContext(), R.string.subscription_update_success).show();
                refresh();
            });
        }

        if (isChampionshipStarted) {
            btnSubscribe.setEnabled(false);
            btnUnsubscribe.setEnabled(false);
        }

        championshipContainer.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Load the {@link Championship} {@link User} and {@link Car} and {@link Team}.
     * The loading combination {@link User} must have a {@link Car}
     * The loading combination {@link User} must have a {@link Team}
     * todo This code sucks
     *
     * @param idChampionship The {@link Championship} {@link Long identifier}
     */
    private void loadChampionshipUsersAndCarsAndTeams(final long idChampionship) {
        if (idChampionship == INVALID_CHAMPIONSHIP_ID) return;

        API.getInstance().getClient().create(UserChampionshipService.class).findByChampionship(idChampionship).enqueue(new Callback<API.Response<List<UserChampionship>>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<List<UserChampionship>>> call, @NotNull Response<API.Response<List<UserChampionship>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final List<UserChampionship> subscriptions = response.body().data;

                    for (int i = 0; i < subscriptions.size(); ++i) {
                        if (!subscriptions.get(i).user.equals(AuthManager.getInstance().getAuthUserId()))
                            continue;
                        userChampionship = subscriptions.get(i);
                        break;
                    }

                    API.getInstance().getClient().create(UserService.class).findByChampionship(idChampionship).enqueue(new Callback<API.Response<List<User>>>() {
                        @Override
                        public void onResponse(@NotNull Call<API.Response<List<User>>> call, @NotNull Response<API.Response<List<User>>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                final List<User> users = response.body().data;

                                API.getInstance().getClient().create(CarService.class).findByChampionship(idChampionship).enqueue(new Callback<API.Response<List<Car>>>() {
                                    @Override
                                    public void onResponse(@NotNull Call<API.Response<List<Car>>> call, @NotNull Response<API.Response<List<Car>>> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            final List<Car> cars = response.body().data;

                                            if (userChampionship != null) {
                                                for (int i = 0; i < cars.size(); ++i) {
                                                    if (!userChampionship.car.equals(cars.get(i).id))
                                                        continue;
                                                    userCar = cars.get(i);
                                                    break;
                                                }
                                            }

                                            API.getInstance().getClient().create(TeamService.class).findByChampionship(idChampionship).enqueue(new Callback<API.Response<List<Team>>>() {
                                                @Override
                                                public void onResponse(@NotNull Call<API.Response<List<Team>>> call, @NotNull Response<API.Response<List<Team>>> response) {
                                                    if (response.isSuccessful() && response.body() != null) {
                                                        final List<Team> teams = response.body().data;
                                                        final List<Triple<User, Car, Team>> userChampionships = new ArrayList<>(subscriptions.size());

                                                        if (userChampionship != null) {
                                                            for (int i = 0; i < teams.size(); ++i) {
                                                                if (!userChampionship.team.equals(teams.get(i).id))
                                                                    continue;
                                                                userTeam = teams.get(i);
                                                                break;
                                                            }
                                                        }

                                                        for (UserChampionship subscription : subscriptions) {
                                                            for (User user : users) {
                                                                if (!subscription.user.equals(user.id))
                                                                    continue;
                                                                for (Car car : cars) {
                                                                    if (!subscription.car.equals(car.id))
                                                                        continue;
                                                                    for (Team team : teams) {
                                                                        if (!subscription.team.equals(team.id))
                                                                            continue;
                                                                        userChampionships.add(Triple.of(user, car, team));
                                                                        break;
                                                                    }
                                                                    break;
                                                                }
                                                                break;
                                                            }
                                                        }

                                                        championshipAdapter.setListData(ChampionshipAdapter.TYPE_CHAMPIONSHIP_USER, new ArrayList<>(userChampionships));
                                                        championshipAdapter.setListData(ChampionshipAdapter.TYPE_CHAMPIONSHIP_CAR, new ArrayList<>(cars));
                                                        championshipAdapter.setListData(ChampionshipAdapter.TYPE_CHAMPIONSHIP_TEAM, new ArrayList<>(teams));
                                                        isUsersLoaded = true;
                                                        isCarsLoaded = true;
                                                        isTeamsLoaded = true;
                                                        showUI();
                                                    } else {
                                                        Toasty.error(requireContext(), R.string.error_unknown).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(@NotNull Call<API.Response<List<Team>>> call, @NotNull Throwable t) {
                                                    Log.e(TAG, "Unable to load Teams of " + idChampionship + "due to " + t.getMessage(), t);
                                                    ErrorHelper.showFailureError(getContext(), t);
                                                }
                                            });
                                        } else {
                                            Toasty.error(requireContext(), R.string.error_unknown).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call<API.Response<List<Car>>> call, @NotNull Throwable t) {
                                        Log.e(TAG, "Unable to load Cars of " + idChampionship + "due to " + t.getMessage(), t);
                                        ErrorHelper.showFailureError(getContext(), t);
                                    }
                                });
                            } else {
                                Toasty.error(requireContext(), R.string.error_unknown).show();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<API.Response<List<User>>> call, @NotNull Throwable t) {
                            Log.e(TAG, "Unable to load Users of " + idChampionship + "due to " + t.getMessage(), t);
                            ErrorHelper.showFailureError(getContext(), t);
                        }
                    });
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<List<UserChampionship>>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to load User Championship of " + idChampionship + "due to " + t.getMessage(), t);
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    /**
     * Load the {@link Circuit} of the given idChampionship
     *
     * @param idChampionship The {@link Championship} {@link Long identifier}
     */
    private void loadChampionshipCircuits(final long idChampionship) {
        if (idChampionship == INVALID_CHAMPIONSHIP_ID) return;

        API.getInstance().getClient().create(ChampionshipCircuitService.class).findByChampionship(idChampionship).enqueue(new Callback<API.Response<List<ChampionshipCircuit>>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<List<ChampionshipCircuit>>> call, @NotNull Response<API.Response<List<ChampionshipCircuit>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final List<ChampionshipCircuit> dates = response.body().data != null ? response.body().data : new ArrayList<>();

                    // Check if the championship has already started
                    if (dates.size() > 0 && dates.get(0).date.isBefore(LocalDate.now()))
                        isChampionshipStarted = true;

                    API.getInstance().getClient().create(CircuitService.class).findByChampionship(idChampionship).enqueue(new Callback<API.Response<List<Circuit>>>() {
                        @Override
                        public void onResponse(@NotNull Call<API.Response<List<Circuit>>> call, @NotNull Response<API.Response<List<Circuit>>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                final List<Circuit> circuits = response.body().data;
                                final List<Pair<ChampionshipCircuit, Circuit>> championshipCircuits = new ArrayList<>(dates.size());

                                for (Circuit circuit : circuits) {
                                    for (ChampionshipCircuit date : dates) {
                                        if (!circuit.id.equals(date.circuit)) continue;
                                        championshipCircuits.add(Pair.of(date, circuit));
                                        break;
                                    }
                                }

                                championshipAdapter.setListData(ChampionshipAdapter.TYPE_CHAMPIONSHIP_CIRCUIT, new ArrayList<>(championshipCircuits));
                                isCircuitsLoaded = true;
                                showUI();
                            } else {
                                Toasty.error(requireContext(), R.string.error_unknown).show();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<API.Response<List<Circuit>>> call, @NotNull Throwable t) {
                            Log.e(TAG, "Unable to load Circuits of " + idChampionship + "due to " + t.getMessage(), t);
                            ErrorHelper.showFailureError(getContext(), t);
                        }
                    });
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<List<ChampionshipCircuit>>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to load Championship Circuits of " + idChampionship + "due to " + t.getMessage(), t);
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    /**
     * Load the {@link GameSetting} of the given idChampionship
     *
     * @param idChampionship The {@link Championship} {@link Long identifier}
     */
    private void loadChampionshipGameSettings(final long idChampionship) {
        if (idChampionship == INVALID_CHAMPIONSHIP_ID) return;

        API.getInstance().getClient().create(ChampionshipGameSettingService.class).findByChampionship(idChampionship).enqueue(new Callback<API.Response<List<ChampionshipGameSetting>>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<List<ChampionshipGameSetting>>> call, @NotNull Response<API.Response<List<ChampionshipGameSetting>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final List<ChampionshipGameSetting> values = response.body().data;

                    API.getInstance().getClient().create(GameSettingService.class).findByChampionship(idChampionship).enqueue(new Callback<API.Response<List<GameSetting>>>() {
                        @Override
                        public void onResponse(@NotNull Call<API.Response<List<GameSetting>>> call, @NotNull Response<API.Response<List<GameSetting>>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                final List<GameSetting> names = response.body().data;
                                final List<Pair<ChampionshipGameSetting, GameSetting>> championshipGameSettings = new ArrayList<>(values.size());

                                for (ChampionshipGameSetting value : values) {
                                    for (GameSetting setting : names) {
                                        if (!value.gameSetting.equals(setting.id)) continue;
                                        championshipGameSettings.add(Pair.of(value, setting));
                                        break;
                                    }
                                }

                                championshipAdapter.setListData(ChampionshipAdapter.TYPE_CHAMPIONSHIP_GAME_SETTING, new ArrayList<>(championshipGameSettings));
                                isGameSettingsLoaded = true;
                                showUI();
                            } else {
                                Toasty.error(requireContext(), R.string.error_unknown).show();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<API.Response<List<GameSetting>>> call, @NotNull Throwable t) {
                            Log.e(TAG, "Unable to load Game Settings of " + idChampionship + "due to " + t.getMessage(), t);
                            ErrorHelper.showFailureError(getContext(), t);
                        }
                    });
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<List<ChampionshipGameSetting>>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to load Championship Game Settings of " + idChampionship + "due to " + t.getMessage(), t);
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    /**
     * Unsubscribe the current authenticate {@link User}
     */
    private void unsubscribe() {
        if (idChampionship == INVALID_CHAMPIONSHIP_ID) return;
        swipeRefreshLayout.setRefreshing(true);

        API.getInstance().getClient().create(UserChampionshipService.class).delete(idChampionship).enqueue(new Callback<API.Response>() {
            @Override
            public void onResponse(@NotNull Call<API.Response> call, @NotNull Response<API.Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toasty.success(requireContext(), R.string.unsubscribe_success).show();
                    refresh();
                } else {
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to unsubscribe from Championship " + idChampionship + "due to " + t.getMessage(), t);
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {
        if (!swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(true);
        isUsersLoaded = isCircuitsLoaded = isCarsLoaded = isTeamsLoaded = isGameSettingsLoaded = false;
        championshipContainer.setVisibility(View.GONE);
        for (int i = 0; i < championshipAdapter.getGroupCount(); i++)
            expandableListView.collapseGroup(i);
        loadChampionship(idChampionship);
    }
}
