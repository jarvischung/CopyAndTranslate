package com.imrd.copy.service;

import com.imrd.copy.R;
import com.imrd.copy.translate.Google;
import com.imrd.copy.translate.TranslateClient;
import com.imrd.copy.translate.TranslateClient.TranslateAware;
import com.imrd.copy.util.LogProcessUtil;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class UpdateService extends Service implements ICountService,
		TranslateAware {

	public static final String TAG = UpdateService.class.getSimpleName();

	private ToggleRecentAppsButton mToggleOverlay;
	private int count;
	private ServiceBinder serviceBinder = new ServiceBinder();
	private Button mScaleText;
	private EditText mCopyText;
	private ClipboardManager cm;
	private ClipData cd;
	private ClipDescription cdc;
	private String beforeWord = "";
	float downXValue, downYValue;

	private TranslateClient transClient;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onStartService()");

		transClient = TranslateClient.newInstance();

		cm = (ClipboardManager) this.getSystemService(CLIPBOARD_SERVICE);
		cm.addPrimaryClipChangedListener(mPrimaryChangeListener);
		cd = cm.getPrimaryClip();
		// LogProcessUtil.LogPushD(TAG, "ClipData:" + cd.getItemAt(0) + "count:"
		// + cd.getItemCount());

		cdc = cm.getPrimaryClipDescription();
		// LogProcessUtil.LogPushD(TAG, "ClipDescription:" + cdc.getMimeType(0)
		// + "count:" + cdc.getMimeTypeCount());

		mToggleOverlay = new ToggleRecentAppsButton(UpdateService.this);
		mToggleOverlay.setContentView(R.layout.copy);
		mScaleText = (Button) mToggleOverlay.findViewById(R.id.scale_button);
		mScaleText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		mCopyText = (EditText) mToggleOverlay.findViewById(R.id.copy_text);
		mCopyText.setEnabled(true);
		mCopyText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// run translate
			}
		});

		mCopyText.setOnTouchListener(mOnTouchListener);
		mToggleOverlay.show();
	}

	ClipboardManager.OnPrimaryClipChangedListener mPrimaryChangeListener = new ClipboardManager.OnPrimaryClipChangedListener() {
		@Override
		public void onPrimaryClipChanged() {
			cd = cm.getPrimaryClip();
			String nowWord = "";
			try {
				nowWord = cd.getItemAt(0).getText().toString();
			} catch (Exception e) {
				nowWord = "";
			}
			if (beforeWord.equals(nowWord))
				return;

			LogProcessUtil.LogPushD(TAG, "ClipData:" + cd.getItemAt(0)
					+ "count:" + cd.getItemCount());

			if (!TextUtils.isEmpty(nowWord)) {
				beforeWord = nowWord;

				if (isNetOpen(UpdateService.this)) {
					transClient.requestTranslate(nowWord, UpdateService.this);
				} else {
					transClient.requestTranslateLocal(nowWord,
							UpdateService.this);
				}
			}

		}
	};

	/**
	 * a handler to process text update after translated.
	 */
	private Handler serviceUIUpdated = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			String text = "";
			if (msg.obj instanceof String) {
				text = (String) msg.obj;
			} else if (msg.obj instanceof Google) {
				Google model = (Google) msg.obj;
				text = model.sentences.get(0).trans;
			}

			mCopyText.setText(text);
		}
	};

	public class ServiceBinder extends Binder implements ICountService {

		public int getCount() {
			LogProcessUtil.LogPushD(TAG, "ServiceBinder:" + count);
			return count;
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return serviceBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		mToggleOverlay.hide();
		cm.removePrimaryClipChangedListener(mPrimaryChangeListener);
		return super.onUnbind(intent);
	}

	private final OnTouchListener mOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams) mCopyText
					.getLayoutParams();

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				downXValue = event.getX();
				downYValue = event.getY();
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				float currentX = event.getX();
				float currentY = event.getY();

				double sizeInX = Math.abs(downXValue - currentX);
				double sizeInY = Math.abs(downYValue - currentY);
				if (sizeInX > sizeInY) {
					// you better swipe horizontally
					LogProcessUtil.LogPushD(TAG, "Horizontally");

					if (downXValue < currentX) {
						LogProcessUtil.LogPushD(TAG, "Right");
						layout.width = layout.width - 30;
					}
					if (downXValue > currentX) {
						LogProcessUtil.LogPushD(TAG, "Left");
						layout.width = layout.width + 30;
					}
				} else {
					// you better swipe vertically
					LogProcessUtil.LogPushD(TAG, "Vertically");
					if (downXValue < currentX) {
						LogProcessUtil.LogPushD(TAG, "Down");
						layout.height = layout.height + 30;
					}
					if (downXValue > currentX) {
						LogProcessUtil.LogPushD(TAG, "Up");
						layout.height = layout.height - 30;
					}
				}

				mCopyText.setLayoutParams(layout);
			}

			return false;
		}
	};

	public class ToggleRecentAppsButton {
		private final Context mContext;
		private final WindowManager mWindowManager;
		private final ViewGroup mContentView;
		private final LayoutParams mParams;

		private ToggleRecentAppsButtonListener mListener;
		private boolean mVisible;

		public ToggleRecentAppsButton(Context context) {
			mContext = context;
			mWindowManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			mContentView = new SilentFrameLayout(context);

			mParams = new WindowManager.LayoutParams();
			mParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN
					| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
			mParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
			mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
			mParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
			mParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			mParams.format = PixelFormat.TRANSLUCENT;
			mParams.gravity |= Gravity.TOP | Gravity.RIGHT;
			setParams(mParams);

			mVisible = false;
		}

		public Context getContext() {
			return mContext;
		}

		public void setListener(ToggleRecentAppsButtonListener listener) {
			mListener = listener;
		}

		public final void show() {
			if (mVisible) {
				return;
			}

			mWindowManager.addView(mContentView, mParams);
			mVisible = true;

			if (mListener != null) {
				mListener.onShow(this);
			}

			onShow();
		}

		public final void hide() {
			if (!mVisible) {
				return;
			}

			mWindowManager.removeViewImmediate(mContentView);
			mVisible = false;

			if (mListener != null) {
				mListener.onHide(this);
			}

			onHide();
		}

		protected void onShow() {
			// Do nothing.
		}

		protected void onHide() {
			// Do nothing.
		}

		public LayoutParams getParams() {
			final LayoutParams copy = new LayoutParams();
			copy.copyFrom(mParams);
			return copy;
		}

		public void setParams(LayoutParams params) {
			mParams.copyFrom(params);

			if (mVisible) {
				mWindowManager.updateViewLayout(mContentView, mParams);
			}
		}

		public boolean isVisible() {
			return mVisible;
		}

		public void setContentView(int layoutResId) {
			final LayoutInflater inflater = LayoutInflater.from(mContext);
			inflater.inflate(layoutResId, mContentView);

		}

		public void setContentView(View content) {
			mContentView.removeAllViews();
			mContentView.addView(content);
		}

		public View getRootView() {
			return mContentView;
		}

		public View findViewById(int id) {
			return mContentView.findViewById(id);
		}
	}

	public interface ToggleRecentAppsButtonListener {

		public void onShow(ToggleRecentAppsButton overlay);

		public void onHide(ToggleRecentAppsButton overlay);
	}

	public static class SilentFrameLayout extends FrameLayout {
		public SilentFrameLayout(Context context) {
			super(context);
		}

		/**
		 * In API 14+ this is an override.
		 */
		public boolean requestSendAccessibilityEvent(View view,
				AccessibilityEvent event) {
			// Never send accessibility events.
			return false;
		}
	}

	@Override
	public int getCount() {
		return count;
	}

	/**
	 * @param context
	 * @return network on/off
	 */
	private static boolean isNetOpen(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if ((ni != null) && ni.isConnected()) {
			return ni.isConnected();
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.imrd.copy.translate.TranslateClient.TranslateAware#receiveTranslateText(java.lang.Object)
	 */
	@Override
	public void receiveTranslateText(Object transobj) {

		serviceUIUpdated.sendMessage(serviceUIUpdated.obtainMessage(1, transobj));

	}
}