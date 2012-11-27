package com.twitter.android.api;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.IDs;
import twitter4j.Place;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

@SuppressWarnings({ "unused", "deprecation" })
public class TwitterApp {
	/**
	 * 
	 */

	private Twitter mTwitter;
	private TwitterSession mSession;
	private AccessToken mAccessToken;
	private CommonsHttpOAuthConsumer mHttpOauthConsumer;
	private OAuthProvider mHttpOauthprovider;
	private String mConsumerKey;
	private String mSecretKey;
	private ProgressDialog mProgressDlg;
	private TwDialogListener mListener;
	private Context context, activity;

	public static final String CALLBACK_URL = "twitterapp://connect";
	private static final String TAG = "TwitterApp";

	public TwitterApp(Context context, Context activity, String consumerKey,
			String secretKey) {
		this.context = context;
		this.activity = activity;
		mTwitter = new TwitterFactory().getInstance();
		mSession = new TwitterSession(context);
		mProgressDlg = new ProgressDialog(activity);
		//
		mProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mConsumerKey = consumerKey;
		mSecretKey = secretKey;

		mHttpOauthConsumer = new CommonsHttpOAuthConsumer(mConsumerKey,
				mSecretKey);
		mHttpOauthprovider = new DefaultOAuthProvider(
				"https://api.twitter.com/oauth/request_token",
				"https://api.twitter.com/oauth/access_token",
				"https://api.twitter.com/oauth/authorize");

		mAccessToken = mSession.getAccessToken();

		configureToken();

	}

	public void setListener(TwDialogListener listener) {
		mListener = listener;
	}

