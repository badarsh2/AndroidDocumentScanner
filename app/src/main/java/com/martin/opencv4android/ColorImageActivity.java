package com.martin.opencv4android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

/**
 * Created by k002 on 1/11/16.
 */
public class ColorImageActivity extends AppCompatActivity {
    private FrameLayout sourceFrame;
    private LinearLayout graylayout,bwlayout,enhacedlayout,lightenlayout,normallayout,layout_color;
    private ImageView gray,bw,enhaced,lighten,normal,actual_image;
    private TextView tgray,tbw,tenhanced,tlighten,tnormal;
    private Uri uri;

    private Bitmap original,bitmap;
    private FileOutputStream fos;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_scanned_image);
        // Toast.makeText(ColorImageActivity.this,"hi",Toast.LENGTH_SHORT).show();
        actual_image = (ImageView)findViewById(R.id.img11);

        layout_color = (LinearLayout)findViewById(R.id.layout_color);

        graylayout = (LinearLayout)findViewById(R.id.llgray);
        bwlayout = (LinearLayout)findViewById(R.id.llbw);
        enhacedlayout = (LinearLayout)findViewById(R.id.llenhanced);
        lightenlayout = (LinearLayout)findViewById(R.id.lllighten);
        normallayout = (LinearLayout)findViewById(R.id.llnormal);

        gray=(ImageView)findViewById(R.id.myImageViewGray);
        bw=(ImageView)findViewById(R.id.myImageViewLighten);
        enhaced=(ImageView)findViewById(R.id.myImageViewEnhance);
        lighten=(ImageView)findViewById(R.id.myImageViewBW);
        normal=(ImageView)findViewById(R.id.myImageViewNormal);

        tgray =(TextView)findViewById(R.id.gray);
        tbw =(TextView)findViewById(R.id.bw);
        tenhanced =(TextView)findViewById(R.id.enhance);
        tlighten =(TextView)findViewById(R.id.light);
        tnormal =(TextView)findViewById(R.id.normal);


        fab = (FloatingActionButton) findViewById(R.id.submit);

        uri = getIntent().getParcelableExtra("image");

        original = getBitmap();

        actual_image.setImageBitmap(original);


        fab.setOnClickListener(new ScanButtonClickListener());




        graylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Bitmap transfor = OpenCVHelper.getGrayBitmapp(original);
                actual_image.setImageBitmap(transfor);
            }
        });

        bwlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(PolygonViewScreen.this,"hell",Toast.LENGTH_SHORT).show();
                Bitmap transfor = OpenCVHelper.getBlackWhiteBitmapp(original);
                actual_image.setImageBitmap(transfor);

            }
        });


        enhacedlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Bitmap transfor = OpenCVHelper.getMagicBitmapp(original);
                actual_image.setImageBitmap(transfor);
            }
        });



        lightenlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Bitmap transfor = OpenCVHelper.getLightedBitmapp(original);
                actual_image.setImageBitmap(transfor);
            }
        });


        normallayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bitmap  transformed = original;
                actual_image.setImageBitmap(transformed);


            }
        });

    }

    private class ScanButtonClickListener implements View.OnClickListener {


        @Override
        public void onClick(View view) {
            popup_request(((BitmapDrawable)actual_image.getDrawable()).getBitmap());
        }



    }

    private void popup_request(final Bitmap b) {


        int i = (int) (new Date().getTime()/1000);
        String name = String.valueOf(i);
        String val = name;
        new SaveImageToStorageAsynTask(val,b).execute();
        Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_LONG).show();
        finish();
    }

    //ASyn Task
    private class SaveImageToStorageAsynTask extends AsyncTask<String, Void, Bitmap> {

        int i = (int) (new Date().getTime()/1000);
        String name = String.valueOf(i);

        String namenew = "Images";

        String val = name;
        Bitmap bitmapImage;



        public SaveImageToStorageAsynTask(String val, Bitmap bitmapImage) {
            this.namenew = val;
            this.bitmapImage=bitmapImage;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            File directory = new File("/sdcard/DocumentScanner/", "ScannedImage");
            // File directory = new File(android.os.Environment.getExternalStorageDirectory(),"DocumentScanner/" + "ScannedImage/"+namenew);//its create a separate folderinside the scanned image
            if(!directory.exists()) {
                directory.mkdirs();
                File dirthumb = new File(android.os.Environment.getExternalStorageDirectory(),"DocumentScanner/thumbnails");
                if(!dirthumb.exists()) {
                    dirthumb.mkdirs();
                }
                File mythumbpath=new File(dirthumb, namenew + ".jpg");

                try {
                    FileOutputStream fosthumb = new FileOutputStream(mythumbpath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fosthumb);
                    fosthumb.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            int i = (int) (new Date().getTime()/1000);
            String name = String.valueOf(i);
            String ts =  name.toString();
            File mypath=new File(directory, ts + ".jpg");

            try {
                fos = new FileOutputStream(mypath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {

        }

    }

    private Bitmap getBitmap() {
        Uri uri = getUri();
        try {
            bitmap = Utils.getBitmap(ColorImageActivity.this, uri);
            this.getContentResolver().delete(uri, null, null);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri getUri() {
        uri = getIntent().getParcelableExtra("image");
        return uri;
    }
}
