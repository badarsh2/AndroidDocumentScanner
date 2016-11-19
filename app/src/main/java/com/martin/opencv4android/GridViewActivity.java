package com.martin.opencv4android;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


import com.martin.opencv4android.Adapter.GridViewImageAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class GridViewActivity extends AppCompatActivity {

	private Utils utils;
	private ArrayList<String> imagePaths = new ArrayList<String>();
	private GridViewImageAdapter adapter;
	private GridView gridView;
	private int columnWidth;
	private ArrayList<String> _filePaths = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid_view);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setTitle("Images");
		setTitleColor(Color.WHITE);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
				//Toast.makeText(DetailActivity.this,"hello",Toast.LENGTH_SHORT).show();
			}
		});


		gridView = (GridView) findViewById(R.id.grid_view);

		utils = new Utils(this);

		// Initilizing Grid View
		InitilizeGridLayout();

		// loading all image paths from SD card
		imagePaths = utils.getFilePaths();

		// Gridview adapter
		adapter = new GridViewImageAdapter(GridViewActivity.this, imagePaths,
				columnWidth);

		// setting grid view adapter
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {


				File externalFile2 = new File(imagePaths.get(position));
				Log.i("search1", externalFile2.toString());

            //THIS IS getting File:///sdcard/documentScanner/batch/img_8945.jpg ---dont use this,we have to send content:;//media/external/images/media/8989
			//	Uri external = Uri.fromFile(externalFile2);
			//	Log.i("search2", external.toString());


				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(externalFile2), options);

				try {

					if (bitmap != null) {

						Uri uri = Utils.getUri(GridViewActivity.this, bitmap);
						bitmap.recycle();

						Log.i("GRid test uri", uri.toString());
						Intent in = new Intent(GridViewActivity.this, PolygonViewScreen.class);//this polygonviewscreen class calculating edge of polygon surface automatically.....
						in.putExtra("image2", uri);
						startActivity(in);
						finish();

					} else {
						Toast.makeText(GridViewActivity.this,
								"Failed to Capture the picture. kindly Try Again:",
								Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
				}
			}
		});
	}

	private void InitilizeGridLayout() {
		Resources r = getResources();
		float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				ScanConstants.GRID_PADDING, r.getDisplayMetrics());

		columnWidth = (int) ((utils.getScreenWidth() - ((ScanConstants.NUM_OF_COLUMNS + 1) * padding)) / ScanConstants.NUM_OF_COLUMNS);

		gridView.setNumColumns(ScanConstants.NUM_OF_COLUMNS);
		gridView.setColumnWidth(columnWidth);
		gridView.setStretchMode(GridView.NO_STRETCH);
		gridView.setPadding((int) padding, (int) padding, (int) padding,
				(int) padding);
		gridView.setHorizontalSpacing((int) padding);
		gridView.setVerticalSpacing((int) padding);
	}

}
