package com.martin.opencv4android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.Math.max;


/**
 * Created by k002 on 17/10/16.
 */
public class PolygonViewScreen extends AppCompatActivity {
    private ImageView imageView;
    private Uri uri;
    private PolygonView polygonView;
    private Uri imageUriFromGallery,imageUriFromCamera,test_uri,imageUriFromBatch;
    private Bitmap bitmap, op, op2,tempBitmap;
    private int status = 0;
    private FileOutputStream fos;
    private float perswidth, persheight;
    private Bitmap original,scaledBitmap,newimage;
    private FrameLayout sourceFrame;
    private LinearLayout graylayout,bwlayout,enhacedlayout,lightenlayout,normallayout,layout_color;
    private ImageView gray,bw,enhaced,lighten,normal;
    private TextView tgray,tbw,tenhanced,tlighten,tnormal;
    private static String test;
    private  Uri test1;
    private Matrix mat;
    Map<Integer, PointF> pointFs;
    private RelativeLayout grievanceProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.img1);
        polygonView = (PolygonView) findViewById(R.id.polygonView);
        sourceFrame = (FrameLayout) findViewById(R.id.sourceFrame);
        grievanceProgressBar = (RelativeLayout) findViewById(R.id.grievanceProgressBar);

        ImageView submit = (ImageView) findViewById(R.id.submit);
        ImageView cancel = (ImageView) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ImageView rotateleft = (ImageView) findViewById(R.id.rotateleft);
        rotateleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap rotatedBitmap = rotateImage(((BitmapDrawable)imageView.getDrawable()).getBitmap(), -90);
                imageView.setImageBitmap(rotatedBitmap);
            }
        });
        ImageView rotateright = (ImageView) findViewById(R.id.rotateright);
        rotateright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap rotatedBitmap = rotateImage(((BitmapDrawable)imageView.getDrawable()).getBitmap(), 90);
                imageView.setImageBitmap(rotatedBitmap);
            }
        });

        sourceFrame = (FrameLayout) findViewById(R.id.sourceFrame);
        sourceFrame.post(new Runnable() {
            @Override
            public void run() {


                original = getBitmap();
                original = rotateImage(original, 90);

                if (original != null) {
                    setBitmap(original);
                }
            }
        });

        submit.setOnClickListener(new ScanButtonClickListener());

    }

    private Bitmap getBitmap() {
        Uri uri = getUri();
        try {
            bitmap = Utils.getBitmap(PolygonViewScreen.this, uri);
            this.getContentResolver().delete(uri, null, null);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri getUri() {
        imageUriFromGallery = getIntent().getParcelableExtra("imageTest1");
        imageUriFromCamera = getIntent().getParcelableExtra("imageTest");
        imageUriFromBatch = getIntent().getParcelableExtra("image2");

        if (imageUriFromGallery != null && imageUriFromBatch == null) {
            uri = getIntent().getParcelableExtra("imageTest1");
            Log.i("uri test gallery",uri.toString());

        } else if(imageUriFromCamera != null) {
            uri = getIntent().getParcelableExtra("imageTest");
            Log.i("uri test cameraSingle",uri.toString());
        } else {
            uri = getIntent().getParcelableExtra("image2");
            Log.i("uri test cameraBatch",uri.toString());
        }

        test = uri.toString();
        test1 = uri;
        Log.i("test test ",test.toString());
        Log.i("test test2 ",test1.toString());

        return uri;
    }

    private void setBitmap(Bitmap original) {


        scaledBitmap = scaledBitmap(original, sourceFrame.getWidth(), sourceFrame.getHeight());
        imageView.setImageBitmap(scaledBitmap);
        new EdgeAsyncTask(scaledBitmap).execute();

    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    private boolean isScanPointsValid(Map<Integer, PointF> points) {
        return points.size() == 4;
    }

    private class ScanButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(status == 0) {
                Map<Integer, PointF> points = polygonView.getPoints();
                if (isScanPointsValid(points)) {
                    //AsynCal
                    new ScanAsyncTask(points).execute();

                } else {
                    Toast.makeText(PolygonViewScreen.this, "error,", Toast.LENGTH_SHORT).show();
                }
            }else if(status == 1) {

                Uri uri = Utils.getUri(PolygonViewScreen.this, op2);
                // op2.recycle();

                Log.i("test uri",uri.toString());
                Intent in = new Intent(PolygonViewScreen.this,ColorImageActivity.class);
                in.putExtra("image", uri);
                startActivity(in);
                finish();

            }
        }
    }



    private class ScanAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        private Map<Integer, PointF> points;

        public ScanAsyncTask(Map<Integer, PointF> points) {
            this.points = points;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // Toast.makeText(PolygonViewScreen.this,"Scanning",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    try {

                        original = Utils.getBitmap(PolygonViewScreen.this, uri);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    op = getScannedBitmap(original, points);
                    // perswidth = points[0]
                    Log.d("MainActivity", "Sizes are" + original.getWidth() + " " + original.getHeight());
                    if (perswidth > persheight) {
                        op2 = Bitmap.createScaledBitmap(op, 1600, 1000, false);
                    }
                    else {
                        op2 = Bitmap.createScaledBitmap(op, 1000, 1600, false);
                    }
                    imageView.setImageBitmap(op2);

                    if (isScanPointsValid(points)) {

                    } else {
                        // showErrorDialog();
                    }
                    status = 1;


                }
            });
            return original;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
