/**
 * File: FullScreenImage
 *
 * This file is in use when the image has been selected and is in full screen mode.
 * It manages the caption buttons, as well as creating, editing, and file writing
 * for both kinds of captions (Audio, Text)
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observer;

import android.widget.Button;
import android.view.View;

import org.w3c.dom.Text;

import static java.lang.String.valueOf;
///

public class FullScreenImage extends AppCompatActivity implements Pop.ExampleDialogListener {
    // Draggable Tag
    private static final String TAGCONTENT = "content";
    private TextView displayTag;
    private FloatingActionButton dialogButton;
    private int mLastX = 0;
    private int mLastY = 0;
    private FloatingActionButton locationBt;
    private SharedPreferences mSharedPreferences;


    String tagInfo;
    int locationX;
    int locationY;
    //Creating instances
    boolean hasTextCaption;
    private static String textCaption;
    private static String textCaptionFileName = null;
    FusedLocationProviderClient fusedLocationProviderClient;
    ImageView imageView;
    private TextView locationInpu;
    //used for CaptionButton
    private FloatingActionButton captionButton = null;
    private static final String LOG_TAG = "AudioRecordTest";
    boolean hasCaption;
    boolean isRecording = false;
    boolean isPlaying = false;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private static String fileName = null;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO}; //permission list

    //Override method

    //First part is permissions.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission NOT GRANTED", Toast.LENGTH_SHORT).show();
                    finish();
                }
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;

        }
        if (!permissionToRecordAccepted) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        imageView = (ImageView) findViewById(R.id.image_view);


        //Draggable tag part
        displayTag = (TextView) findViewById(R.id.addChipV);
        dialogButton = (FloatingActionButton) findViewById(R.id.btn_pop);
        String data = FileUtil.getFile("save.txt");
        displayTag.setText(data);

              dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

//geo tagging
        locationBt = (FloatingActionButton) findViewById(R.id.getLocation);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationInpu = (TextView) findViewById(R.id.location);
        locationBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(FullScreenImage.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    ActivityCompat.requestPermissions(FullScreenImage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });



        //audio caption button
        captionButton = (FloatingActionButton) findViewById(R.id.audioPlayFab);
        captionButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        onClickAudioCaptionButton(v);
                    }
                }
        );

        //long click button
        captionButton.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        onLongClickCaptionButton(v);
                        return true;
                    }
                }
        );

        //Text caption button
        FloatingActionButton textCaptionButton = (FloatingActionButton) findViewById(R.id.addCaptionFab);
        textCaptionButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        onClickTextCaptionButton(FullScreenImage.this);
                    }
                }
        );

        //Creating files for Caption storage
        getSupportActionBar().hide();

        Intent intent = getIntent();
        int position = intent.getExtras().getInt("id");
        //Bitmap[] imageArray = (Bitmap[]) intent.getParcelableArrayExtra("ImageArray");
        Uri selectedImage = Uri.parse(intent.getExtras().getString("imageUri"));

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap imageBM = BitmapFactory.decodeFile(picturePath, bmOptions);
        //Bitmap imageBM = intent.getExtras().getParcelable("image"); needed for the order version


        //ImageAdapter imageAdapter = new ImageAdapter(this);

        //imageView.setImageResource(imageAdapter.imageArray[position]);
        imageView.setImageBitmap(imageBM);

        //file for audio caption storage
        fileName = getApplicationContext().getFilesDir().getPath();
        fileName += "/audio" + position + ".3gp";
        hasCaption = isHasCaption();
        captionButton.setImageResource(captionButtonImage());

        //file for text caption storage
        textCaptionFileName = "text" + position + ".txt";
        hasTextCaption = isHasTextCaption();


        //Request permission to write in storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
            ActivityCompat.requestPermissions(this, permissions, 200);
        }

        ///

    }



    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {

                    try {
                        Geocoder geocoder = new Geocoder(FullScreenImage.this, Locale.getDefault());
                        List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        locationInpu.setText(Html.fromHtml(address.get(0).getCountryName()));


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        }

    @Override
    //release file from player
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }
    //End of override methods

    //If there is text caption, show it. Otherwise make one.
    private void onClickTextCaptionButton(Context c) {
        if (hasTextCaption) {
            DisplayTextCaption(c);
        }
        else {
            RecordTextCaption(c);
        }
    }
    
    //Record the text caption
    private void RecordTextCaption(Context c) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Add a new text caption")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textCaption = valueOf(taskEditText.getText());
                        //saveTextCaptionAsFile(textCaptionFileName, textCaption);
                        saveFile(textCaptionFileName, textCaption);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
    //Display the text caption
    private void DisplayTextCaption(Context c) {
        String text = readFile(textCaptionFileName);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Saved Text Caption")
                .setMessage(text)
                .setNegativeButton("Close", null)
                .create();
        dialog.show();
    }
    //Save the text caption
    private void saveTextCaptionAsFile(String textCaptionFileName, String content) {
        String fileName = textCaptionFileName + ".txt";

        File file = new File(fileName);


        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
            Toast.makeText(this, "TEXT CAPTION SAVED", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "FILE NOT FOUND", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR SAVING", Toast.LENGTH_LONG).show();
        }
    }
    //Save the file
    private void saveFile(String file, String text) {
        try {
            FileOutputStream fos = openFileOutput(file, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
            hasTextCaption = true;
            Toast.makeText(this, "SAVED", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR SAVING", Toast.LENGTH_LONG).show();
        }
    }

    //Open the file
    private String readFile(String file) {
        String text = "";

        try {
            FileInputStream fis = openFileInput(file);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            text = new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR READING", Toast.LENGTH_LONG).show();
        }

        return text;
    }







    ///


    //Set up the audio caption button to record, playback, or stop, as is appropriate
    public void onClickAudioCaptionButton(View view){

        if(hasCaption){
            if(isPlaying){
                stopPlaying();
            }
            else {
                Toast.makeText(this,"PLAYING!",Toast.LENGTH_LONG).show();
                startPlaying();
            }
        }
        else{
            if(isRecording){
                stopRecording();
            }
            else{
                Toast.makeText(this, "RECORDING!",Toast.LENGTH_LONG).show();
                startRecording();
            }
        }

    }

    //Delete function action
    public void onLongClickCaptionButton(View view){
        if(getDatabasePath(fileName).exists()){
            deleteDatabase(fileName);
            hasCaption = false;
            captionButton.setImageResource(captionButtonImage());
            Toast.makeText(this,"DELETED!", Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(this,"No file is found",Toast.LENGTH_SHORT).show();

    }
    //ends of onclick

    //Methods for caption button
    
    //Method to record audio
    private void startRecording() {
        ActivityCompat.requestPermissions(this,permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        isRecording = true;
        captionButton.setImageResource(R.drawable.ic_action_stop);
        recorder.start();
    }
    //Method to end recording audio
    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        isRecording = false;
        hasCaption = true;
        captionButton.setImageResource(captionButtonImage());
    }
    //Method to start playing audio
    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            isPlaying = true;
            captionButton.setImageResource(R.drawable.ic_action_stop);
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }
    //Method to stop playing audio
    private void stopPlaying() {
        if(player != null && isPlaying) {
            player.release();
            player = null;
            isPlaying = false;
            captionButton.setImageResource(captionButtonImage());
        }
    }
    //ends of method for caption button

    //other methods
    
    //See if there is caption
    public boolean isHasCaption(){
        if (getDatabasePath(fileName).exists()){
            return true;
        }
        return false;
    }
    //See if there is text caption
    public boolean isHasTextCaption(){
        if (getDatabasePath(getApplicationContext().getFilesDir().getPath()+ "/" + textCaptionFileName).exists()){
            return true;
        }
        return false;
    }
    //Set caption button to show if there is a caption or not
    public int captionButtonImage(){
        if(hasCaption)return R.drawable.ic_action_play;
        else return R.drawable.ic_action_record;
    }
    //Tag section
    public void openDialog() {
        Pop exampleDialog = new Pop();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }
    //set text to tag
    @Override
    public void applyTexts(String tag1) {
        FileUtil.saveFile(tag1, "save.txt");

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
