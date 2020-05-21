package com.unitn.disi.lpsmt.racer.util;

import android.content.Context;

import androidx.annotation.DimenRes;

/**
 * Dimension utility class
 *
 * @author Carlo Corradini
 */
public final class DimensUtil {

    /**
     * Convert the given {@link DimenRes identifier} to dp value
     *
     * @param context The current {@link Context}
     * @param id      The {@link DimenRes} identifier
     * @return The identified {@link DimenRes id} into dp dimension
     */
    public static int toDp(final Context context, @DimenRes int id) {
        return (int) (context.getResources().getDimension(id) * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
