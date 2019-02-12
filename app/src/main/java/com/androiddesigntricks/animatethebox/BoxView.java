package com.androiddesigntricks.animatethebox;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;

/**
 * <p>
 * This class is a custom view derived from the basic {@link View} class that can be used to manage
 * details for simple animations in Android with the {@link android.view.ViewPropertyAnimator} class.
 * </p>
 *
 * <p>
 * <em>Note: </em> This class does not take care of the animations themselves. This class only manages
 * the attributes used for the animations such as what x-coord the box should translate to, or what
 * alpha level the transparency should be modified. To complete the animation, you will still need to
 * pass this information into your own {@link android.view.ViewPropertyAnimator} methods on the Activity
 * or Fragment.
 * </p>
 */
public class BoxView extends View {

    private static final String TAG = "BoxView";

    private static final int MOVE_TOP = 0;
    private static final int RIGHT = 1;
    private static final int MOVE_BOTTOM = 2;
    private static final int LEFT = 3;
    private static final int MOVE_CENTER = 4;

    private static final int ELEVATION_LEVEL = 40;

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

    private int zElevation = 0;

    private float alphaFactor = 1.0f;
    private float scaleFactor = SCALE_NORMAL;

    private int prevXTranslationPoint;
    private int prevYTranslationPoint;
    private int curXTranslationPoint;
    private int curYTranslationPoint;

    private float prevScalePoint;
    private float currScalePoint;

    private boolean isAlphaTransparent;
    private boolean isRotationClockwise;
    private boolean isElevated;

    enum BoxStatus {STATIONARY, MOVING_X, MOVING_Y, MOVING_Z, SCALING, ROTATING, ALPHA;}

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
        isElevated = false;
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
     * Returns the elevation of the box in the z-axis.
     * @return The elevation of the box in the z-axis.
     */
    public int getZElevation() {
        return zElevation;
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
        int boxWidth = getScaledWidth();

        maxXTranslation = parentWidth - boxWidth - paddingLeft - paddingRight;
        midXTranslation = maxXTranslation / 2;
        minXTranslation = paddingLeft;
    }

    /**
     * Calculate the maximum amount of translation the box is allowed to move inside the parent
     * layout when considering the height of the box, along with two views that act as barriers for
     * the minimum and maximum amount the box is able to translate in the y-axis.
     *
     * @param topBarrier    - The view that borders the highest point on the screen the box can translate
     *                      in the y-axis
     * @param bottomBarrier - The view that borders the lowest point on the screen the box can translate
     *                      in the y-axis
     */
    public void calcYTranslationPoints(View topBarrier, View bottomBarrier) {
        int topBarrierY = (int) topBarrier.getY();
        int bottomBarrierY = (int) bottomBarrier.getY();

        int topBarrierHeight = topBarrier.getHeight();
        int boxHeight = getScaledHeight();

        MarginLayoutParams topLayoutParams = (MarginLayoutParams) topBarrier.getLayoutParams();
        MarginLayoutParams bottomLayoutParams = (MarginLayoutParams) bottomBarrier.getLayoutParams();

        int topBarrierBottomMargin = topLayoutParams.bottomMargin;
        int bottomBarrierTopMargin = bottomLayoutParams.topMargin;

        minYTranslation = topBarrierY + topBarrierHeight + topBarrierBottomMargin;
        maxYTranslation = bottomBarrierY - bottomBarrierTopMargin - boxHeight;
        midYTranslation = ((maxYTranslation - minYTranslation) / 2) + minYTranslation;
    }

    /**
     * Set and return the x-coord the box should move to inside the layout based on its current
     * position and its previous position.
     *
     * @return The translation point in pixels the box should move to inside the layout along the x-axis
     */
    public float getNextXTranslation() {
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

    /**
     * Set and return the y-coord the box should move to inside the layout based on its current
     * position and its previous position.
     *
     * @return The translation point in pixels the box should move to inside the layout along the y-axis
     */
    public float getNextYTranslation() {
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

    /**
     * Gets the elevation level to move the box to, and then toggles the elevation flag.
     * @return The elevation level to move the box to.
     */
    public int getNextElevationLevel() {
        if (isElevated) {
            zElevation = 0;
        } else {
            zElevation = ELEVATION_LEVEL;
        }

        isElevated = !isElevated;

        return zElevation;
    }

    /**
     * Set and return the scaling factor for the box.
     *
     * @return The scaleFactor of the box.
     */
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

    /**
     * Set and return the alphaFactor factor for the box.
     *
     * @return The alphaFactor of the box.
     */
    public float getAlphaToChange() {

        if (isAlphaTransparent) {
            alphaFactor = ALPHA_OPAQUE;
        } else {
            alphaFactor = ALPHA_TRANSPARENT;
        }

        isAlphaTransparent = !isAlphaTransparent;

        return alphaFactor;
    }

    /**
     * Set and return the angle to which the box must rotate.
     *
     * @return The scaleFactor of the box.
     */
    public float getAngleToRotate() {
        float rotationAngle;

        if (isRotationClockwise) {
            rotationAngle = ROTATION_COUNTER_CLOCKWISE;
        } else {
            rotationAngle = ROTATION_CLOCKWISE;
        }

        isRotationClockwise = !isRotationClockwise;

        return rotationAngle;
    }

    /**
     * Get the current status of the box.
     *
     * @return The current status of the box as a string.
     */
    public String getBoxStatusName() {
        return boxStatus.name();
    }

    /**
     * Get the current status of the box.
     *
     * @return The current status of the box as an enum BoxStatus value.
     */
    public BoxStatus getBoxStatus() {
        return boxStatus;
    }

    /**
     * Set the BoxStatus for the box.
     *
     * @param boxStatus Enum value for BoxStatus
     */
    public void setBoxStatus(BoxStatus boxStatus) {
        this.boxStatus = boxStatus;
    }

    /**
     * The width of the box with scaling taken into factor.
     *
     * @return The width of the box with scaling taken into factor.
     */
    public int getScaledWidth() {
        return (int) (getWidth() * scaleFactor);
    }

    /**
     * The height of the box with scaling taken into factor.
     *
     * @return The height of the box with scaling taken into factor.
     */
    public int getScaledHeight() {
        return (int) (getHeight() * scaleFactor);
    }

    /**
     * Get the alpha value as a percentage.
     *
     * @return A numerical value representing the View's current alpha as a percentage.
     */
    public int getAlphaAsPercent() {
        return (int) (alphaFactor * 100);
    }

    /**
     * Get the scaling factor of the box.
     *
     * @return The scaling factor of the box.
     */
    public float getScaleFactor() {
        return scaleFactor;
    }
}
