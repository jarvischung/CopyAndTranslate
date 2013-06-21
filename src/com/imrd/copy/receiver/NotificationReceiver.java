package com.imrd.copy.receiver;

import com.imrd.copy.CopyAndTranslateActivity;
import com.imrd.copy.R;
import com.imrd.copy.service.ICountService;
import com.imrd.copy.service.UpdateService;
import com.imrd.copy.util.LogProcessUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class NotificationReceiver extends BroadcastReceiver {

	private ICountService countService;

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		LogProcessUtil.LogPushD("", "I Arrived: " + action);

		if (action.equals("com.imrd.copy.action.start")) {
			LogProcessUtil.LogPushD("", "Pressed Start");

			if (!CopyAndTranslateActivity.isRunService) {
				Intent intent2 = new Intent(context, UpdateService.class);
				context.startService(intent2);
			} else {
				LogProcessUtil.LogPushD("", "Pressed Clean");
				Intent intent2 = new Intent(context, UpdateService.class);
				context.stopService(intent2);
			}

			CopyAndTranslateActivity.isRunService = !CopyAndTranslateActivity.isRunService;

			// Can't bind service in onReceive()
			// context.bindService(intent2, serviceConnection,
			// Context.BIND_AUTO_CREATE);
		} else if (action.equals("com.imrd.copy.action.clean")) {
			// TODO clean history
		} else if (action.equals("com.imrd.copy.action.speech")) {
			LogProcessUtil.LogPushD("", "Pressed Speech");
			UpdateService.isSpeech = !UpdateService.isSpeech;
		}

		updateNotification(context, action);
	}

	private void updateNotification(Context context, String action) {
		Intent intent = new Intent("com.imrd.copy.action.start");
		PendingIntent pIntentStart = PendingIntent.getBroadcast(context, 0,
				intent, 0);

		Intent intent2 = new Intent("com.imrd.copy.action.clean");
		PendingIntent pIntentClean = PendingIntent.getBroadcast(context, 0,
				intent2, 0);

		Intent intent3 = new Intent("com.imrd.copy.action.speech");
		PendingIntent pIntentSpeech = PendingIntent.getBroadcast(context, 0,
				intent3, 0);

		String actionStart = "Start";
		String actionSpeech = "Speech";

		if (CopyAndTranslateActivity.isRunService)
			actionStart = "Stop";
		else
			actionStart = "Start";

		if (UpdateService.isSpeech)
			actionSpeech = "Speech";
		else
			actionSpeech = "No speech";

		Notification noti = new Notification.Builder(context)
				.setContentTitle("CopyAndTranslate").setContentText("Subject")
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentIntent(pIntentStart)
				.addAction(R.drawable.ic_launcher, actionStart, pIntentStart)
				.addAction(R.drawable.ic_launcher, "Clean", pIntentClean)
				.addAction(R.drawable.ic_launcher, actionSpeech, pIntentSpeech)
				.build();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(context.NOTIFICATION_SERVICE);

		noti.flags |= Notification.FLAG_NO_CLEAR
				| Notification.FLAG_ONLY_ALERT_ONCE
				| Notification.FLAG_FOREGROUND_SERVICE;

		notificationManager.notify(0, noti);
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {
			countService = (ICountService) service;
			LogProcessUtil.LogPushD("NotificationReceiver",
					"onServiceConnected count is " + countService.getCount());
		}

		public void onServiceDisconnected(ComponentName name) {
			countService = null;
		}

	};
}