	private void configureToken() {
		if (mAccessToken != null) {
			mTwitter.setOAuthConsumer(mConsumerKey, mSecretKey);

			mTwitter.setOAuthAccessToken(mAccessToken);
		}
	}

	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}

	public void resetAccessToken() {
		if (mAccessToken != null) {
			mSession.resetAccessToken();

			mAccessToken = null;
		}
	}

	public String getUsername() {
		return mSession.getUsername();
	}

	public String getUserCreataion() {
		return mSession.getUserCreation();
	}

	public String getUserDesc() {
		return mSession.getUserDesc();

	}

	public int getUserFriendsCount() {
		return mSession.getUserFriendCount();

	}

	public Status updateStatus(String status) throws Exception {
		Status s;
		try {
			s = mTwitter.updateStatus(status);

		} catch (TwitterException e) {
			throw e;
		}
		return s;
	}

	public Status updaStatusWithFileMedia(String status, File file,
			GeoLocation location) {
		Status s = null;
		// HttpResponse res=null;
		try {
			s = mTwitter.updateStatusWithFileMedia(status, file, location);

		} catch (TwitterException e) {

			e.printStackTrace();
		}
		return s;
	}

	public Status updaStatusWithFileMedia(String status, File file) {
		Status s = null;
		try {
			s = mTwitter.updateStatusWithFileMedia(status, file);

		} catch (TwitterException e) {

			e.printStackTrace();
			try {
				File myFile = new File("/sdcard/Frutarre/twitterexception.txt");

				BufferedWriter myOutWriter = new BufferedWriter(new FileWriter(
						myFile, true));
				/** Saving the contents to the file */
				SimpleDateFormat sdf = new SimpleDateFormat(
						"dd-MM-yyyy HH:mm:ss");
				Date date = new Date();
				myOutWriter
						.write("\n" + e.toString() + sdf.format(date) + "\n");
				myOutWriter.flush();
				myOutWriter.close();

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return s;
	}

	public void authorize() {
		mProgressDlg.setMessage("Initializing. Please wait...");
		mProgressDlg.show();

		new Thread() {
			@Override
			public void run() {
				String authUrl = "";
				int what = 1;

				try {
					authUrl = mHttpOauthprovider.retrieveRequestToken(
							mHttpOauthConsumer, CALLBACK_URL);

					what = 0;
					Log.d(TAG, "what" + String.valueOf(what));
					Log.d(TAG, "Request token url " + authUrl);

				} catch (Exception e) {
					Log.d(TAG, "Failed to get request token");

					e.printStackTrace();
				}
				mHandler.sendMessage(mHandler
						.obtainMessage(what, 1, 0, authUrl));

			}
		}.start();
	}

	public User getUser() {

		User user;
		try {
			user = mTwitter.verifyCredentials();
			return user;
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public void processToken(String callbackUrl) {
		mProgressDlg.setMessage("Finalizing. Please wait...");
		mProgressDlg.show();

		final String verifier = getVerifier(callbackUrl);

		new Thread() {
			@Override
			public void run() {
				int what = 1;

				try {
					mHttpOauthprovider.retrieveAccessToken(mHttpOauthConsumer,
							verifier);

					mAccessToken = new AccessToken(
							mHttpOauthConsumer.getToken(),
							mHttpOauthConsumer.getTokenSecret());

					configureToken();

					User user = mTwitter.verifyCredentials();

					mSession.storeAccessToken(mAccessToken, user.getName(),
							user.getDescription(), user.getCreatedAt(),
							user.getFriendsCount());

					what = 0;
				} catch (Exception e) {
					Log.d(TAG, "Error getting access token");

					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
	}

	private String getVerifier(String callbackUrl) {
		String verifier = "";

		try {
			callbackUrl = callbackUrl.replace("twitterapp", "http");

			URL url = new URL(callbackUrl);
			String query = url.getQuery();

			String array[] = query.split("&");

			for (String parameter : array) {
				String v[] = parameter.split("=");

				if (URLDecoder.decode(v[0]).equals(
						oauth.signpost.OAuth.OAUTH_VERIFIER)) {
					verifier = URLDecoder.decode(v[1]);
					break;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return verifier;
	}

	public void showLoginDialog(String url) {
		final TwDialogListener listener = new TwDialogListener() {
			public void onComplete(String value) {
				processToken(value);
			}

			public void onError(String value) {
				mListener.onError("Failed opening authorization page");
			}
		};

		new TwitterDialog(activity, url, listener).show();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, msg.toString());
			mProgressDlg.dismiss();

			if (msg.what == 1) {
				if (msg.arg1 == 1)
					mListener.onError("Error getting request token");
				else
					mListener.onError("Error getting access token");
			} else if (msg.what == 0) {
				if (msg.arg1 == 1) {
					Log.d(TAG, String.valueOf(msg.arg1));
					showLoginDialog((String) msg.obj);
				} else
					try {
						mListener.onComplete("");
					} catch (NullPointerException e) {

						e.printStackTrace();
					}
			}

		}
	};

	public Status showStatus(long status_id) {

		Status s = null;
		try {
			s = mTwitter.showStatus(status_id);
			return s;
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public ArrayList<User> getFriends() {

		ArrayList<User> users = new ArrayList<User>();
		long cursor = -1;
		int i = 0;
		IDs ids = null;
		System.out.println("Listing followers's ids.");
		do {
			try {
				ids = mTwitter.getFollowersIDs(cursor);

				for (long id : ids.getIDs()) {
					// System.out.println(id);
					users.add(mTwitter.showUser(id));

					// System.out.println(user.getName());
				}
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while ((cursor = ids.getNextCursor()) != 0);
		Log.v("users", users.toString());
		return users;
	}

	public interface TwDialogListener {
		public void onComplete(String value);

		public void onError(String value);
	}

	public ResponseList<Place> reverseGeoTwitter(GeoLocation location) {
		try {
			GeoQuery query = new GeoQuery(location);
			query.setMaxResults(10);
			query.setGranularity("poi");
			return mTwitter.reverseGeoCode(query);

		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public Status updateStatusWithFileMedia(String status, File file,
			String place_id) {

		try {
			return mTwitter.updateStatusWithFileMedia(status, file, place_id);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

}