package com.unitn.disi.lpsmt.racer.ui.activity;

import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import com.unitn.disi.lpsmt.racer.R;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.ui.fragment.NavHeaderFragment;

/**
 * Main Activity when the {@link User} has been successfully authenticated
 *
 * @author Carlo Corradini
 */
public final class MainActivity extends AppCompatActivity {

    /**
     * {@link AppBarConfiguration} current configuration
     */
    private AppBarConfiguration appBarConfiguration;

    /**
     * {@link NavHeaderFragment Navigation Header} fragment
     */
    private NavHeaderFragment navHeaderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_main);
        NavigationView navigationView = findViewById(R.id.nav_view_main);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_main);
        navHeaderFragment = new NavHeaderFragment(navigationView.getHeaderView(0));

        setSupportActionBar(toolbar);
        navigationView.setItemIconTintList(null);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_championships)
                .setDrawerLayout(drawer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navHeaderFragment.destroy();
    }
}
