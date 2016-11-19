package com.martin.opencv4android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by k002 on 24/10/16.
 */
public class CameraScreen extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    boolean previewing = false;
    LayoutInflater controlInflater = null;
    ImageView setting1,single,setting2,single_batch_page;
    Button camera_click,batch_click,front_camera,batch;


    private Uri fileUri,outPutfileUri;
    private TextView text;
    protected String imageFilePath;
    LinearLayout layout1,layout2,layout3;
    LinearLayout camera_preview;
    public static int count = 0;

    private ImageView pictureIv;
    private View pictureCountBgV;
    private TextView pictureCountTV;
    private  Camera.Parameters parameters;
    private boolean isFlashOn;
    static String formattedDate;
    private boolean cameraFront = false;

    LinearLayout setting_bef,setting_aft,camera_single,camera_batch,batch_bef,batch_aft,single_bef,single_afr,flashon,flashoff,settingTest;
    TextView textView;
    ImageView settingbefore,settingafter,single_mode,single_mode_again,batch_mode_before,batch_mode_after,settingtest;
    Button camerasingle,cameraBatch;
    LinearLayout extra_settings;
    ImageView flash_on,flash_off;
    private static Context myContext;
    LinearLayout picturecount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_camera_new);
        myContext = this;

        extra_settings= (LinearLayout)findViewById(R.id.extra_settings);
        camera_preview = (LinearLayout)findViewById(R.id.camera_preview);

        flashon =(LinearLayout)findViewById(R.id.flashon);
        flashoff = (LinearLayout)findViewById(R.id.flashoff);

        setting_bef =(LinearLayout)findViewById(R.id.setting_bef);
        setting_aft =(LinearLayout)findViewById(R.id.setting_aft);
        camera_single =(LinearLayout)findViewById(R.id.camera_single);
        camera_batch =(LinearLayout)findViewById(R.id.camera_batch);
        batch_bef =(LinearLayout)findViewById(R.id.batch_bef);
        batch_aft =(LinearLayout)findViewById(R.id.batch_aft);
        single_bef =(LinearLayout)findViewById(R.id.single_bef);
        single_afr =(LinearLayout)findViewById(R.id.single_afr);

        flash_off =(ImageView)findViewById(R.id.flash_off);
        flash_on = (ImageView)findViewById(R.id.flash_on);

        textView = (TextView)findViewById(R.id.textView);
        picturecount = (LinearLayout)findViewById(R.id.picturecount);

        pictureIv = (ImageView) findViewById(R.id.pictureIV);
        pictureCountBgV = (View) findViewById(R.id.pictureCountBgV);
        pictureCountTV = (TextView) findViewById(R.id.pictureCountTV);

        settingTest =(LinearLayout)findViewById(R.id.settingTest);
        settingtest = (ImageView)findViewById(R.id.settingtest);

        initialize();


    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext,
                    "Sorry, your phone does not have a camera!",
                    Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(this, "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
                front_camera.setVisibility(View.GONE);
            }
            mCamera = Camera.open(findBackFacingCamera());
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
        }
    }

    public void initialize() {


        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_SMS, Manifest.permission.CAMERA};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


        camera_preview = (LinearLayout) findViewById(R.id.camera_preview);
        mPreview = new CameraPreview(myContext, mCamera);
        camera_preview.addView(mPreview);

        extra_settings= (LinearLayout)findViewById(R.id.extra_settings);
        picturecount = (LinearLayout)findViewById(R.id.picturecount);

        setting_bef =(LinearLayout)findViewById(R.id.setting_bef);
        setting_bef.setOnClickListener(setting_befListener);

        setting_aft =(LinearLayout)findViewById(R.id.setting_aft);
        setting_aft.setOnClickListener(setting_aftListener);

        camera_single =(LinearLayout)findViewById(R.id.camera_single);
        // camera_single.setOnClickListener(camera_singleListener);

        camera_batch =(LinearLayout)findViewById(R.id.camera_batch);
        camera_batch.setOnClickListener(camera_batchListener);

        batch_bef =(LinearLayout)findViewById(R.id.batch_bef);
        batch_bef.setOnClickListener(batch_befListener);

        batch_aft =(LinearLayout)findViewById(R.id.batch_aft);
        batch_aft.setOnClickListener(batch_aftListener);

        single_bef =(LinearLayout)findViewById(R.id.single_bef);
        single_bef.setOnClickListener(single_befListener);

        single_afr =(LinearLayout)findViewById(R.id.single_afr);
        single_afr.setOnClickListener(single_afrListener);

        settingbefore = (ImageView)findViewById(R.id.settingbefore);
        settingafter = (ImageView)findViewById(R.id.settingafter);

        flashon =(LinearLayout)findViewById(R.id.flashon);
        flashon.setOnClickListener(flashOnListener);

        flashoff = (LinearLayout)findViewById(R.id.flashoff);
        flashoff.setOnClickListener(flashOffListener);

        pictureIv = (ImageView) findViewById(R.id.pictureIV);
        pictureIv.setOnClickListener(showPictureListener);

        settingTest =(LinearLayout)findViewById(R.id.settingTest);
        settingTest.setOnClickListener(TestListener);

        settingtest = (ImageView)findViewById(R.id.settingtest);

        camerasingle = (Button)findViewById(R.id.camerasingle);
        camerasingle.setOnClickListener(captrureListener);

        cameraBatch = (Button)findViewById(R.id.cameraBatch);
        cameraBatch.setOnClickListener(batchCameraListener);


    }

    View.OnClickListener setting_befListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setting_bef.setVisibility(View.GONE);
            setting_aft.setVisibility(View.VISIBLE);
            extra_settings.setVisibility(View.VISIBLE);
            settingbefore.setVisibility(View.GONE);
            settingafter.setVisibility(View.VISIBLE);

        }
    };

    View.OnClickListener setting_aftListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setting_bef.setVisibility(View.VISIBLE);
            setting_aft.setVisibility(View.GONE);
            extra_settings.setVisibility(View.GONE);
            settingbefore.setVisibility(View.VISIBLE);
            settingafter.setVisibility(View.GONE);

        }
    };

    View.OnClickListener batch_befListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            batch_bef.setVisibility(View.GONE);
            batch_aft.setVisibility(View.VISIBLE);
            camera_single.setVisibility(View.GONE);
            camera_batch.setVisibility(View.VISIBLE);
            picturecount.setVisibility(View.VISIBLE);
            textView.setText("Batch Mode");

            settingTest.setVisibility(View.GONE);
            settingtest.setVisibility(View.GONE);

        }
    };

    View.OnClickListener batch_aftListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            batch_bef.setVisibility(View.VISIBLE);
            batch_aft.setVisibility(View.GONE);
            camera_single.setVisibility(View.VISIBLE);
            camera_batch.setVisibility(View.GONE);
            picturecount.setVisibility(View.GONE);
            textView.setText("Single Mode");

            settingTest.setVisibility(View.VISIBLE);
            settingtest.setVisibility(View.VISIBLE);

        }
    };

    View.OnClickListener single_befListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            if( textView.equals("Batch Mode")){

                Log.i("BatchMode","Mode");

            }
            else
            {
                batch_bef.setVisibility(View.VISIBLE);
                batch_aft.setVisibility(View.GONE);
                camera_single.setVisibility(View.VISIBLE);
                camera_batch.setVisibility(View.GONE);
                picturecount.setVisibility(View.GONE);
                textView.setText("Single Mode");
            }

        }
    };

    View.OnClickListener showPictureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.i("test count", "picture count");
            //this is for taken images shown
            Intent in = new Intent(CameraScreen.this,GridViewActivity.class);
            startActivity(in);

        }
    };


    View.OnClickListener single_afrListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(CameraScreen.this,"Single Mode",Toast.LENGTH_SHORT).show();
        }
    };

    View.OnClickListener camera_batchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(CameraScreen.this,"Batch Mode",Toast.LENGTH_SHORT).show();

        }
    };


    View.OnClickListener flashOnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            turnOnFlash();
        }
    };

    View.OnClickListener flashOffListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            turnOffFlash();

        }
    };

    View.OnClickListener TestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("test","test");

        }
    };

    View.OnClickListener batchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            int camerasNumber = Camera.getNumberOfCameras();
            if (camerasNumber > 1) {
                // release the old camera instance
                // switch camera, from the front and the back and vice versa
                count++;
                mCamera.startPreview();
                mCamera.takePicture(null,null,photoCallback_batch);
                String s = String.valueOf(count);
                pictureCountTV.setText(s);
                Log.i("test count", String.valueOf(count));

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("Count", String.valueOf(count));
                editor.clear();
                editor.commit();

            } else {
                Toast toast = Toast.makeText(myContext,
                        "Sorry, your phone has only one camera!",
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the frontFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }






    @Override
    protected void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        releaseCamera();
    }

    private boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }


    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                // make a new picture file
                File pictureFile = getOutputMediaFile();

                if (pictureFile == null) {
                    return;
                }
                try {
                    // write the file
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(pictureFile), options);


                    if (bitmap != null) {

                        Uri uri = Utils.getUri(CameraScreen.this, bitmap);
                        bitmap.recycle();

                        Log.i("test uri",uri.toString());
                        Intent in = new Intent(CameraScreen.this,PolygonViewScreen.class);//this polygonviewscreen class calculating edge of polygon surface automatically.....
                        in.putExtra("imageTest", uri);
                        startActivity(in);
                        finish();

                    } else {
                        Toast.makeText(CameraScreen.this,
                                "Failed to Capture the picture. kindly Try Again:",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }

                // refresh camera to continue preview
                mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }

    //multiple photos
    Camera.PictureCallback photoCallback_batch = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera1) {
            FileOutputStream outStream = null;
            try
            {

                // External sdcard location
                File mediaStorageDir = new File("/sdcard/", "Batch");

                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("image upload", "Oops! Failed create "
                                + "YOUR DIRECTORY NAME" + " directory");

                    }
                }
                outPutfileUri = Uri.fromFile(mediaStorageDir);
                //TODO change naming
                // Create a media file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());

                int i = (int) (new Date().getTime()/1000);
                System.out.println("Integer : " + i);
                String name = String.valueOf(i);
                System.out.println("Time : " + name);



                File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "IMG_" + name + ".jpg");
                System.out.println("mediaFile : " + mediaFile);

                imageFilePath = String.format(mediaStorageDir +"/"+ name);
                Log.i("current imageFilePath",imageFilePath);



                outStream = new FileOutputStream(mediaFile);
                outStream.write(data);
                outStream.close();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(mediaFile), options);



            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }

            mCamera.startPreview();
        }
    };


    private void turnOnFlash() {

        Camera.Parameters p = mCamera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(p);
        mCamera.startPreview();
    }

    private void turnOffFlash() {

        Camera.Parameters p = mCamera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(p);
        mCamera.startPreview();

    }


    View.OnClickListener captrureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCamera.takePicture(null, null, mPicture);
        }
    };

    // make picture and save to a folder
    private static File getOutputMediaFile() {
        // make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File("/sdcard/", "Single");

        // if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            // if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }


       /* Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        formattedDate = df.format(c.getTime());*/

        int i = (int) (new Date().getTime()/1000);
        System.out.println("Integer : " + i);
        String name = String.valueOf(i);
        System.out.println("Time : " + name);



        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + name + ".jpg");
        System.out.println("mediaFile : " + mediaFile);



        return mediaFile;
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
