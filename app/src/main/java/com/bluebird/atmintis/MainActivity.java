/**
* File: Main activity
*
* This file is the main file.
*
* Authors:
* Kieth Chung; mingipchung@gmail.com
* Aditya Lakshminarayanan; aditya.net09@gmail.com
* Theo Messer; messertheo@gmail.com
* Jack Park; jackp7640@gmail.com
* Charles Wang; xiaotian980204@gmail.com
*
* Date: November 6 2020
*
*//
package com.bluebird.atmintis;

//Imports
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity {

    GridView gridView;
    
    //Override methods
    
    //Show the grid on the main screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(new ImageAdapter(this));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), FullScreenImage.class);
                intent.putExtra("id", position);
                startActivity(intent);
            }
        });
        
    //TODO: On Clicking button, add files from external storage
    }

}
