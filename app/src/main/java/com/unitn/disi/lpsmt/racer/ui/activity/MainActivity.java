package com.unitn.disi.lpsmt.racer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.unitn.disi.lpsmt.racer.R;

import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.unitn.disi.lpsmt.racer.api.AuthManager;
import com.unitn.disi.lpsmt.racer.api.entity.User;
import com.unitn.disi.lpsmt.racer.ui.fragment.NavHeaderFragment;

import es.dmoral.toasty.Toasty;

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

    /**
     * {@link DrawerLayout} menu navigation
     */
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        drawerLayout = findViewById(R.id.drawer_layout_main);
        NavigationView navigationView = findViewById(R.id.nav_view_main);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_main);
        Button buttonSignOut = findViewById(R.id.sign_out_button);
        navHeaderFragment = new NavHeaderFragment(navigationView.getHeaderView(0));

        setSupportActionBar(toolbar);
        navigationView.setItemIconTintList(null);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_account,
                R.id.nav_championships,
                R.id.nav_admin_panel)
                .setDrawerLayout(drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        buttonSignOut.setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.sign_out_question)
                .setNegativeButton(R.string.dismiss, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.confirm, ((dialog, which) -> {
                    AuthManager.getInstance().clearTokens();
                    Intent intent = new Intent(this, SignIn.class);
                    intent.putExtra("ACTION_SIGN_OUT", true);
                    startActivity(intent);
                    finish();
                })).show());

        checkAction();
    }

    /**
     * Check if the activity has a correlated action
     * and show the status to the user
     */
    private void checkAction() {
        if (getIntent().getBooleanExtra("ACTION_SIGN_IN", false)) {
            // Sign In action
            Toasty.success(this, R.string.sign_in_success).show();
        }
        if (getIntent().getBooleanExtra("ACTION_SIGN_IN_AUTOMATIC", false)) {
            // Sign In automatic, welcome back
            Toasty.normal(this, R.string.sign_in_automatic).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navHeaderFragment.destroy();
    }
}
