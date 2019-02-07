package com.androiddesigntricks.animatethebox;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class BoxView extends View {

    private int maxXTranslation = 0;
    private float alpha = 1.0f;
    private float scale = 1.0f;

    enum BoxStatus {STATIONARY, MOVING_X, MOVING_Y, SCALE_UP, SCALE_DOWN, ROTATE_LEFT, ROTATE_RIGHT}

    ;
    private BoxStatus boxStatus = BoxStatus.STATIONARY;

    public BoxView(Context context) {
        super(context);
    }

    public BoxView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Returns the absolute x-coord of the BoxView top left corner in number of pixels from the
     * left side of the screen.
     *
     * @return x-coordinate of top left corner
     */
    public int getXLocationInWindow() {
        int[] outLocation = new int[2];
        getLocationInWindow(outLocation);

        return outLocation[0];
    }

    /**
     * Returns the absolute y-coord of the BoxView top left corner in number of pixels from the top
     * of the screen.
     *
     * @return y-coordinate of the top left corner
     */
    public int getYLocationInWindow() {
        int[] outLocation = new int[2];
        getLocationInWindow(outLocation);

        return outLocation[1];
    }

    /**
     * Calculate the maximum amount of translation the box is allowed to move inside the parent
     * layout when considering the width of the box, the left and right padding of the parent layout
     * and the width of the parent layout.
     *
     * @param parentLayout The ViewGroup that contains the BoxView (e.g. LinearLayout, ConstraintLayout, etc...)
     */
    public void calcMaxXTranslation(View parentLayout) {
        int parentWidth = parentLayout.getWidth();
        int paddingLeft = parentLayout.getPaddingLeft();
        int paddingRight = parentLayout.getPaddingRight();
        int boxWidth = getWidth();

        maxXTranslation = parentWidth - boxWidth - paddingLeft - paddingRight;
    }

    public String getBoxStatusName() {
        return boxStatus.name();
    }

    public int getAlphaAsPercent() {
        return (int) (alpha * 100);
    }

    public float getScale() {
        return scale;
    }
}
