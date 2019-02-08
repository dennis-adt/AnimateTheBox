package com.androiddesigntricks.animatethebox;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.androiddesigntricks.animatethebox.BoxView.BoxStatus;

public class MainActivity extends AppCompatActivity {

    private static final int DURATION = 1000;

    ConstraintLayout parentLayout;

    private Button buttonTranslateX;
    private Button buttonTranslateY;
    private Button buttonScale;
    private Button buttonAlpha;
    private Button buttonRotation;
    private View dividerTop;
    private View dividerBottom;
    private BoxView boxView;
    private TextView textTranslationX;
    private TextView textTranslationY;
    private TextView textAlpha;
    private TextView textScale;
    private TextView textWidth;
    private TextView textStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentLayout = findViewById(R.id.container);
        buttonTranslateX = findViewById(R.id.button_translate_x);
        buttonTranslateY = findViewById(R.id.button_translate_y);
        buttonScale = findViewById(R.id.button_scale);
        buttonAlpha = findViewById(R.id.button_alpha);
        buttonRotation = findViewById(R.id.button_rotation);
        dividerTop = findViewById(R.id.divider_top);
        dividerBottom = findViewById(R.id.divider_bottom);
        boxView = findViewById(R.id.view_box);
        textTranslationX = findViewById(R.id.text_translation_x);
        textTranslationY = findViewById(R.id.text_translation_y);
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
                textWidth.setText(String.format("%d px", boxView.getWidth()));
                textStatus.setText(boxView.getBoxStatusName());
                textAlpha.setText(String.format("%d%%", boxView.getAlphaAsPercent()));
                textScale.setText(String.format("%.1f x", boxView.getScale()));

                boxView.calcXTranslationPoints(parentLayout);
            }
        });
    }

    public void onTranslateXClicked(View view) {
        float moveToX = boxView.moveToX();
        boxView.setBoxStatus(BoxStatus.MOVING_X);

        boxView.animate()
                .setDuration(DURATION)
                .withStartAction(animateStartAction())
                .x(moveToX)
                .setUpdateListener(animateUpdate())
                .withEndAction(animateEndAction());
    }

    public void onTranslateYClicked(View view) {

    }

    public void onAlphaClicked(View view) {

    }

    public void onScaleClicked(View view) {

    }

    public void onRotationClicked(View view) {

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
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                textTranslationX.setText(String.format("%d px", boxView.getXLocationInWindow()));
                textTranslationY.setText(String.format("%d px", boxView.getYLocationInWindow()));
            }
        };
    }
}
