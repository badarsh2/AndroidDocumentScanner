package com.martin.opencv4android;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ImageView iv_show_img;
    private PolygonView polygonView;
    private String img_Decodable_Str;
    private Button scan;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_show_img = (ImageView) findViewById(R.id.iv_show_img);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new ScanButtonClickListener());
        // scan = (Button) findViewById(R.id.button1);

        Bundle bundle = getIntent().getExtras();

        if(bundle.getInt("PASSING")== 1)
        {
            openCamera();
        }
        else if(bundle.getInt("PASSING")==2) {
            openMediaContent();
        }
        // scan.setOnClickListener(new ScanButtonClickListener());
    }

    private class CameraButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            openCamera();
        }
    }

    private class GalleryClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            openMediaContent();
        }
    }

    public void openMediaContent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, ScanConstants.PICKFILE_REQUEST_CODE);
    }

    public void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // File file = createImageFile();
        // file.getParentFile().mkdirs();
        // fileUri = Uri.fromFile(file);
        // if (file != null) {
        // cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(cameraIntent, ScanConstants.START_CAMERA_REQUEST_CODE);
        // }
    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//        super.onActivityResult(requestCode, resultCode, data);
//
//        Bitmap bp = (Bitmap) data.getExtras().get("data");
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    private File createImageFile() {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
//                Date());
//        File file = new File(ScanConstants.IMAGE_PATH, "IMG_" + timeStamp +
//                ".jpg");
//        return file;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        try {
            // When an Image is picked
            if (requestCode == ScanConstants.PICKFILE_REQUEST_CODE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                img_Decodable_Str = cursor.getString(columnIndex);
                cursor.close();
                bitmap = Bitmap.createBitmap(BitmapFactory.decodeFile(img_Decodable_Str));
                Log.d("OCV", "here");
                // bitmap = getResizedBitmap(bitmap, 1080, 1920);
                // ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                // byte[] byteArray = stream.toByteArray();
                // Intent intent = new Intent(this, MainActivity.class);
                // intent.putExtra("picture", byteArray);
                // createImageFromBitmap(bitmap);
                // startActivity(intent);
                iv_show_img.setImageBitmap(bitmap);
                polygonView = (PolygonView) findViewById(R.id.polygonView);
                Map<Integer, PointF> pointFs = getEdgePoints(bitmap);
                float scalex = iv_show_img.getWidth()/bitmap.getWidth();
                float scaley = iv_show_img.getHeight()/bitmap.getHeight();
                polygonView.setPoints(pointFs);
                polygonView.setVisibility(View.VISIBLE);
                int padding = (int) getResources().getDimension(R.dimen.scanPadding);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
                layoutParams.gravity = Gravity.CENTER;
                polygonView.setLayoutParams(layoutParams);
            }

            else if (requestCode == ScanConstants.START_CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
                bitmap = Bitmap.createBitmap((Bitmap) data.getExtras().get("data"));
                // bitmap = getResizedBitmap(bitmap, 720, 1280);
                iv_show_img.setImageBitmap(bitmap);
                polygonView = (PolygonView) findViewById(R.id.polygonView);
                Map<Integer, PointF> pointFs = getEdgePoints(bitmap);
                float scalex = iv_show_img.getWidth()/bitmap.getWidth();
                float scaley = iv_show_img.getHeight()/bitmap.getHeight();
                polygonView.setPoints(pointFs);
                polygonView.setVisibility(View.VISIBLE);
                int padding = (int) getResources().getDimension(R.dimen.scanPadding);
                // FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(bitmap.getWidth() + 2 * padding, bitmap.getHeight() + 2 * padding);
                // layoutParams.gravity = Gravity.CENTER;
                // polygonView.setLayoutParams(layoutParams);
            }
            else {
                Toast.makeText(this, "Hey pick your image first",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went embrassing", Toast.LENGTH_LONG)
                    .show();
        }
    }

    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "myImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    public Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    public void onCLickToDetetFeatures(View view) {

    }




    @NonNull
    private Bitmap getBlackBitmap(Bitmap bitmap) {

        int w = bitmap.getWidth(), h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int[] resultPixes = OpenCVHelper.gray(pix, w, h);
        int cornerpts[];
        cornerpts = OpenCVHelper.getBoxPoints(pix, w, h);
        for(int i = 0; i<cornerpts.length; i++) {
            Log.d("Scanned points", String.valueOf(cornerpts[i]));
        }
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        result.setPixels(resultPixes, 0, w, 0, 0, w, h);
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

    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
        Map<Integer, PointF> orderedPoints = polygonView.getOrderedPoints(pointFs);
        if (!polygonView.isValidShape(orderedPoints)) {
            orderedPoints = getOutlinePoints(tempBitmap);
        }
        return orderedPoints;
    }

    private Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap) {
        Map<Integer, PointF> outlinePoints = new HashMap<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth(), 0));
        outlinePoints.put(2, new PointF(0, tempBitmap.getHeight()));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth(), tempBitmap.getHeight()));
        return outlinePoints;
    }

    private class ScanButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Map<Integer, PointF> points = polygonView.getPoints();
            Log.d("OpenCV4Android", String.valueOf(points.size()));
            Bitmap op = getScannedBitmap(bitmap, points);
            iv_show_img.setImageBitmap(op);
            if (isScanPointsValid(points)) {
//                Bitmap op = getScannedBitmap(bitmap, points);
//                iv_show_img.setImageBitmap(op);
            } else {
                // showErrorDialog();
            }
        }
    }

    private Bitmap getScannedBitmap(Bitmap original, Map<Integer, PointF> points) {
        int width = original.getWidth();
        int height = original.getHeight();
        float xRatio = (float) original.getWidth() / iv_show_img.getWidth();
        float yRatio = (float) original.getHeight() / iv_show_img.getHeight();
        // float xRatio = 1;
        // float yRatio = 1;

        int[] newpoints = {0,0,0,0,0,0,0,0};
        newpoints[0] = (int)((points.get(0).x) * xRatio);
        newpoints[1] = (int)((points.get(0).y) * xRatio);
        newpoints[2] = (int)((points.get(1).x) * xRatio);
        newpoints[3] = (int)((points.get(1).y) * xRatio);
        newpoints[4] = (int)((points.get(2).x) * yRatio);
        newpoints[5] = (int)((points.get(2).y) * yRatio);
        newpoints[6] = (int)((points.get(3).x) * yRatio);
        newpoints[7] = (int)((points.get(3).y) * yRatio);
        Log.d("OpenCV", String.valueOf(points.get(0).x));
        Log.d("OpenCV", String.valueOf(points.get(0).y));
        Log.d("OpenCV", String.valueOf(points.get(1).x));
        Log.d("OpenCV", String.valueOf(points.get(1).y));
        Log.d("OpenCV", String.valueOf(points.get(2).x));
        Log.d("OpenCV", String.valueOf(points.get(2).y));
        Log.d("OpenCV", String.valueOf(points.get(3).x));
        Log.d("OpenCV", String.valueOf(points.get(3).y));
        // Log.d("", "POints(" + x1 + "," + y1 + ")(" + x2 + "," + y2 + ")(" + x3 + "," + y3 + ")(" + x4 + "," + y4 + ")");
        int[] pix = new int[width * height];
        original.getPixels(pix, 0, width, 0, 0, width, height);
        Log.d("OpenCV4Android", "Came here");
        int[] resultPixes = OpenCVHelper.perspective(pix, newpoints, width, height);
        Log.d("OpenCV4Android", "Checkpint");
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        result.setPixels(pix, 0, width, 0, 0, width, height);
        polygonView.setVisibility(View.GONE);
        return result;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    protected void showProgressDialog(String message) {
//        progressDialogFragment = new ProgressDialogFragment(message);
//        FragmentManager fm = getFragmentManager();
//        progressDialogFragment.show(fm, ProgressDialogFragment.class.toString());
    }

    protected void dismissDialog() {
        // progressDialogFragment.dismissAllowingStateLoss();
    }

    private boolean isScanPointsValid(Map<Integer, PointF> points) {
        return points.size() == 4;
    }

}
