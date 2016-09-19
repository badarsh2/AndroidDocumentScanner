package com.martin.opencv4android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
                convertit(f, s);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        rvPics = (RecyclerView) findViewById(R.id.rvPics);
        getFromSdcard(s);
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
                        DetailItem postemail = new DetailItem(b);
                        iPostParams.add(postemail);
                        f.add(b);
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
