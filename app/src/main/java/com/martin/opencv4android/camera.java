package com.martin.opencv4android;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class camera extends AppCompatActivity {


    Button capture,ok,cancel;
    Handler photo=null;
    File pictureFile=null;
    int cFlag=0;
    Runnable rr;
    RadioButton single,batch;
    android.hardware.Camera camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        capture=(Button)findViewById(R.id.capture);
        final SurfaceView sv;
        sv=(SurfaceView)findViewById(R.id.surfaceView);
        camera= android.hardware.Camera.open(0);
        cancel=(Button)findViewById(R.id.cancel);
        ok=(Button)findViewById(R.id.ok);

        single=(RadioButton)findViewById(R.id.single);


        final Runnable mRunnalbe=new Runnable() {
            @Override
            public void run() {


                try{

                    android.hardware.Camera.Parameters params = camera.getParameters();
                    params.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    camera.setParameters(params);
                    camera.setPreviewDisplay(sv.getHolder());
                    camera.setDisplayOrientation(90);
                    camera.startPreview();

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        };
        final Handler mHandler=new Handler();
        mHandler.postDelayed(mRunnalbe, 2000);
        photo=new Handler();
        rr=new Runnable() {
            @Override
            public void run() {
                camera.takePicture(null,null,jpegCallBack);
            }
        };
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo.postDelayed(rr,0);
                ok.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                capture.setVisibility(View.INVISIBLE);
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(camera.this,gallery.class);
                i.putExtra("path",pictureFile.toString());
                startActivity(i);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    pictureFile.delete();
                    camera.startPreview();
                    ok.setVisibility(View.INVISIBLE);
                    cancel.setVisibility(View.INVISIBLE);
                    capture.setVisibility(View.VISIBLE);
            }
        });
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Scanner");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
            else{
                Log.d("Scanner", "success");
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");


        return mediaFile;
    }


    final android.hardware.Camera.PictureCallback jpegCallBack=new android.hardware.Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {


            pictureFile = getOutputMediaFile();
            Toast.makeText(getApplicationContext(), ""+pictureFile.toString(), Toast.LENGTH_SHORT).show();
            Log.d("Time", "getting ouputmedia..");
            if (pictureFile == null) {
                return;
            }
            //if(cFlag==1) {
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    cFlag=0;
                    Log.d("MyCameraApp", "writing photo...");
                    fos.close();
                } catch (FileNotFoundException e) {

                } catch (IOException e) {
                }
        //    }
        }
    };

}
