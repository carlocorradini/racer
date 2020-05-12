package com.unitn.disi.lpsmt.racer.helper;

import android.content.Context;
import android.util.Log;

import androidx.annotation.StringRes;

import com.unitn.disi.lpsmt.racer.R;

import java.io.IOException;

import es.dmoral.toasty.Toasty;

/**
 * Error helper class
 *
 * @author Carlo Corradini
 */
public final class ErrorHelper {
    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = ErrorHelper.class.getName();

    /**
     * Show common {@link retrofit2.Retrofit} failure error with {@link Toasty} message
     *
     * @param context The current {@link Context}
     * @param t       The {@link Throwable error} thrown
     */
    public static void showFailureError(Context context, Throwable t) {
        @StringRes int stringErrorRes = R.string.error_unknown;
        if (t instanceof IOException) {
            stringErrorRes = R.string.error_connection_timeout;
        } else if (t instanceof IllegalStateException) {
            stringErrorRes = R.string.error_conversion;
        }

        Log.e(TAG, "Failure error due to " + t.getMessage(), t);

        Toasty.error(context, stringErrorRes).show();
    }
}
