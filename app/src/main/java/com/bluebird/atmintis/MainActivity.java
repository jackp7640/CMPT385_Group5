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
 */
package com.bluebird.atmintis;

//Imports
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    GridView gridView;

    private FloatingActionButton addPhotosButton = null;
    private  static final int MY_READ_PERMISSION_CODE = 101;
    private static final int PERMISSION_REQUEST = 0;

    public ImageAdapter image = new ImageAdapter(this);
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ImageAdapter getImageAdapter(){
        return image; //This is the variable value I can't get.
    }

    public ArrayList<Uri> uris = new ArrayList<>();

    final static private String TAG = "tag";





    //Show the grid on the main screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (savedInstanceState != null) {
            Log.d("SUCCESS", "STARTING FROM SAVED INSTANCE STATE");

        } else {
            Log.d("ERROR", "NO SAVED INSTANCE STATE");
            setContentView(R.layout.activity_main);

            gridView = findViewById(R.id.grid_view);
            gridView.setAdapter(image);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), FullScreenImage.class);
                    intent.putExtra("id", position);
                    //intent.putExtra("image", image.imageArray[position]);
                    intent.putExtra("imageUri",uris.get(position).toString());
                    startActivity(intent);
                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            }





            addPhotosButton = (FloatingActionButton) findViewById(R.id.addButton);
            addPhotosButton.setOnClickListener(
                    new View.OnClickListener()
                    {
                        public void onClick(View v)
                        {
                            onClickAddPhotosButton(v);
                        }
                    }
            );
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void onClickAddPhotosButton(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, MY_READ_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case MY_READ_PERMISSION_CODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    Log.d("app", "The uri of the picture " + selectedImage);
                    uris.add(selectedImage);

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath, bmOptions);


                    image.addElement(image.imageArray, bitmap);
                    Log.d("app", "The # of pictures: " + Integer.toString(image.getCount()));
                    gridView = findViewById(R.id.grid_view);
                    gridView.setAdapter(image);

                    //Toast.makeText(this, count, Toast.LENGTH_SHORT).show();
                    Log.d("app", "The # of pictures: " + Integer.toString(image.getCount()));
                }
        }
    }
}