package com.martin.opencv4android;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    private ImageView imageView;
    private static String FILE = "mnt/sdcard/FirstPdf.pdf";
    private RecyclerView rvPics;
    ArrayList<Bitmap> f;
    File[] listFile;
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        s = getIntent().getStringExtra("folder");
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        f = new ArrayList<>();

        // imageView = (ImageView)findViewById(R.id.imageView1);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // convertit(f, s);
                new PDFAsyncTask().execute();
            }
        });
        rvPics = (RecyclerView) findViewById(R.id.rvPics);
        getFromSdcard(s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private class PDFAsyncTask extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(DetailActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("Saving as PDF...");
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            convertit(f, s);
            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/DocumentScanner/" + s + "/example.pdf");
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(file),"application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent = Intent.createChooser(target, "Open File");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread

            pdLoading.dismiss();
        }

    }


    public void getFromSdcard(String s)
    {
        File file= new File(android.os.Environment.getExternalStorageDirectory(),"/DocumentScanner/" + s);
        ArrayList<DetailItem> iPostParams = new ArrayList<DetailItem>();
        Log.d("DetailActivity", "Checkpt 1");

        if (file.isDirectory())
        {
            Log.d("DetailActivity", "Checkpt 2");
            listFile = file.listFiles();


            for (int i = 0; i < listFile.length; i++)
            {
                try {
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(listFile[i]));
                    if(b!= null) {
                        DetailItem postemail = new DetailItem(b, listFile[i].getAbsolutePath());
                        iPostParams.add(postemail);
                        f.add(b);
                        // Log.d("Path", listFile[i].getAbsolutePath());
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }


            DetailAdapter adapter = new DetailAdapter(getApplicationContext(),iPostParams);
            rvPics.setAdapter(adapter);
            rvPics.setLayoutManager(new GridLayoutManager(this, 2));
        }
    }

    private void convertit(ArrayList<Bitmap> bitmaps, String s) {
        try
        {
            Document document = new Document();
            String dirpath=android.os.Environment.getExternalStorageDirectory().toString();
//            File wallpaperDirectory = new File(dirpath+"/DocumentScanner/New Doc 1");
//            // have the object build the directory structure, if needed.
//            wallpaperDirectory.mkdirs();
            PdfWriter.getInstance(document,new FileOutputStream(dirpath+"/DocumentScanner/" + s + "/example.pdf"));
            document.open();
            Bitmap bitmap;
            // BitmapDrawable bmp = (BitmapDrawable)imageView.getDrawable();
            for(int i = 0; i < bitmaps.size(); i++) {
                bitmap = bitmaps.get(i);
                if(bitmap != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    Image img = Image.getInstance(stream.toByteArray());
                    // addImage(document);
                    float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                            - document.rightMargin() - 0) / img.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                    img.scalePercent(scaler);
                    img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                    //img.setAlignment(Image.LEFT| Image.TEXTWRAP);

                    document.add(img);
                }
            }
            document.close();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
