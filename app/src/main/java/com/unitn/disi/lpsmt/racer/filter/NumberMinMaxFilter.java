package com.unitn.disi.lpsmt.racer.filter;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

/**
 * Number minimum and maximum {@link InputFilter}
 */
public final class NumberMinMaxFilter implements InputFilter {

    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = NumberMinMaxFilter.class.getName();

    /**
     * Minimum number accepted
     */
    private final int min;
    /**
     * Maximum number accepted
     */
    private final int max;

    /**
     * Construct a {@link NumberMinMaxFilter} with the min and max numbers given
     *
     * @param min Minimum number accepted
     * @param max Maximum number accepted
     */
    public NumberMinMaxFilter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Construct a {@link NumberMinMaxFilter} with min and max strings as numbers given
     *
     * @param min Minimum number accepted as string number
     * @param max Maximum number accepted as string number
     */
    public NumberMinMaxFilter(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            if (isInRange(min, max, Integer.parseInt(dest.toString() + source.toString())))
                return null;
        } catch (NumberFormatException ex) {
            Log.d(TAG, "Error parsing integer due to " + ex.getMessage(), ex);
        }

        return "";
    }

    /**
     * Check if value is in the range with minimum value min and maximum value max.
     * Both values are inclusive.
     *
     * @param min   Minimum number
     * @param max   Maximum number
     * @param value The value to check if it's in range
     * @return True if the value is in the range, false otherwise
     */
    private boolean isInRange(int min, int max, int value) {
        return max > min ? value >= min && value <= max : value >= max && value <= min;
    }
}
