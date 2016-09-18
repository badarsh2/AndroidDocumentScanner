package com.martin.opencv4android;

/**
 * Created by donghongyu on 2016/8/17.
 */

public class OpenCVHelper {
    static {
        System.loadLibrary("OpenCV");
    }

    public static native int[] gray(int[] buf, int w, int h);

    public static native int[] getBoxPoints(int[] buf, int w, int h);

    public static native int[] perspective(int[] buf, int[] points, int w, int h);
}
