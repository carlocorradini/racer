package com.unitn.disi.lpsmt.racer.util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Input utility class
 *
 * @author Carlo Corradini
 */
public final class InputUtil {
    /**
     * Hide the Keyboard if it's focused
     *
     * @param activity The current {@link Activity activity}
     * @return True if the keyboard has been closed, false otherwise
     */
    public static boolean hideKeyboard(final Activity activity) {
        if (activity == null) return false;
        InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() == null || im == null) return false;
        im.hideSoftInputFromWindow(activity.getCurrentFocus().getApplicationWindowToken(), 0);
        return true;
    }
}
