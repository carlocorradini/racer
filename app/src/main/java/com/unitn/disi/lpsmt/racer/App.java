package com.unitn.disi.lpsmt.racer;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * Application class
 *
 * @author Carlo Corradini
 */
public final class App extends Application {

    /**
     * The {@link Application} context
     *
     * @see Application
     */
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
    }

    /**
     * Return the {@link Application} context
     *
     * @return {@link Application} context
     */
    public static Context getContext() {
        return App.context;
    }
}
