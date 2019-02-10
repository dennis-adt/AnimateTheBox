package com.androiddesigntricks.animatethebox;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;

public class BoxView extends View {

    private static final String TAG = "BoxView";

    private static final int MOVE_TOP = 0;
    private static final int RIGHT = 1;
    private static final int MOVE_BOTTOM = 2;
    private static final int LEFT = 3;
    private static final int MOVE_CENTER = 4;

    private static final float SCALE_SMALL = 0.5f;
    private static final float SCALE_NORMAL = 1.0f;
    private static final float SCALE_LARGE = 2.0f;

    private static final float ALPHA_TRANSPARENT = 0.2f;
    private static final float ALPHA_OPAQUE = 1.0f;

    private static final float ROTATION_CLOCKWISE = 360.0f;
    private static final float ROTATION_COUNTER_CLOCKWISE = -360.0f;

    private int minXTranslation = 0;
    private int midXTranslation = 0;
    private int maxXTranslation = 0;

    private int minYTranslation = 0;
    private int midYTranslation = 0;
    private int maxYTranslation = 0;

    private float alpha = 1.0f;
    private float scaleFactor = SCALE_NORMAL;

    // We start off with our box in the center of the stage. The initial direction of movement
    // will be towards the right side of the stage for the x-axis, and towards the bottom of the stage
    // in the y-axis.
    private int prevXTranslationPoint;
    private int prevYTranslationPoint;
    private int curXTranslationPoint;
    private int curYTranslationPoint;

    private float prevScalePoint;
    private float currScalePoint;

    private boolean isAlphaTransparent;
    private boolean isRotationClockwise;

    enum BoxStatus {STATIONARY, MOVING_X, MOVING_Y, SCALING, ROTATING, ALPHA;}

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
        prevYTranslationPoint = MOVE_TOP;
        curXTranslationPoint = MOVE_CENTER;
        curYTranslationPoint = MOVE_CENTER;

        prevScalePoint = SCALE_SMALL;
        currScalePoint = SCALE_NORMAL;

        isAlphaTransparent = false;
        isRotationClockwise = false;
    }

    public int getScaleWidth() {
        return (int) (getWidth() * scaleFactor);
    }

    public int getScaleHeight() {
        return (int) (getHeight() * scaleFactor);
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

    public void calcYTranslationPoints(View topBarrier, View bottomBarrier) {
        int topBarrierY = (int) topBarrier.getY();
        int bottomBarrierY = (int) bottomBarrier.getY();

        int topBarrierHeight = topBarrier.getHeight();
        int boxHeight = getHeight();

        MarginLayoutParams topLayoutParams = (MarginLayoutParams) topBarrier.getLayoutParams();
        MarginLayoutParams bottomLayoutParams = (MarginLayoutParams) bottomBarrier.getLayoutParams();

        int topBarrierBottomMargin = topLayoutParams.bottomMargin;
        int bottomBarrierTopMargin = bottomLayoutParams.topMargin;

        minYTranslation = topBarrierY + topBarrierHeight + topBarrierBottomMargin;
        maxYTranslation = bottomBarrierY - bottomBarrierTopMargin - boxHeight;
        midYTranslation = ((maxYTranslation - minYTranslation) / 2) + minYTranslation;
    }

    /**
     * Set the x-coord the box should move to inside the layout based on its current position and its
     * previous position.
     *
     * @return The translation point in pixels the box should move to inside the layout along the x-axis
     */
    public float getXToMove() {
        float moveToX = 0;

        switch (curXTranslationPoint) {
            case LEFT:
                moveToX = midXTranslation;
                prevXTranslationPoint = LEFT;
                curXTranslationPoint = MOVE_CENTER;
                break;

            case MOVE_CENTER:
                // If we came from the LEFT in the last move, then we want to move RIGHT this time,
                // and vice versa.
                moveToX = prevXTranslationPoint == LEFT ? maxXTranslation : minXTranslation;
                curXTranslationPoint = prevXTranslationPoint == LEFT ? RIGHT : LEFT;
                prevXTranslationPoint = MOVE_CENTER;
                break;

            case RIGHT:
                moveToX = midXTranslation;
                prevXTranslationPoint = RIGHT;
                curXTranslationPoint = MOVE_CENTER;
        }

        return moveToX;
    }

    public float getYToMove() {
        float moveToY = 0;

        switch (curYTranslationPoint) {
            case MOVE_TOP:
                moveToY = midYTranslation;
                prevYTranslationPoint = MOVE_TOP;
                curYTranslationPoint = MOVE_CENTER;
                break;

            case MOVE_CENTER:
                // If we came from the MOVE_TOP in the last move, then we want to move MOVE_BOTTOM this time,
                // and vice versa.
                moveToY = prevYTranslationPoint == MOVE_TOP ? maxYTranslation : minYTranslation;
                curYTranslationPoint = prevYTranslationPoint == MOVE_TOP ? MOVE_BOTTOM : MOVE_TOP;
                prevYTranslationPoint = MOVE_CENTER;
                break;

            case MOVE_BOTTOM:
                moveToY = midYTranslation;
                prevYTranslationPoint = MOVE_BOTTOM;
                curYTranslationPoint = MOVE_CENTER;
        }

        return moveToY;
    }


    public float getFactorToScale() {

        if (currScalePoint == SCALE_SMALL) {
            prevScalePoint = SCALE_SMALL;
            currScalePoint = SCALE_NORMAL;
            scaleFactor = SCALE_NORMAL;
        } else if (currScalePoint == SCALE_NORMAL) {
            scaleFactor = prevScalePoint == SCALE_SMALL ? SCALE_LARGE : SCALE_SMALL;
            currScalePoint = prevScalePoint == SCALE_SMALL ? SCALE_LARGE : SCALE_SMALL;
            prevScalePoint = SCALE_NORMAL;
        } else if (currScalePoint == SCALE_LARGE) {
            prevScalePoint = SCALE_LARGE;
            currScalePoint = SCALE_NORMAL;
            scaleFactor = SCALE_NORMAL;
        }

        return scaleFactor;
    }

    public float getAlphaToChange() {

        if (isAlphaTransparent) {
            alpha = ALPHA_OPAQUE;
        } else {
            alpha = ALPHA_TRANSPARENT;
        }

        isAlphaTransparent = !isAlphaTransparent;

        return alpha;
    }

    public float getAngleToRotate() {
        float rotation;

        if (isRotationClockwise) {
            rotation = ROTATION_COUNTER_CLOCKWISE;
        } else {
            rotation = ROTATION_CLOCKWISE;
        }

        isRotationClockwise = !isRotationClockwise;

        return rotation;
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

    public float getScaleFactor() {
        return scaleFactor;
    }
}
