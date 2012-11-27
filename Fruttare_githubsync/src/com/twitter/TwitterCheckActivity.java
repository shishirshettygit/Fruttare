package com.twitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fruttare.utils.Commons;
import com.fruttare.utils.CustomToast;
import com.twitter.android.api.TwitterApp;
import com.twitter.android.api.TwitterApp.TwDialogListener;

public class TwitterCheckActivity extends Activity {
	TwitterApp mTwitter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		mTwitter = new TwitterApp(TwitterCheckActivity.this,
				TwitterCheckActivity.this, Commons.OAUTH_CONSUMER,
				Commons.OAUTH_SECRET);
		mTwitter.setListener(mTwLoginDialogListener);

		loginToTwitter();

	}

	private void goToScreen2() {
		// TODO Auto-generated method stub
		startActivity(new Intent(TwitterCheckActivity.this,
				CameraAndFolderActivity.class));
		finish(); 
	}

	private void loginToTwitter() {
		// TODO Auto-generated method stub
		if (!mTwitter.hasAccessToken()) {
			mTwitter.authorize();
			//
		} else {
			goToScreen2();
		}
	}

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		public void onComplete(String value) {
			Log.d("DROID", "TwDialogListener onComplete");
			goToScreen2();
		}

		public void onError(String value) {
		/*	Toast.makeText(TwitterCheckActivity.this,
					"Connection with Twitter failed, Check Internet connection", Toast.LENGTH_LONG).show();*/
			CustomToast.showToast("Connection with Twitter failed, Check Internet connection", TwitterCheckActivity.this, getApplicationContext());
	
			// finish();
		}
	};
}
