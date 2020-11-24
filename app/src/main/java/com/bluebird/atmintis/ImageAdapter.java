/**
* File: Image Adapter
*
* This file creates our image gallery grid
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
import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    
    //Where the images are ordered
    public int[] imageArray = {
        R.drawable.image_1, R.drawable.image_2, R.drawable.image_3, R.drawable.image_4,
        R.drawable.image_5, R.drawable.image_6, R.drawable.image_7, R.drawable.image_8,
        R.drawable.image_9, R.drawable.image_10, R.drawable.image_11
    };

    //Constructor
    public ImageAdapter(Context mContext) {
        this.mContext = mContext;
    }

    //Override methods
    @Override
    public int getCount() {
        return imageArray.length;
    }

    @Override
    public Object getItem(int position) {
        return imageArray[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //Sets up the gallery view.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView galleryImageView = new ImageView(mContext);
        galleryImageView.setImageResource(imageArray[position]);
        galleryImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        galleryImageView.setLayoutParams(new ViewGroup.LayoutParams(340, 350));

        return galleryImageView;
    }
}
