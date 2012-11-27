package com.twitter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.fruttare.utils.Commons;

public class FrameMergingActivity extends Activity {

	// LinearLayout innerLinearLayout;
	LinearLayout outerLinearLayout;
	FrameLayout parentFrameLayout;
	public static String FLIE_NAME_TO_TWITTER;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		outerLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_Myimage_class);

		parentFrameLayout = (FrameLayout) findViewById(R.id.parentFrameLayout);
		// outerLinearLayout = (LinearLayout)
		// findViewById(R.id.linearLayout_Myimage);
		parentFrameLayout.setDrawingCacheEnabled(true);
		parentFrameLayout
				.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		outerLinearLayout.addView(new MyImage(FrameMergingActivity.this));

	}

	public void cancelButton(View view) {
		startActivity(new Intent(FrameMergingActivity.this, CameraScreen.class));
		finish();
	}

	public void okButton(View view) {

		outerLinearLayout.invalidate();
		parentFrameLayout.invalidate();
		try {
			String name;
			Bitmap bitmap = Bitmap.createBitmap(parentFrameLayout
					.getDrawingCache());
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

			// you can create a new file name "test.jpg" in sdcard
			// folder.
			File sdCard = Environment.getExternalStorageDirectory();

			File dir = new File(sdCard.getAbsolutePath() + "/Frutarre");
			name = Commons.getTodaysDate() + ".jpg"; // "test.jpg"
			dir.mkdirs();

			File f = new File(dir, name);
			f.createNewFile();

			FLIE_NAME_TO_TWITTER = sdCard.getAbsolutePath() + "/Frutarre"
					+ File.separator + name;
			// write the bytes in file
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("GCM", e.toString());
		}
		startActivity(new Intent(FrameMergingActivity.this,
				AndroidCustomListViewActivity.class));
		finish();
	}

	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		startActivity(new Intent(FrameMergingActivity.this, CameraScreen.class));
		finish();
	}

}
