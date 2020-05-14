package com.unitn.disi.lpsmt.racer;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.unitn.disi.lpsmt.racer.api.AuthManager;
import com.unitn.disi.lpsmt.racer.ui.activity.MainActivity;
import com.unitn.disi.lpsmt.racer.ui.activity.SignIn;

/**
 * Launcher Activity
 *
 * @author Carlo Corradini
 */
public final class Launcher extends AppCompatActivity {

    /**
     * Timeout in ms
     */
    private static final int LAUNCHER_TIMEOUT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // TODO REMOVE!!!
        // AuthManager.getInstance().clearToken();

        new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(LAUNCHER_TIMEOUT);
                } catch (Exception ignored) {
                } finally {
                    Intent intent = AuthManager.getInstance().isAuth()
                            ? new Intent(Launcher.this, MainActivity.class)
                            : new Intent(Launcher.this, SignIn.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.start();
    }
}
