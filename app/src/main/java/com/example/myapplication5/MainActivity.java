package com.example.myapplication5;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements Pop.ExampleDialogListener {
    private TextView displayTag;
    private FloatingActionButton dialogButton;
    private int mLastX = 0;
    private int mLastY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayTag = (TextView) findViewById(R.id.addChipV);
        dialogButton = (FloatingActionButton) findViewById(R.id.btn_pop);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }
    public void openDialog() {
        Pop exampleDialog = new Pop();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }
    //set text to tag
    @Override
    public void applyTexts(String tag1) {

        displayTag.setText(tag1);
        displayTag.setTextColor(Color.parseColor("#ffffff"));

    }
    //make textview draggable
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;

                int translationX = (int) displayTag.getTranslationX() + deltaX;
                int translationY = (int) displayTag.getTranslationY() + deltaY;
                displayTag.setTranslationX(translationX);
                displayTag.setTranslationY(translationY);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }
}
