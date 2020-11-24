package com.bluebird.atmintis;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;

public class AudioCaptionButton extends CaptionButton{

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    boolean hasTextCaption = false;
    boolean isRecording = false;
    boolean isPlaying = false;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    //Clicker variable that provides functions of playing and recording
    OnClickListener clicker = new OnClickListener(){
        public void onClick(View v){
            if(hasCaption){
                if(isHasTextCaption()) {tts();}
                else{
                    if (isPlaying) {
                        stopPlaying();
                    } else {
                        Toast.makeText(getContext(), "PLAYING!", Toast.LENGTH_LONG).show();
                        startPlaying();
                    }
                }
            }
            else{
                if(isRecording){
                    stopRecording();
                }
                else{
                    Toast.makeText(getContext(), "RECORDING!",Toast.LENGTH_LONG).show();
                    startRecording();
                }
            }
        }
    };

    //Long clicker variable that provides function of deleting
    OnLongClickListener longClicker = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            if(getContext().getDatabasePath(storageAddress).exists()){
                getContext().deleteDatabase(storageAddress);
                hasCaption = false;
                setImageResource(captionButtonImage());
                Toast.makeText(getContext(),"DELETED!", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(getContext(),"No file is found",Toast.LENGTH_SHORT).show();

            return false;
        }
    };

    //constructor method
    public AudioCaptionButton(@NonNull Context context) {
        super(context);
        //setting clicker on initiation
        setOnClickListener(clicker);
        setOnLongClickListener(longClicker);
    }

    //method for audio caption button
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(storageAddress);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        isRecording = true;
        setImageResource(R.drawable.ic_action_stop);
        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        isRecording = false;
        hasCaption = true;
        setImageResource(captionButtonImage());
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(storageAddress);
            player.prepare();
            isPlaying = true;
            setImageResource(R.drawable.ic_action_stop);
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        if(player != null && isPlaying) {
            player.release();
            player = null;
            isPlaying = false;
            setImageResource(captionButtonImage());
        }
    }
    //ends of method for audio caption button

    public boolean isHasTextCaption(){
        return hasTextCaption;
    }

    @Override
    public boolean isHasCaption(){
        return hasCaption;
    }

    public void tts(){}



}
