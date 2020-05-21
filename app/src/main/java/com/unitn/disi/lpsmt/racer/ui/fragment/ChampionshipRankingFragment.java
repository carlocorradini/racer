package com.unitn.disi.lpsmt.racer.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;
import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.api.entity.UserChampionship;
import com.unitn.disi.lpsmt.racer.api.service.UserChampionshipService;
import com.unitn.disi.lpsmt.racer.api.service.UserService;
import com.unitn.disi.lpsmt.racer.helper.ColorHelper;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;
import com.unitn.disi.lpsmt.racer.util.DimensUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * {@link Championship} ranking {@link Fragment}
 *
 * @author Carlo Corradini
 */
public final class ChampionshipRankingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = ChampionshipRankingFragment.class.getName();

    /**
     * Invalid {@link Championship} identifier
     */
    private static final long INVALID_CHAMPIONSHIP_ID = 0;

    /**
     * Current {@link Championship} {@link Long id} to show
     */
    private long idChampionship = INVALID_CHAMPIONSHIP_ID;

    /**
     * {@link TableLayout} container
     */
    private TableLayout table = null;

    /**
     * {@link SwipeRefreshLayout} for reloading the {@link Championship ranking}
     */
    private SwipeRefreshLayout swipeRefreshLayout = null;

    @NotNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_championship_ranking, container, false);

        table = root.findViewById(R.id.fragment_championship_ranking_table);

        swipeRefreshLayout = root.findViewById(R.id.fragment_championship_ranking_swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(ColorHelper.COLOR_BLUE, ColorHelper.COLOR_RED, ColorHelper.COLOR_YELLOW, ColorHelper.COLOR_GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        idChampionship = getArguments() != null ? getArguments().getLong("id", INVALID_CHAMPIONSHIP_ID) : INVALID_CHAMPIONSHIP_ID;

        loadChampionshipRanking(idChampionship);
    }

    /**
     * Load the {@link UserChampionship} and thank extract the values
     *
     * @param id The {@link Championship} id
     * @see UserChampionship#points
     */
    private void loadChampionshipRanking(final long id) {
        if (id == INVALID_CHAMPIONSHIP_ID) {
            Toasty.error(requireContext(), R.string.championship_invalid).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        API.getInstance().getClient().create(UserChampionshipService.class).findByChampionship(id).enqueue(new Callback<API.Response<List<UserChampionship>>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<List<UserChampionship>>> call, @NotNull Response<API.Response<List<UserChampionship>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "Successfully loaded Championship ranking");
                    final List<UserChampionship> userChampionships = response.body().data;

                    API.getInstance().getClient().create(UserService.class).findByChampionship(id).enqueue(new Callback<API.Response<List<User>>>() {
                        @Override
                        public void onResponse(@NotNull Call<API.Response<List<User>>> call, @NotNull Response<API.Response<List<User>>> response) {
                            swipeRefreshLayout.setRefreshing(false);
                            if (response.isSuccessful() && response.body() != null) {
                                final List<User> users = response.body().data;

                                populateTable(userChampionships, users);
                            } else {
                                Log.w(TAG, "Unable to load Championship ranking due to failure response");
                                Toasty.error(requireContext(), R.string.error_unknown).show();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<API.Response<List<User>>> call, @NotNull Throwable t) {
                            Log.e(TAG, "Unable to load Championship ranking due to " + t.getMessage(), t);
                            swipeRefreshLayout.setRefreshing(false);
                            ErrorHelper.showFailureError(getContext(), t);
                        }
                    });
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Log.w(TAG, "Unable to load Championship ranking due to failure response");
                    Toasty.error(requireContext(), R.string.error_unknown).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<List<UserChampionship>>> call, @NotNull Throwable t) {
                Log.e(TAG, "Unable to load Championship ranking due to " + t.getMessage(), t);
                swipeRefreshLayout.setRefreshing(false);
                ErrorHelper.showFailureError(getContext(), t);
            }
        });
    }

    /**
     * Populate the table with the given values
     *
     * @param userChampionships The {@link UserChampionship} ranking and subscription
     * @param users             The {@link User users}
     */
    private void populateTable(final List<UserChampionship> userChampionships, final List<User> users) {
        if (table == null || userChampionships == null || users == null) return;

        table.removeAllViews();
        userChampionships.sort((o1, o2) -> o1.points.compareTo(o2.points));

        if (userChampionships.isEmpty() || userChampionships.get(0).points == 0)
            Toasty.info(requireContext(), R.string.championship_not_started).show();

        final TableRow tableRowTitle = constructTableRow();
        final TextView tableDataTitleRanking = constructTableData(R.string.ranking);
        tableDataTitleRanking.setAllCaps(true);
        tableDataTitleRanking.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_medium));
        final TextView tableDataTitleUsername = constructTableData(R.string.username);
        tableDataTitleUsername.setAllCaps(true);
        tableDataTitleUsername.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_medium));
        final TextView tableDataTitlePoints = constructTableData(R.string.points);
        tableDataTitlePoints.setAllCaps(true);
        tableDataTitlePoints.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_medium));

        tableRowTitle.addView(tableDataTitleRanking);
        tableRowTitle.addView(tableDataTitleUsername);
        tableRowTitle.addView(tableDataTitlePoints);
        table.addView(tableRowTitle);

        for (int i = 0; i < userChampionships.size(); ++i) {
            final UserChampionship userChampionship = userChampionships.get(i);
            for (final User user : users) {
                if (!userChampionship.user.equals(user.id)) continue;

                final TableRow tableRow = constructTableRow();

                final TextView tableDataRanking = constructTableData(String.valueOf(i + 1));

                // Ranking podium colors
                switch (i + 1) {
                    case 1: {
                        tableDataRanking.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGold, null));
                        break;
                    }
                    case 2: {
                        tableDataRanking.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorSilver, null));
                        break;
                    }
                    case 3: {
                        tableDataRanking.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorBronze, null));
                        break;
                    }
                }

                tableRow.addView(tableDataRanking);
                tableRow.addView(constructTableData(user.username));
                tableRow.addView(constructTableData(String.valueOf(userChampionship.points)));
                table.addView(tableRow);

                break;
            }
        }
    }

    /**
     * Construct a table row
     *
     * @return Constructed {@link TableRow}
     */
    private TableRow constructTableRow() {
        final TableRow tableRow = new TableRow(getContext());
        final TableRow.LayoutParams tableRowLayoutParams = tableRow.getLayoutParams() != null ? (TableRow.LayoutParams) tableRow.getLayoutParams() : new TableRow.LayoutParams();

        tableRowLayoutParams.width = 0;
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setLayoutParams(tableRowLayoutParams);

        return tableRow;
    }

    /**
     * Construct a table data with the given text
     *
     * @param text The text to display
     * @return The {@link TextView} data representation
     */
    private TextView constructTableData(final String text) {
        final TextView txt = new TextView(getContext());
        final ViewGroup.LayoutParams txtLayoutParams = txt.getLayoutParams() != null ? txt.getLayoutParams() : new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);

        txt.setText(text);
        txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txt.setTypeface(txt.getTypeface(), Typeface.BOLD);
        txt.setPadding(DimensUtil.toDp(requireContext(), R.dimen.padding_tiny), DimensUtil.toDp(requireContext(), R.dimen.padding_tiny), DimensUtil.toDp(requireContext(), R.dimen.padding_tiny), DimensUtil.toDp(requireContext(), R.dimen.padding_tiny));
        txt.setLayoutParams(txtLayoutParams);

        return txt;
    }

    /**
     * Construct a table data with the given {@link StringRes} id
     *
     * @param id The {@link StringRes} id
     * @return The {@link TextView} data representation
     */
    private TextView constructTableData(@StringRes int id) {
        return constructTableData(getResources().getString(id));
    }

    @Override
    public void onRefresh() {
        loadChampionshipRanking(idChampionship);
    }
}
