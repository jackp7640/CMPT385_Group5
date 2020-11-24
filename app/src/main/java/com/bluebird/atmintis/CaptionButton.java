package com.bluebird.atmintis;

import android.Manifest;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CaptionButton extends FloatingActionButton {

    //Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO}; //permission list

    boolean hasCaption;
    public OnClickListener clicker = null;
    public OnLongClickListener longClicker = null;
    public static String storageAddress = null;

    //Constructor method
    public CaptionButton(@NonNull Context context) {
        super(context);


        //setting icon base on whether caption exist
        setImageResource(captionButtonImage());
    }

    //determining whether there is a caption association
    public boolean isHasCaption(){return false;}

    //base on result above to change the icon of the button
    public int captionButtonImage(){
        return R.drawable.ic_action_add;
    }











}
