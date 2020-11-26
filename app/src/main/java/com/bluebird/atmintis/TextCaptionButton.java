package com.bluebird.atmintis;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

public class TextCaptionButton extends CaptionButton{

    private static String textCaption;


    OnClickListener clicker = new OnClickListener() {
        @Override
        public void onClick(View view){
        }
    };

    public TextCaptionButton(@NonNull Context context) {
        super(context);
        setOnClickListener(clicker);
    }

    @Override
    public boolean isHasCaption() {
        return hasCaption;
    }
}


