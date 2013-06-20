package com.imrd.copy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.imrd.copy.dict.StarDict;
import com.imrd.copy.service.ICountService;
import com.imrd.copy.service.UpdateService;
import com.imrd.copy.util.LogProcessUtil;

public class CopyAndTranslateActivity extends Activity {

	private String TAG = CopyAndTranslateActivity.class.getName();
	private String sourceLang = "en";
	private String desLang = "zh-TW";
	private String text = "Hello!!";
	private boolean isRunService = false;
	
	public static String defaultSDCardPath = "/sdcard/copyandtranslate/langdao/";
	public static String defaultDictName = "langdao_ec_gb";

	private ICountService countService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_copy);
		
		final Intent intent = new Intent(CopyAndTranslateActivity.this,
				UpdateService.class);
		// startService(intent);

		Button bb = (Button) this.findViewById(R.id.button);
		bb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isRunService) {
					Intent intent2 = new Intent(CopyAndTranslateActivity.this,
							UpdateService.class);
					bindService(intent2, serviceConnection,
							Context.BIND_AUTO_CREATE);
					isRunService = true;
				} else {
					unbindService(serviceConnection);
					isRunService = false;
				}
				
				copyAssetsToSD();
			}

		});

		// LogProcessUtil.LogPushD(TAG, "Explanation:" + new
		// StarDict().getExplanation("test") );
		
	}

	private void copyAssetsToSD() {
		AssetManager assetManager = getAssets();
		String[] files = null;
		try {
			files = assetManager.list("langdao");
			File f = new File(defaultSDCardPath);
			f.mkdirs();
		} catch (Exception e) {
			LogProcessUtil.LogPushD("tag", "Failed to get asset file list.");
			e.printStackTrace();
		}
		for (String filename : files) {
			InputStream in = null;
			OutputStream out = null;
			try {
				if (new File(defaultSDCardPath + filename)
						.exists())
					continue;
				
				in = assetManager.open("langdao/" + filename);
				out = new FileOutputStream(defaultSDCardPath
						+ filename);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (IOException e) {
				LogProcessUtil.LogPushD("tag", "Failed to copy asset file: "
						+ filename);
				e.printStackTrace();
			}
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {
			countService = (ICountService) service;
			LogProcessUtil.LogPushD(TAG, "onServiceConnected count is "
					+ countService.getCount());
		}

		public void onServiceDisconnected(ComponentName name) {
			countService = null;
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.copy_and_translate, menu);
		return true;
	}

}