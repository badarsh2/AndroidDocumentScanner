package com.martin.opencv4android;

import android.graphics.Bitmap;

/**
 * Created by adarsh on 14/9/16.
 */
public class DetailItem {
    private Bitmap bitmap;

    public DetailItem() {
    }

    public DetailItem(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}