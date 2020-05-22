package com.unitn.disi.lpsmt.racer.ui.component;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * {@link android.widget.GridView} {@link AppCompatImageView} item
 *
 * @author Carlo Corradini
 */
public final class GridImageViewItem extends AppCompatImageView {
    public GridImageViewItem(Context context) {
        super(context);
    }

    public GridImageViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridImageViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
