package com.martin.opencv4android;

import android.graphics.Bitmap;

/**
 * Created by adarsh on 14/4/16.
 */
public class DocItem {
    private String name, timestamp;
    private Bitmap bitmap;

    public DocItem() {
    }

    public DocItem(String name, String timestamp, Bitmap bitmap) {
        this.name = name;
        this.timestamp = timestamp;
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}