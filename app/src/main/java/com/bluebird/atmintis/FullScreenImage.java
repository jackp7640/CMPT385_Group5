package com.bluebird.atmintis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class FullScreenImage extends AppCompatActivity {

    //TODO: Create Button instances

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        imageView = (ImageView) findViewById(R.id.image_view);

        getSupportActionBar().hide();

        Intent intent = getIntent();

        int position = intent.getExtras().getInt("id");

        ImageAdapter imageAdapter = new ImageAdapter(this);

        imageView.setImageResource(imageAdapter.imageArray[position]);

        //TODO: add onClickListeners to make buttons perform their functions.
    }

}