package com.martin.opencv4android;

import android.graphics.Bitmap;

/**
 * Created by adarsh on 14/9/16.
 */
public class DetailItem {
    private Bitmap bitmap;
    private String path;

    public DetailItem() {
    }

    public DetailItem(Bitmap bitmap, String path) {
        this.bitmap = bitmap;
        this.path = path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
    public String getPath() {
        return path;
    }
}