package com.bluebird.atmintis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

// Jack
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;

/// Jack
import android.widget.Button;
import android.view.View;
///

public class FullScreenImage extends AppCompatActivity {

    //TODO: Create Button instances

    /// Jack - TEXT CAPTION
    boolean hasTextCaption;
    private static String textCaption;
    private static String textCaptionFileName = null;

    ///



    ImageView imageView;

    //use for CaptionButton
    //private CaptionButton captionButton = null;
    private static final String LOG_TAG = "AudioRecordTest";
    boolean hasCaption;
    boolean isRecording = false;
    boolean isPlaying = false;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private static String fileName = null;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;


    //Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO}; //permission list

    //Override Method
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission NOT GRANTED", Toast.LENGTH_SHORT).show();
                    finish();
                }
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;

        }
        if (!permissionToRecordAccepted ) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        imageView = (ImageView) findViewById(R.id.image_view);


        /// Jack - AUDIO CAPTION
        FloatingActionButton audioCaptionButton = (FloatingActionButton) findViewById(R.id.audioPlayFab);
        audioCaptionButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        onClickAudioCaptionButton(v);
                    }
                }
        );
        ///

        /// Jack - TEXT CAPTION
        FloatingActionButton textCaptionButton = (FloatingActionButton) findViewById(R.id.addCaptionFab);
        textCaptionButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v)
                    {
                        onClickTextCaptionButton(FullScreenImage.this);
                    }
                }
        );

        ///

        getSupportActionBar().hide();

        Intent intent = getIntent();
        int position = intent.getExtras().getInt("id");
        ImageAdapter imageAdapter = new ImageAdapter(this);
        imageView.setImageResource(imageAdapter.imageArray[position]);

        //initiate file for audio storage
        fileName = getApplicationContext().getFilesDir().getPath();
        fileName += "/audio"+position+".3gp";
        hasCaption = isHasCaption();

        /// Jack - TEXT CAPTION
        textCaptionFileName = "text"+position+".txt";
        hasTextCaption = isHasTextCaption();




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
            ActivityCompat.requestPermissions(this,permissions, 200);
        }


        ///

    }

    @Override
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
    //Ends of override method

    /// JACK - Text Caption

    private void onClickTextCaptionButton(Context c) {
        if (hasTextCaption) {
            DisplayTextCaption(c);
        }
        else {
            RecordTextCaption(c);
        }
    }

    private void RecordTextCaption(Context c) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Add a new text caption")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textCaption = String.valueOf(taskEditText.getText());
                        //saveTextCaptionAsFile(textCaptionFileName, textCaption);
                        saveFile(textCaptionFileName, textCaption);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void DisplayTextCaption(Context c) {
        String text = readFile(textCaptionFileName);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Saved Text Caption")
                .setMessage(text)
                .setNegativeButton("Close", null)
                .create();
        dialog.show();
    }

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


    //onClick
    public void onClickAudioCaptionButton(View view){

        if(!hasCaption && !isRecording){
            Toast.makeText(this, "RECORDING!",Toast.LENGTH_LONG).show();
            startRecording();
        }
        else if (isRecording){
            Toast.makeText(this, "RECORDED!",Toast.LENGTH_LONG).show();
            stopRecording();
        }
        else if(hasCaption && !isRecording){
            Toast.makeText(this,"PLAYING!",Toast.LENGTH_LONG).show();
            startPlaying();
        }
        else if(isPlaying){
            stopPlaying();
        }

    }
    //ends of onclick

    //method for caption button
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
        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        isRecording = false;
        hasCaption = true;
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            isPlaying = true;
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

    }

    private void stopPlaying() {
        player.release();
        player = null;
        isPlaying = false;
    }
    //ends of method for caption button

    //other method
    public boolean isHasCaption(){
        if (getDatabasePath(fileName).exists()){
            return true;
        }
        return false;
    }

    public boolean isHasTextCaption(){
        if (getDatabasePath(getApplicationContext().getFilesDir().getPath()+ "/" + textCaptionFileName).exists()){
            return true;
        }
        return false;
    }
}