package com.androiddesigntricks.animatethebox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button buttonTranslateX;
    private Button buttonTranslateY;
    private Button buttonScale;
    private Button buttonAlpha;
    private Button buttonRotation;
    private View dividerTop;
    private View dividerBottom;
    private View viewBox;
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

        buttonTranslateX = findViewById(R.id.button_translate_x);
        buttonTranslateY = findViewById(R.id.button_translate_y);
        buttonScale = findViewById(R.id.button_scale);
        buttonAlpha = findViewById(R.id.button_alpha);
        buttonRotation = findViewById(R.id.button_rotation);
        dividerTop = findViewById(R.id.divider_top);
        dividerBottom = findViewById(R.id.divider_bottom);
        viewBox = findViewById(R.id.view_box);
        textTranslationX = findViewById(R.id.text_translation_x);
        textTranslationY = findViewById(R.id.text_translation_y);
        textAlpha = findViewById(R.id.text_alpha);
        textScale = findViewById(R.id.text_scale);
        textWidth = findViewById(R.id.text_width);
        textStatus = findViewById(R.id.text_status);
    }

    public void onTranslateXClicked(View view) {

    }

    public void onTranslateYClicked(View view) {

    }

    public void onAlphaClicked(View view) {

    }

    public void onScaleClicked(View view) {

    }

    public void onRotationClicked(View view) {

    }
}
