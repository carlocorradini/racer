package com.unitn.disi.lpsmt.racer.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;
import com.unitn.disi.lpsmt.racer.api.entity.Team;
import com.unitn.disi.lpsmt.racer.api.entity.UserChampionship;
import com.unitn.disi.lpsmt.racer.util.DimensUtil;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * {@link Championship} {@link Team} ranking
 *
 * @author Carlo Corradini
 */
public final class ChampionshipRankingTabTeamsFragment extends Fragment {

    /**
     * {@link TableLayout} container
     */
    private TableLayout table = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_championship_ranking_tab_teams, container, false);

        table = root.findViewById(R.id.fragment_championship_ranking_tab_teams_table);

        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Construct a ranking list sorted DESC of teams
     *
     * @param userChampionships The {@link UserChampionship} ranking and subscription
     * @param teams             The {@link Team teams}
     * @return Ranking {@link List} sorted DESC
     */
    private List<Pair<Team, Short>> toRanking(final List<UserChampionship> userChampionships, final List<Team> teams) {
        final Map<Team, Short> ranking = new HashMap<>();
        final List<Pair<Team, Short>> rankingList = new ArrayList<>();

        for (final UserChampionship userChampionship : userChampionships) {
            for (final Team team : teams) {
                if (!userChampionship.team.equals(team.id)) continue;

                short points = ranking.get(team) != null ? Objects.requireNonNull(ranking.get(team)) : 0;
                points += userChampionship.points;

                ranking.put(team, points);

                break;
            }
        }

        for (Map.Entry<Team, Short> teamPoints : ranking.entrySet()) {
            rankingList.add(Pair.of(teamPoints.getKey(), teamPoints.getValue()));
        }

        rankingList.sort(((o1, o2) -> o1.getRight().compareTo(o2.getRight())));
        Collections.reverse(rankingList);

        return rankingList;
    }

    /**
     * Populate the table with the given values
     *
     * @param userChampionships The {@link UserChampionship} ranking and subscription
     * @param teams             The {@link Team teams}
     */
    public void populateTable(final List<UserChampionship> userChampionships, final List<Team> teams) {
        if (table == null || userChampionships == null || teams == null) return;

        table.removeAllViews();

        final List<Pair<Team, Short>> ranking = toRanking(userChampionships, teams);

        final TableRow tableRowTitle = constructTableRow();
        final TableRow tableRowDivider = constructTableRow();

        final TextView tableDataTitleRanking = constructTableData(R.string.ranking);
        tableDataTitleRanking.setAllCaps(true);
        tableDataTitleRanking.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_medium));
        final TextView tableDataTitleUsername = constructTableData(R.string.team);
        tableDataTitleUsername.setAllCaps(true);
        tableDataTitleUsername.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_medium));
        final TextView tableDataTitlePoints = constructTableData(R.string.points);
        tableDataTitlePoints.setAllCaps(true);
        tableDataTitlePoints.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_medium));

        tableRowTitle.addView(tableDataTitleRanking);
        tableRowTitle.addView(tableDataTitleUsername);
        tableRowTitle.addView(tableDataTitlePoints);

        tableRowDivider.addView(constructTableDivider());
        tableRowDivider.addView(constructTableDivider());
        tableRowDivider.addView(constructTableDivider());

        table.addView(tableRowTitle);
        table.addView(tableRowDivider);

        for (int i = 0; i < ranking.size(); ++i) {
            final Pair<Team, Short> rank = ranking.get(i);
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
            tableRow.addView(constructTableData(rank.getLeft().name));
            tableRow.addView(constructTableData(String.valueOf(rank.getRight())));
            table.addView(tableRow);
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

    /**
     * Table simple divider
     * @return A {@link View} representing the divider
     */
    private View constructTableDivider() {
        final View divider = new View(getContext());
        final ViewGroup.LayoutParams dividerLayoutParams = divider.getLayoutParams() != null ? divider.getLayoutParams() : new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1, 1);

        divider.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorGrey, null));
        divider.setLayoutParams(dividerLayoutParams);

        return divider;
    }
}