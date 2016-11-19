package com.martin.opencv4android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;


import com.martin.opencv4android.Adapter.DocsAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class HomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private RecyclerView rvDocs;
    private Uri fileUri,selectedImage;
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
            DocItem postemail = new DocItem("Dummy Doc", "12/09/16", null);
            iPostParams.add(postemail);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
            String formattedDate = df.format(c.getTime());
            System.out.println("Current time => " + formattedDate);

            File[] list = root.listFiles();
            if(list != null) {
                for (File f : list) {
                    if (f.isDirectory() && !(f.getName().equals("thumbnails"))) {
                        Log.d("", "Dir: " + f.getAbsoluteFile());
                        postemail= null;
                        Bitmap b = null;
                        File file= new File(android.os.Environment.getExternalStorageDirectory(),"/DocumentScanner/thumbnails/" + f.getName() + ".jpg");
                        try {
                            b = BitmapFactory.decodeStream(new FileInputStream(file));
                            System.out.println("File name" + file);
                            System.out.println("bitmap"+b);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        postemail = new DocItem(f.getName().toString(), formattedDate, b);
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


        Intent in =new Intent(HomeActivity.this,CameraScreen.class);
        startActivity(in);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("", "onActivityResult" + resultCode);
        Bitmap bitmap = null;
        if (resultCode == Activity.RESULT_OK) {
            try {
                switch (requestCode) {

                    case ScanConstants.PICKFILE_REQUEST_CODE:
                        selectedImage = data.getData();
                        Intent i = new Intent(HomeActivity.this, PolygonViewScreen.class);
                        i.putExtra("imageTest1", selectedImage);//here we have to pass uri for crystal clear photo ,if this is gallery so pass data.getData()
                        Log.i("test null",selectedImage.toString());
                        startActivity(i);
                        break;
                }
            } catch (Exception e
                    ) {
                e.printStackTrace();
            }
        }

    }


    public void initNavigationDrawer() {

        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id){
                    case R.id.home:
                        openCamera();//this will open the camera (Activity screen)
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.settings:
                        openMediaContent();//this will open the gallery
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.docs:
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.logout://exit the app
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