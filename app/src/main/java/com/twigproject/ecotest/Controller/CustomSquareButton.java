package com.twigproject.ecotest.Controller;

/**
 * Creates custom square button
 * @autor Max Ermakov
 * @see android.widget.Button
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class CustomSquareButton extends Button {

    public CustomSquareButton(Context context) {
        super(context);
    }

    public CustomSquareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSquareButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int width = getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
        final int height = getDefaultSize(getSuggestedMinimumHeight(),heightMeasureSpec);
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, w, oldw, oldh);
    }

}