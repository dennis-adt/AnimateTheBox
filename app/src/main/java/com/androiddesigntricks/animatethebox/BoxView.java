package com.androiddesigntricks.animatethebox;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class BoxView extends View {

    private static final int TOP = 0;
    private static final int RIGHT = 1;
    private static final int BOTTOM = 2;
    private static final int LEFT = 3;
    private static final int CENTER = 4;

    private int minXTranslation = 0;
    private int midXTranslation = 0;
    private int maxXTranslation = 0;

    private int minYTranslation = 0;
    private int midYTranslation = 0;
    private int maxYTranslation = 0;

    private float alpha = 1.0f;
    private float scale = 1.0f;

    // We start off with our box in the center of the stage. The initial direction of movement
    // will be towards the right side of the stage for the x-axis, and towards the bottom of the stage
    // in the y-axis.
    private int prevXTranslationPoint;
    private int prevYTranslationPoint;
    private int curXTranslationPoint;
    private int curYTranslationPoint;

    enum BoxStatus {STATIONARY, MOVING_X, MOVING_Y, SCALE_UP, SCALE_DOWN, ROTATE_LEFT, ROTATE_RIGHT, FADE_IN, FADE_OUT}

    private BoxStatus boxStatus = BoxStatus.STATIONARY;
    public BoxView(Context context) {
        super(context);
        init();
    }

    public BoxView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        prevXTranslationPoint = LEFT;
        prevYTranslationPoint = TOP;
        curXTranslationPoint = CENTER;
        curYTranslationPoint = CENTER;
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
    public void calcXTranslationPoints(View parentLayout) {
        int parentWidth = parentLayout.getWidth();
        int paddingLeft = parentLayout.getPaddingLeft();
        int paddingRight = parentLayout.getPaddingRight();
        int boxWidth = getWidth();

        maxXTranslation = parentWidth - boxWidth - paddingLeft - paddingRight;
        midXTranslation = maxXTranslation / 2;
        minXTranslation = paddingLeft;
    }

    /**
     * Set the x-coord the box should move to inside the layout based on its current position and its
     * previous position.
     *
     * @return The translation point in pixels the box should move to inside the layout along the x-axis
     */
    public float moveToX() {
        float moveToX = 0;

        switch (curXTranslationPoint) {
            case LEFT:
                moveToX = midXTranslation;
                prevXTranslationPoint = LEFT;
                curXTranslationPoint = CENTER;
                break;

            case CENTER:
                // If we came from the LEFT in the last move, then we want to move RIGHT this time,
                // and vice versa.
                moveToX = prevXTranslationPoint == LEFT ? maxXTranslation : 0;
                curXTranslationPoint = prevXTranslationPoint == LEFT ? RIGHT : LEFT;
                prevXTranslationPoint = CENTER;
                break;

            case RIGHT:
                moveToX = midXTranslation;
                prevXTranslationPoint = RIGHT;
                curXTranslationPoint = CENTER;
        }

        return moveToX;
    }

    public String getBoxStatusName() {
        return boxStatus.name();
    }

    public void setBoxStatus(BoxStatus boxStatus) {
        this.boxStatus = boxStatus;
    }

    public int getAlphaAsPercent() {
        return (int) (alpha * 100);
    }

    public float getScale() {
        return scale;
    }
}
