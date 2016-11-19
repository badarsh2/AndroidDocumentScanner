package com.martin.opencv4android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * Created by k002 on 18/10/16.
 */
public class ResultActivity extends AppCompatActivity {
    ImageView image;
    private Bitmap op,original;
  //  private Mat mat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);
        image = (ImageView)findViewById(R.id.scannedImage);
        Button black_white = (Button)findViewById(R.id.BWMode);

        Intent intent = getIntent();
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");
        Toast.makeText(ResultActivity.this,bitmap.toString(),Toast.LENGTH_SHORT).show();

        /*byte[] byteArray = getIntent().getByteArrayExtra("BitmapImage");
        original = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Toast.makeText(ResultActivity.this,original.toString(),Toast.LENGTH_SHORT).show();
      */  //image.setImageBitmap(original);

        black_white.setOnClickListener(new BWButtonClickListener());
    }
    private class BWButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            color_change();

        }

    }

    private Bitmap JPGtoRGB888(Bitmap img){
        Bitmap result = null;

        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

//        get jpeg pixels, each int is the color value of one pixel
        img.getPixels(pixels,0,img.getWidth(),0,0,img.getWidth(),img.getHeight());

//        create bitmap in appropriate format
        result = Bitmap.createBitmap(img.getWidth(),img.getHeight(), Bitmap.Config.ARGB_8888);

//        Set RGB pixels
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());

        return result;
    }

private void color_change()
{

   /* Bitmap bmp32 = JPGtoRGB888(original);
    Mat sourceImage = new Mat();
    Utils.bitmapToMat(bmp32, sourceImage);
*/
    op = OpenCVHelper.getMagicColorBitmap(original);
    image.setImageBitmap(op);
}


    static {
        System.loadLibrary("OpenCV");
        //   System.loadLibrary("Scanner");
    }


}
