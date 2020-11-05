package com.bluebird.atmintis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

// Jack
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

/// Jack
import android.widget.Button;
import android.view.View;
///

public class FullScreenImage extends AppCompatActivity {

    //TODO: Create Button instances
    // Jack


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

        /// Jack
        FloatingActionButton audioCaptionButton = (FloatingActionButton) findViewById(R.id.audioPlayFab);
        audioCaptionButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        onClickCaptionButton(v);
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

    //onClick
    public void onClickCaptionButton(View view){

        if(!hasCaption && !isRecording){
            Toast.makeText(this, "RECORDING!",Toast.LENGTH_LONG).show();
            startRecording();
        }
        else if (isRecording){
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
}