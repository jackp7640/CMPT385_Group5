package com.bluebird.atmintis;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

public class LocationCaptionButton extends CaptionButton{

    private static String locationCaption;


    OnClickListener clicker = new OnClickListener() {
        @Override
        public void onClick(View view){
        }
    };

    public LocationCaptionButton(@NonNull Context context) {
        super(context);
        setOnClickListener(clicker);
    }

    @Override
    public boolean isHasCaption() {
        return hasCaption;
    }
}

