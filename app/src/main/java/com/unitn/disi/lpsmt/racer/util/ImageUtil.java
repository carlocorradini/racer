package com.unitn.disi.lpsmt.racer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Image utility class
 *
 * @author Carlo Corradini
 */
public final class ImageUtil {

    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = ImageUtil.class.getName();

    /**
     * Image type PNG
     */
    public static final String TYPE_PNG = "png";

    /**
     * Image type extension PNG
     */
    public static final String TYPE_PNG_EXT = "." + TYPE_PNG;

    /**
     * Convert the given bitmap into a local Uri using the given context
     *
     * @param context The current {@link Context}
     * @param bitmap  The {@link Bitmap image} to convert
     * @return The {@link Uri} representation of the bitmap, null otherwise
     */
    public static Uri toLocalBitmapUri(final Context context, final Bitmap bitmap) {
        if (context == null || bitmap == null) return null;
        Uri bmpUri = null;

        try {
            final File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + TYPE_PNG_EXT);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException ex) {
            Log.e(TAG, "Unable to convert Bitmap to local Uri due to " + ex.getMessage(), ex);
        }

        return bmpUri;
    }

    /**
     * Given the imageView extract the {@link BitmapDrawable} and convert into a local Uri using the given context
     *
     * @param context   The current {@link Context}
     * @param imageView The {@link ImageView} to extract the {@link Bitmap} from
     * @return The {@link Uri} representation of the extracted {@link BitmapDrawable} from imageView, null otherwise
     */
    public static Uri toLocalBitmapUri(final Context context, final ImageView imageView) {
        return (context != null && imageView != null && imageView.getDrawable() instanceof BitmapDrawable)
                ? toLocalBitmapUri(context, ((BitmapDrawable) imageView.getDrawable()).getBitmap())
                : null;
    }

    /**
     * Given the view convert into an {@link ImageView} and extract the {@link BitmapDrawable} and convert into a local Uri using the given context
     *
     * @param context The current {@link Context}
     * @param view    The {@link View} that wil be casted to {@link ImageView}
     * @return The {@link Uri} representation of the {@link ImageView} casted from view, null otherwise
     */
    public static Uri toLocalBitmapUri(final Context context, final View view) {
        return (context != null && view instanceof ImageView)
                ? toLocalBitmapUri(context, ((ImageView) view))
                : null;
    }
}
