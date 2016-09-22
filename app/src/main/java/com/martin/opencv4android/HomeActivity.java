package com.martin.opencv4android;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class HomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private RecyclerView rvDocs;
    private Uri fileUri;
    ArrayList<DocItem> iPostParams;
    private DocsAdapter adapter;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initNavigationDrawer();
        getSupportActionBar().setHomeButtonEnabled(true);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new CameraButtonClickListener());

        rvDocs = (RecyclerView) findViewById(R.id.rvDocs);
        iPostParams = new ArrayList<DocItem>();

        Filewalker fw = new Filewalker();
        String dirpath=android.os.Environment.getExternalStorageDirectory().toString();
        File reader = new File(dirpath, "DocumentScanner");
        fw.walk(reader);

        adapter = new DocsAdapter(getApplicationContext(), iPostParams);
        rvDocs.setAdapter(adapter);
        rvDocs.setLayoutManager(new GridLayoutManager(this, 2));
    }

    public class Filewalker {

        public void walk(File root) {
            iPostParams = new ArrayList<>();
            DocItem postemail;
            // iPostParams.add(postemail);

            File[] list = root.listFiles();
            if(list != null) {
                for (File f : list) {
                    if (f.isDirectory() && !(f.getName().equals("thumbnails"))) {
                        Log.d("", "Dir: " + f.getAbsoluteFile());
                        postemail = null;
                        Bitmap b = null;
                        File file= new File(android.os.Environment.getExternalStorageDirectory(),"/DocumentScanner/thumbnails/" + f.getName() + ".jpg");
                        try {
                            b = BitmapFactory.decodeStream(new FileInputStream(file));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        postemail = new DocItem(f.getName().toString(), "19/09/16", b);
                        iPostParams.add(postemail);
                        // walk(f);
                    } else {
                        Log.d("", "File: " + f.getAbsoluteFile());
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(0);
        Filewalker fw = new Filewalker();
        String dirpath=android.os.Environment.getExternalStorageDirectory().toString();
        File reader = new File(dirpath, "DocumentScanner");
        fw.walk(reader);


        adapter = new DocsAdapter(getApplicationContext(), iPostParams);
        rvDocs.setAdapter(adapter);
        rvDocs.setLayoutManager(new GridLayoutManager(this, 2));
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, ScanConstants.PICKFILE_REQUEST_CODE);
    }

    public void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File file = createImageFile();
        file.getParentFile().mkdirs();
        fileUri = Uri.fromFile(file);
        if (file != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(cameraIntent, ScanConstants.START_CAMERA_REQUEST_CODE);
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
                Date());
        File file = new File(ScanConstants.IMAGE_PATH, "IMG_" + timeStamp +
                ".jpg");
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("", "onActivityResult" + resultCode);
        Bitmap bitmap = null;
        if (resultCode == Activity.RESULT_OK) {
            try {
                switch (requestCode) {
                    case ScanConstants.START_CAMERA_REQUEST_CODE:
                        bitmap = getBitmap(fileUri);
                        break;

                    case ScanConstants.PICKFILE_REQUEST_CODE:
                        bitmap = getBitmap(data.getData());
                        break;
                }
            } catch (Exception e
                    ) {
                e.printStackTrace();
            }
        }
        if (bitmap != null) {
            postImagePick(bitmap);
        }
    }

    protected void postImagePick(Bitmap bitmap) {
        Uri uri = Utils.getUri(HomeActivity.this, bitmap);
        bitmap.recycle();
        // scanner.onBitmapSelect(uri);
        Intent i = new Intent(HomeActivity.this, MainActivity.class);
        i.putExtra("imageUri", uri);
        startActivity(i);
    }

    private Bitmap getBitmap(Uri selectedimg) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        AssetFileDescriptor fileDescriptor = null;
        fileDescriptor = getContentResolver().openAssetFileDescriptor(selectedimg, "r");
        Bitmap original
                = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);
        return original;
    }

//    private class GalleryClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(View view) {
//            Intent i = new Intent(HomeActivity.this, MainActivity.class);
//            i.putExtra("PASSING", 2);
//            startActivity(i);
//        }
//    }

    public void initNavigationDrawer() {

        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id){
                    case R.id.home:
                        openCamera();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.settings:
                        openMediaContent();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.trash:
                        // Toast.makeText(getApplicationContext(),"Trash",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logout:
                        finish();

                }
                return true;
            }
        });
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }
}
