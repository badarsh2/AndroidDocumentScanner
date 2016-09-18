package com.martin.opencv4android;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

public class DetailActivity extends AppCompatActivity {
    private ImageView imageView;
    private static String FILE = "mnt/sdcard/FirstPdf.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView)findViewById(R.id.imageView1);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertit(imageView);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void convertit(ImageView imageView) {
        try
        {
            Document document = new Document();
            String dirpath=android.os.Environment.getExternalStorageDirectory().toString();
//            File wallpaperDirectory = new File(dirpath+"/DocumentScanner/New Doc 1");
//            // have the object build the directory structure, if needed.
//            wallpaperDirectory.mkdirs();
            PdfWriter.getInstance(document,new FileOutputStream(dirpath+"/DocumentScanner/New Doc 1/example.pdf"));
            document.open();

            BitmapDrawable bmp = (BitmapDrawable)imageView.getDrawable();
            Bitmap bitmap = bmp.getBitmap();
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
            document.close();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
