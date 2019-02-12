package com.androiddesigntricks.animatethebox;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.androiddesigntricks.animatethebox.BoxView.BoxStatus;

public class MainActivity extends AppCompatActivity {

    private static int DURATION = 1000;

    ConstraintLayout parentLayout;

    private View dividerTop;
    private View dividerBottom;
    private BoxView boxView;
    private TextView textTranslationX;
    private TextView textTranslationY;
    private TextView textTranslationZ;
    private TextView textAlpha;
    private TextView textScale;
    private TextView textWidth;
    private TextView textStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentLayout = findViewById(R.id.container);
        dividerTop = findViewById(R.id.divider_top);
        dividerBottom = findViewById(R.id.divider_bottom);
        boxView = findViewById(R.id.view_box);
        textTranslationX = findViewById(R.id.text_translation_x);
        textTranslationY = findViewById(R.id.text_translation_y);
        textTranslationZ = findViewById(R.id.text_translation_z);
        textAlpha = findViewById(R.id.text_alpha);
        textScale = findViewById(R.id.text_scale);
        textWidth = findViewById(R.id.text_width);
        textStatus = findViewById(R.id.text_status);

        boxView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onGlobalLayout() {
                boxView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Update our stats about the box on the layout.
                textTranslationX.setText(String.format("%d px", boxView.getXLocationInWindow()));
                textTranslationY.setText(String.format("%d px", boxView.getYLocationInWindow()));
                textTranslationZ.setText(String.format("%d px", boxView.getZElevation()));
                textWidth.setText(String.format("%d px", boxView.getWidth()));
                textStatus.setText(boxView.getBoxStatusName());
                textAlpha.setText(String.format("%d%%", boxView.getAlphaAsPercent()));
                textScale.setText(String.format("%.1f x", boxView.getScaleFactor()));

                boxView.calcXTranslationPoints(parentLayout);
                boxView.calcYTranslationPoints(dividerTop, dividerBottom);
            }
        });
    }

    public void onTranslateXClicked(View view) {
        float moveToX = boxView.getNextXTranslation();
        boxView.setBoxStatus(BoxStatus.MOVING_X);

        boxView.animate()
                .setDuration(DURATION)
                .withStartAction(animateStartAction())
                .x(moveToX)
                .setUpdateListener(animateUpdate())
                .withEndAction(animateEndAction());
    }

    public void onTranslateYClicked(View view) {
        float moveToY = boxView.getNextYTranslation();

        boxView.setBoxStatus(BoxStatus.MOVING_Y);

        boxView.animate()
                .setDuration(DURATION)
                .withStartAction(animateStartAction())
                .y(moveToY)
                .setUpdateListener(animateUpdate())
                .withEndAction(animateEndAction());
    }

    public void onTranslateZClicked(View view) {
        int elevation = boxView.getNextElevationLevel();

        boxView.setBoxStatus(BoxStatus.MOVING_Z);

        boxView.animate()
                .setDuration(DURATION)
                .withStartAction(animateStartAction())
                .z(elevation)
                .setUpdateListener(animateUpdate())
                .withEndAction(animateEndAction());
    }

    @SuppressLint("DefaultLocale")
    public void onScaleClicked(View view) {
        float scaleFactor = boxView.getFactorToScale();

        textScale.setText(String.format("%2.1fx", boxView.getScaleFactor()));
        textWidth.setText(String.format("%d px", boxView.getScaledWidth()));

        boxView.setBoxStatus(BoxStatus.SCALING);

        boxView.animate()
                .setDuration(DURATION)
                .withStartAction(animateStartAction())
                .scaleX(scaleFactor)
                .scaleY(scaleFactor)
                .setUpdateListener(animateUpdate())
                .withEndAction(animateEndAction());
    }

    @SuppressLint("DefaultLocale")
    public void onAlphaClicked(View view) {
        float alphaFactor = boxView.getAlphaToChange();

        boxView.setBoxStatus(BoxStatus.ALPHA);
        textAlpha.setText(String.format("%d%%", boxView.getAlphaAsPercent()));

        boxView.animate()
                .setDuration(DURATION)
                .withStartAction(animateStartAction())
                .alpha(alphaFactor)
                .setUpdateListener(animateUpdate())
                .withEndAction(animateEndAction());
    }

    public void onRotationClicked(View view) {
        float rotationAngle = boxView.getAngleToRotate();

        boxView.setBoxStatus(BoxStatus.ROTATING);

        boxView.animate()
                .setDuration(DURATION)
                .withStartAction(animateStartAction())
                .rotation(rotationAngle)
                .setUpdateListener(animateUpdate())
                .withEndAction(animateEndAction());
    }

    public Runnable animateStartAction() {
        return new Runnable() {
            @Override
            public void run() {
                textStatus.setText(boxView.getBoxStatusName());
            }
        };
    }

    public Runnable animateEndAction() {
        return new Runnable() {
            @Override
            public void run() {
                boxView.setBoxStatus(BoxStatus.STATIONARY);
                textStatus.setText(boxView.getBoxStatusName());
            }
        };
    }

    private ValueAnimator.AnimatorUpdateListener animateUpdate() {
        return new ValueAnimator.AnimatorUpdateListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                textTranslationX.setText(String.format("%d px", boxView.getXLocationInWindow()));
                textTranslationY.setText(String.format("%d px", boxView.getYLocationInWindow()));
                textTranslationZ.setText(String.format("%d px", boxView.getZElevation()));
            }
        };
    }
}
