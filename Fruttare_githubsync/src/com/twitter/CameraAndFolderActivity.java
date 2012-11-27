package com.twitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fruttare.utils.Commons;
import com.fruttare.utils.CustomToast;
import com.image.from.gallery.ImageFromGalery;
import com.twitter.android.api.TwitterApp;
import com.twitter.android.api.TwitterApp.TwDialogListener;

public class CameraAndFolderActivity extends Activity {

	// private Bitmap bitmap;
	// public static Bitmap myBitmap;
	// Uri imageFileUri;
	// File photo;
	TwitterApp mTwitter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen2);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mTwitter = new TwitterApp(CameraAndFolderActivity.this,
				CameraAndFolderActivity.this, Commons.OAUTH_CONSUMER,
				Commons.OAUTH_SECRET);
		mTwitter.setListener(mTwLoginDialogListener);
	}

	public void takeImage(View view) {

		if (mTwitter.hasAccessToken()) {
			startActivity(new Intent(CameraAndFolderActivity.this,
					CameraScreen.class));
			finish();
		} else {
			/*Toast.makeText(CameraAndFolderActivity.this,
					"Please login to Twitter", Toast.LENGTH_LONG).show();*/
			
			CustomToast.showToast("Please login to Twitter", CameraAndFolderActivity.this, getApplicationContext());
						
			loginLogoutTwitter();
		}

	}


	public void fromGallery(View view) {
		if (mTwitter.hasAccessToken())
			startActivity(new Intent(CameraAndFolderActivity.this,
					ImageFromGalery.class));
		else {
		/*	Toast.makeText(CameraAndFolderActivity.this,
					"Please login to Twitter", Toast.LENGTH_LONG).show();*/
		
		CustomToast.showToast("Please login to Twitter", CameraAndFolderActivity.this, getApplicationContext());
			loginLogoutTwitter();
		}
	}

	public void logoutTwitter(View view) {
		loginLogoutTwitter();
	}

	public void loginLogoutTwitter() {
		mTwitter = new TwitterApp(CameraAndFolderActivity.this,
				CameraAndFolderActivity.this, Commons.OAUTH_CONSUMER,
				Commons.OAUTH_SECRET);
		mTwitter.setListener(mTwLoginDialogListener);

		mTwitter.resetAccessToken();

		mTwitter.authorize();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		finish();
	}

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		public void onComplete(String value) {
			Log.d("DROID", "TwDialogListener onComplete");
			/*Toast.makeText(CameraAndFolderActivity.this,
					"User logged into twitter successfully", Toast.LENGTH_LONG)
					.show();*/
			CustomToast.showToast("User logged into twitter successfully", CameraAndFolderActivity.this, getApplicationContext());
			
		}

		public void onError(String value) {
			/*Toast.makeText(CameraAndFolderActivity.this,
					"Connection with Twitter failed", Toast.LENGTH_LONG).show();*/
			CustomToast.showToast("Connection with Twitter failed", CameraAndFolderActivity.this, getApplicationContext());
			
			// finish();
		}
	};
}
