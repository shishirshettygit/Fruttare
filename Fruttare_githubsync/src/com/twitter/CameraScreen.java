package com.twitter;

import java.io.File;

import com.fruttare.utils.Commons;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class CameraScreen extends Activity {

	File photo;
	private static final int REQUEST_CODE = 1;
	public static Bitmap myBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		photo = new File(Environment.getExternalStorageDirectory()
				+ "/Frutarre_NoFrames");
		Log.d("GCM", photo.toString());
		boolean success = false;
		if (!photo.exists()) {
			success = photo.mkdirs();
		}
		// if (!success)
		{
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			photo = new File(Environment.getExternalStorageDirectory()
					+ "/Frutarre_NoFrames", Commons.getTodaysDate() + ".jpg");
			Log.d("GCM", photo.toString());
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
			Uri imageUir = Uri.fromFile(photo);
			startActivityForResult(intent, REQUEST_CODE);
			// finish();
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("FRUTA", "onResume");

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.d("FRUTA", requestCode + " " + resultCode + " " + data);
		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			// if(data!=null)
			{
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				myBitmap = BitmapFactory.decodeFile(photo.toString(), options);

				startActivity(new Intent(CameraScreen.this,
						FrameMergingActivity.class));
				finish();
			}
		}

		Log.d("FRUTA", "onActivityResult");
		if (resultCode != Activity.RESULT_OK) {
			startActivity(new Intent(CameraScreen.this,
					CameraAndFolderActivity.class));
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
