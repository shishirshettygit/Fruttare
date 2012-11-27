package com.twitter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import twitter4j.Status;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.fruttare.utils.Commons;
import com.fruttare.utils.CustomToast;
import com.twitter.android.api.TwitterApp;
import com.twitter.android.api.TwitterApp.TwDialogListener;

public class AndroidCustomListViewActivity extends Activity {
	private ListView myList;
	private MyAdapter myAdapter;

	public ArrayList<ListItem> myItems = new ArrayList<ListItem>();
	public static String upload = "";
	TwitterApp mTwitter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);

		myList = (ListView) findViewById(R.id.MyList);
		myList.setCacheColorHint(Color.TRANSPARENT);
		myList.setItemsCanFocus(true);
		myAdapter = new MyAdapter();

		ListItem listItem = new ListItem();
		listItem.caption = "";
		myItems.add(listItem);

		myList.setAdapter(myAdapter);
		myAdapter.notifyDataSetChanged();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		startActivity(new Intent(AndroidCustomListViewActivity.this,
				CameraScreen.class));
		finish();
	}

	public void backButtonpressed(View view) {
		startActivity(new Intent(AndroidCustomListViewActivity.this,
				CameraScreen.class));
		finish();
	}

	public void postOnTwitter(View view) {

		upload = "";
		for (int i = 0; i < myItems.size(); i++) {
			if (myItems.get(i).caption.trim().length() > 2)
				upload += "@" + myItems.get(i).caption.trim() + ",";
		}
		if (upload.length() > 65) {

			/*
			 * Toast.makeText(getApplicationContext(),
			 * "Please remove some users", Toast.LENGTH_SHORT).show();
			 */
			CustomToast
					.showToast(Commons.CHARACTER_EXCEED_ERROR,
							AndroidCustomListViewActivity.this,
							getApplicationContext());
		} else {
			// startActivity(new Intent(AndroidCustomListViewActivity.this,
			// PostOnTwitter.class));

			saveTwitterHandlesToTextFile(upload);

			mTwitter = new TwitterApp(AndroidCustomListViewActivity.this,
					AndroidCustomListViewActivity.this, Commons.OAUTH_CONSUMER,
					Commons.OAUTH_SECRET);
			mTwitter.setListener(mTwLoginDialogListener);

			new Thread() {
				public void run() {
					try {
						final Status twitterStatus = mTwitter
								.updaStatusWithFileMedia(
										upload
												+ Commons.TWITTER_STRING,
										new File(
												FrameMergingActivity.FLIE_NAME_TO_TWITTER));
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (null != twitterStatus)
									CustomToast
											.showToast(
													Commons.SUCCESS_POST,
													AndroidCustomListViewActivity.this,
													getApplicationContext());
								else {
									CustomToast.showToast(
											Commons.ERROR_MESSAGE,
											AndroidCustomListViewActivity.this,
											getApplicationContext());
								}
							}
						});

					} catch (Exception e) {
						// TODO: handle exception
						Log.d("DROID", e.toString() + " EXCEPTION");
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								CustomToast.showToast(Commons.ERROR_MESSAGE,
										AndroidCustomListViewActivity.this,
										getApplicationContext());
							}
						});

					}
				};
			}.start();

			startActivity(new Intent(AndroidCustomListViewActivity.this,
					CameraScreen.class));
			finish();
		}

		// startActivity(new Intent(AndroidCustomListViewActivity.this,
		// PostOnTwitter.class));

	}

	private void saveTwitterHandlesToTextFile(String upload2) {
		// TODO Auto-generated method stub
		try {
			File myFile = new File("/sdcard/Frutarre/twitterhandle.txt");

			BufferedWriter myOutWriter = new BufferedWriter(new FileWriter(
					myFile, true));
			/** Saving the contents to the file */
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			Date date = new Date();
			myOutWriter.write("\n" + upload2 + sdf.format(date) + "\n");
			myOutWriter.flush();
			myOutWriter.close();

			// Toast.makeText(getBaseContext(), "Successfully saved",
			// Toast.LENGTH_SHORT).show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		public void onComplete(String value) {
			Log.d("DROID", "TwDialogListener onComplete");
		}

		public void onError(String value) {
			/*
			 * Toast.makeText(AndroidCustomListViewActivity.this,
			 * "Connection with Twitter failed", Toast.LENGTH_LONG).show();
			 */
			CustomToast
					.showToast("Connection with Twitter failed",
							AndroidCustomListViewActivity.this,
							getApplicationContext());

			// finish();
		}
	};

	// public void addOrDeleteItems(View view) {
	//
	// int tag = (Integer) view.getTag();
	// if (tag != (myItems.size() - 1)) {
	// myItems.remove(tag);
	// Log.d("GCM", "Item removed from " + tag);
	// myAdapter.notifyDataSetChanged();
	// } else {
	// ListItem listItem = new ListItem();
	// listItem.caption = "";
	// myItems.add(listItem);
	// myAdapter.notifyDataSetChanged();
	// myList.setSelection(myAdapter.getCount() - 1);
	// }
	// }

	public class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// one item added to list by default
			// ListItem listItem = new ListItem();
			// listItem.caption = "";
			// myItems.add(listItem);
			// notifyDataSetChanged();
		}

		public int getCount() {
			return myItems.size();
		}

		public ListItem getItem(int position) {
			return myItems.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item, null);
				holder.captionEditText = (EditText) convertView
						.findViewById(R.id.ItemCaption);

				holder.addOrDeleteButton = (Button) convertView
						.findViewById(R.id.buttonAdd);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Fill EditText with the value you have in data source
			// holder.captionEditText.setId(position);
			holder.captionEditText.setTag(position);
			holder.captionEditText.setText(getItem(position).caption);

			holder.addOrDeleteButton.setTag(position);

			// / this updates tag of
			// the button view as we
			// scroll ///

			holder.addOrDeleteButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					int tag = (Integer) view.getTag();
					if (tag != (myItems.size() - 1)) {
						myItems.remove(tag);
						Log.d("GCM", "Item removed from " + tag);
						myAdapter.notifyDataSetChanged();
					} else {
						if (null != myItems && myItems.size() < 10) {
							ListItem listItem = new ListItem();
							listItem.caption = "";
							myItems.add(listItem);

							myAdapter.notifyDataSetChanged();
							myList.setSelection(myAdapter.getCount() - 1);

							// holder.captionEditText.setFocusable(true);
							// holder.captionEditText.requestFocus();

						} else {
							/*
							 * Toast.makeText(AndroidCustomListViewActivity.this,
							 * "You can only add upto 10 Twitter handles",
							 * Toast.LENGTH_SHORT).show();
							 */
							CustomToast
									.showToast(
											"You can only add up to 10 Twitter handles",
											AndroidCustomListViewActivity.this,
											getApplicationContext());
						}
					}

				}
			});

			holder.captionEditText.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub

				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub

				}

				public void afterTextChanged(Editable s) {
					// if(position < myItems.size())
					// getItem(position).caption = s.toString();

					myItems.get((Integer) holder.captionEditText.getTag()).caption = holder.captionEditText
							.getText().toString();

				}
			});

			if (position != (myItems.size() - 1)) {
				holder.addOrDeleteButton
						.setBackgroundResource(R.drawable.closebuttonselector);
			} else {
				holder.addOrDeleteButton
						.setBackgroundResource(R.drawable.addbuttonselector);
				holder.captionEditText.setFocusable(true);
				holder.captionEditText.requestFocus();

			}

			// we need to update adapter once we finish with editing
			// holder.captionEditText
			// .setOnFocusChangeListener(new OnFocusChangeListener() {
			// public void onFocusChange(View v, boolean hasFocus) {
			//
			// if (!hasFocus) {
			// final int position = (Integer)v.getTag();
			// final EditText Caption = (EditText) v;
			// myItems.get(position).caption = Caption
			// .getText().toString();
			// Log.d("GCM", position+" EditText");
			// //notifyDataSetChanged();
			// }
			// }
			// });

			return convertView;
		}
	}

	class ViewHolder {
		EditText captionEditText;
		Button addOrDeleteButton;
	}

	class ListItem {
		String caption;
	}
}
