package com.unitn.disi.lpsmt.racer.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.unitn.disi.lpsmt.racer.R;
import com.unitn.disi.lpsmt.racer.api.API;
import com.unitn.disi.lpsmt.racer.api.entity.Championship;
import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.api.entity.UserChampionship;
import com.unitn.disi.lpsmt.racer.api.service.TeamService;
import com.unitn.disi.lpsmt.racer.api.service.UserChampionshipService;
import com.unitn.disi.lpsmt.racer.api.service.UserService;
import com.unitn.disi.lpsmt.racer.helper.ColorHelper;
import com.unitn.disi.lpsmt.racer.helper.ErrorHelper;
import com.unitn.disi.lpsmt.racer.api.entity.Team;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * {@link SwipeRefreshLayout} for reloading the {@link Championship ranking}
     */
    private SwipeRefreshLayout swipeRefreshLayout = null;

    /**
     * Ranking container
     */
    private View rankingContainer = null;

    /**
     * {@link User} tab
     */
    private final ChampionshipRankingTabUsersFragment championshipRankingTabUsersFragment = new ChampionshipRankingTabUsersFragment();
    /**
     * {@link Team} tab
     */
    private final ChampionshipRankingTabTeamsFragment championshipRankingTabTeamsFragment = new ChampionshipRankingTabTeamsFragment();

    @NotNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_championship_ranking, container, false);

        rankingContainer = root.findViewById(R.id.fragment_championship_ranking_container);

        swipeRefreshLayout = root.findViewById(R.id.fragment_championship_ranking_swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(ColorHelper.COLOR_BLUE, ColorHelper.COLOR_RED, ColorHelper.COLOR_YELLOW, ColorHelper.COLOR_GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        // Tabs
        ViewPager viewPager = root.findViewById(R.id.fragment_championship_ranking_view_pager);
        TabLayout tabLayout = root.findViewById(R.id.fragment_championship_ranking_tab);

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getParentFragmentManager(), 0);
        viewPagerAdapter.addFragment(championshipRankingTabUsersFragment, getResources().getString(R.string.pilots));
        viewPagerAdapter.addFragment(championshipRankingTabTeamsFragment, getResources().getString(R.string.teams));
        viewPager.setAdapter(viewPagerAdapter);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_user);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_team_formula_one_color);

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
                    final List<UserChampionship> userChampionships = response.body().data;

                    API.getInstance().getClient().create(UserService.class).findByChampionship(id).enqueue(new Callback<API.Response<List<User>>>() {
                        @Override
                        public void onResponse(@NotNull Call<API.Response<List<User>>> call, @NotNull Response<API.Response<List<User>>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                final List<User> users = response.body().data;
                                championshipRankingTabUsersFragment.populateTable(userChampionships, users);

                                API.getInstance().getClient().create(TeamService.class).findByChampionship(id).enqueue(new Callback<API.Response<List<Team>>>() {
                                    @Override
                                    public void onResponse(@NotNull Call<API.Response<List<Team>>> call, @NotNull Response<API.Response<List<Team>>> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            final List<Team> teams = response.body().data;
                                            championshipRankingTabTeamsFragment.populateTable(userChampionships, teams);
                                            swipeRefreshLayout.setRefreshing(false);
                                            rankingContainer.setVisibility(View.VISIBLE);
                                            if (userChampionships.isEmpty() || userChampionships.get(0).points == 0)
                                                Toasty.info(requireContext(), R.string.championship_not_started).show();
                                            Log.i(TAG, "Successfully loaded Championship ranking");
                                        } else {
                                            Log.w(TAG, "Unable to load Championship ranking due to failure response");
                                            swipeRefreshLayout.setRefreshing(false);
                                            Toasty.error(requireContext(), R.string.error_unknown).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call<API.Response<List<Team>>> call, @NotNull Throwable t) {
                                        Log.e(TAG, "Unable to load Championship ranking due to " + t.getMessage(), t);
                                        swipeRefreshLayout.setRefreshing(false);
                                        ErrorHelper.showFailureError(getContext(), t);
                                    }
                                });
                            } else {
                                Log.w(TAG, "Unable to load Championship ranking due to failure response");
                                swipeRefreshLayout.setRefreshing(false);
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

    @Override
    public void onRefresh() {
        rankingContainer.setVisibility(View.GONE);
        loadChampionshipRanking(idChampionship);
    }

    /**
     * {@link ViewPagerAdapter} used for tab
     *
     * @author Carlo Corradini
     */
    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        /**
         * {@link List} of {@link Fragment}
         */
        private List<Fragment> fragments = new ArrayList<>();
        /**
         * {@link List} of {@link Fragment Fragment's} title
         */
        private List<String> fragmentTitle = new ArrayList<>();

        /**
         * Construct a new {@link ViewPagerAdapter}
         *
         * @param fragmentManager The {@link FragmentManager}
         * @param behavior        Behavior
         */
        public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, int behavior) {
            super(fragmentManager, behavior);
        }

        /**
         * Add a fragment to {@link Fragment fragments}
         *
         * @param fragment The {@link Fragment} to add
         * @param title    The title of the fragment
         */
        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}
