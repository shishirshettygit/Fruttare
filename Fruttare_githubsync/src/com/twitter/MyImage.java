package com.twitter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;

public class MyImage extends View {

	private static final int INVALID_POINTER_ID = -1;

	private Drawable mImage;
	private float mPosX;
	private float mPosY;

	private float mLastTouchX;
	private float mLastTouchY;
	private int mActivePointerId = INVALID_POINTER_ID;

	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.f;

	Button yourButton = new Button(getContext());

	public MyImage(Context context) {
		this(context, null, 0);

		// getPath();

		// mImage = getResources().getDrawable(R.drawable.ic_launcher);

		mImage = new BitmapDrawable(getResources(), CameraScreen.myBitmap);
		mImage.setBounds(0, 0, mImage.getIntrinsicWidth(),
				mImage.getIntrinsicHeight());
//		mImage.setBounds(100, 60, mImage.getIntrinsicWidth()/2,
//				mImage.getIntrinsicHeight()/2);
	}

	/*
	 * private String getPath(Uri uri) { // TODO Auto-generated method stub
	 * String[] projection = { MediaStore.Images.Media.DATA }; Cursor cursor =
	 * managedQuery(uri, projection, null, null, null); if (cursor == null)
	 * return null; int column_index =
	 * cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	 * cursor.moveToFirst(); String s=cursor.getString(column_index);
	 * cursor.close(); return s; }
	 */

	public MyImage(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Let the ScaleGestureDetector inspect all events.
		mScaleDetector.onTouchEvent(ev);

		final int action = ev.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			final float x = ev.getX();
			final float y = ev.getY();

			mLastTouchX = x;
			mLastTouchY = y;
			mActivePointerId = ev.getPointerId(0);
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			final int pointerIndex = ev.findPointerIndex(mActivePointerId);
			final float x = ev.getX(pointerIndex);
			final float y = ev.getY(pointerIndex);

			// Only move if the ScaleGestureDetector isn't processing a gesture.
			if (!mScaleDetector.isInProgress()) {
				final float dx = x - mLastTouchX;
				final float dy = y - mLastTouchY;

				mPosX += dx;
				mPosY += dy;

				invalidate();
			}

			mLastTouchX = x;
			mLastTouchY = y;

			break;
		}

		case MotionEvent.ACTION_UP: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}

		case MotionEvent.ACTION_CANCEL: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}

		case MotionEvent.ACTION_POINTER_UP: {
			final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			final int pointerId = ev.getPointerId(pointerIndex);
			if (pointerId == mActivePointerId) {
				// This was our active pointer going up. Choose a new
				// active pointer and adjust accordingly.
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				mLastTouchX = ev.getX(newPointerIndex);
				mLastTouchY = ev.getY(newPointerIndex);
				mActivePointerId = ev.getPointerId(newPointerIndex);
			}
			break;
		}
		}

		return true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.save();
		// Log.d("DEBUG", "X: " + mPosX + " Y: " + mPosY);
		canvas.translate(mPosX, mPosY);
		canvas.scale(mScaleFactor, mScaleFactor);
		mImage.draw(canvas);
		canvas.restore();
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();

			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

			invalidate();
			return true;
		}
	}

}
