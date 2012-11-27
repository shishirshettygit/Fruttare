package com.twitter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.fruttare.utils.MyProgressDialog;

public class SplashScreen extends Activity {

	//protected int _splashTime = 5000;

	// ProgressBar progressBar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.splash);

		MyProgressDialog.show(SplashScreen.this, "", "");

		Thread splashThread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (waited < 2000) {
						sleep(100);
						waited += 100;
					}
				} catch (InterruptedException e) {

					e.printStackTrace();
				} finally {
					try {
						// progressDialog.dismiss();
					} catch (Exception e2) {
						// TODO: handle exception
					}

					startActivity(new Intent(SplashScreen.this,
							TwitterCheckActivity.class));
					
					finish();
				}
			}
		};
		splashThread.start();
		MyProgressDialog.dismiss(getApplicationContext());
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

}