//            layout_color.setVisibility(View.VISIBLE);
        }
    }

    private Bitmap getScannedBitmap(Bitmap original, Map<Integer, PointF> points) {

        int width = original.getWidth();
        int height = original.getHeight();
        float xRatio = (float) original.getWidth() / imageView.getWidth();
        float yRatio = (float) original.getHeight() / imageView.getHeight();
        Log.d("width", width+"");
        Log.d("height", height+"");
        Log.d("xRatio", xRatio+"");
        Log.d("yRatio", yRatio+"");


        int[] newpoints = {0, 0, 0, 0, 0, 0, 0, 0};
        newpoints[0] = (int) ((points.get(0).x) * xRatio);
        newpoints[1] = (int) ((points.get(0).y) * yRatio);
        newpoints[2] = (int) ((points.get(1).x) * xRatio);
        newpoints[3] = (int) ((points.get(1).y) * yRatio);
        newpoints[4] = (int) ((points.get(2).x) * xRatio);
        newpoints[5] = (int) ((points.get(2).y) * yRatio);
        newpoints[6] = (int) ((points.get(3).x) * xRatio);
        newpoints[7] = (int) ((points.get(3).y) * yRatio);
        Log.d("OpenCV", "helo stated");
        Log.d("OpenCV", String.valueOf(newpoints[0]));
        Log.d("OpenCV", String.valueOf(newpoints[1]));
        Log.d("OpenCV", String.valueOf(newpoints[2]));
        Log.d("OpenCV", String.valueOf(newpoints[3]));
        Log.d("OpenCV", String.valueOf(newpoints[4]));
        Log.d("OpenCV", String.valueOf(newpoints[5]));
        Log.d("OpenCV", String.valueOf(newpoints[6]));
        Log.d("OpenCV", String.valueOf(newpoints[7]));


        perswidth = max(abs(newpoints[2] - newpoints[0]), abs(newpoints[4] - newpoints[0]));
        persheight = max(abs(newpoints[3] - newpoints[1]), abs(newpoints[5] - newpoints[1]));
        // Log.d("", "POints(" + x1 + "," + y1 + ")(" + x2 + "," + y2 + ")(" + x3 + "," + y3 + ")(" + x4 + "," + y4 + ")");
        int[] pix = new int[width * height];
        original.getPixels(pix, 0, width, 0, 0, width, height);
        Log.d("OpenCV4Android", "Came here");
        int[] resultPixes = OpenCVHelper.perspective(pix, newpoints, width, height);
        Log.d("OpenCV4Android", "Checkpint");
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        result.setPixels(resultPixes, 0, width, 0, 0, width, height);
        polygonView.setVisibility(View.GONE);
        return result;
    }

    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap) {
        List<PointF> pointFs = getContourEdgePoints(tempBitmap);
        Map<Integer, PointF> orderedPoints = orderedValidEdgePoints(tempBitmap, pointFs);
        return orderedPoints;
    }

    private List<PointF> getContourEdgePoints(Bitmap tempBitmap) {
        int w = tempBitmap.getWidth(), h = tempBitmap.getHeight();
        int[] pix = new int[w * h];
        tempBitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int[] points = OpenCVHelper.getBoxPoints(pix, w, h);
        float x1 = points[0];
        float y1 = points[1];
        float x2 = points[2];
        float y2 = points[3];

        float x3 = points[4];
        float y3 = points[5];
        float x4 = points[6];
        float y4 = points[7];



        List<PointF> pointFs = new ArrayList<>();
        pointFs.add(new PointF(x1, y1));
        pointFs.add(new PointF(x2, y2));
        pointFs.add(new PointF(x3, y3));
        pointFs.add(new PointF(x4, y4));
        return pointFs;
    }

    private Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap) {
        Map<Integer, PointF> outlinePoints = new HashMap<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth(), 0));
        outlinePoints.put(2, new PointF(0, tempBitmap.getHeight()));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth(), tempBitmap.getHeight()));
        return outlinePoints;
    }

    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
        Map<Integer, PointF> orderedPoints = polygonView.getOrderedPoints(pointFs);
        if (!polygonView.isValidShape(orderedPoints)) {
            orderedPoints = getOutlinePoints(tempBitmap);
        }
        return orderedPoints;
    }

    private class EdgeAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        public EdgeAsyncTask(Bitmap tempBitmap) {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tempBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            pointFs = getEdgePoints(tempBitmap);

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            polygonView.setPoints(pointFs);
            polygonView.setVisibility(View.VISIBLE);
            int padding = (int) getResources().getDimension(R.dimen.scanPadding);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
            layoutParams.gravity = Gravity.CENTER;
            polygonView.setLayoutParams(layoutParams);
            grievanceProgressBar.setVisibility(View.GONE);

        }
    }

   /* static {
        System.loadLibrary("OpenCV");

    }
*/
}