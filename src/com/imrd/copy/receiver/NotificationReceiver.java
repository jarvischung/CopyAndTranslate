package com.imrd.copy.receiver;

import com.imrd.copy.service.ICountService;
import com.imrd.copy.service.UpdateService;
import com.imrd.copy.util.LogProcessUtil;
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
			
			Intent intent2 = new Intent(context,
					UpdateService.class);
			context.startService(intent2);
			
			//Can't bind service in onReceive()
			//context.bindService(intent2, serviceConnection,
			//		Context.BIND_AUTO_CREATE);
		} else if (action.equals("com.imrd.copy.action.clean")) {
			LogProcessUtil.LogPushD("", "Pressed Clean");
			Intent intent2 = new Intent(context,
					UpdateService.class);
			context.stopService(intent2);
		}
	}
	
	private ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {
			countService = (ICountService) service;
			LogProcessUtil.LogPushD("NotificationReceiver", "onServiceConnected count is "
					+ countService.getCount());
		}

		public void onServiceDisconnected(ComponentName name) {
			countService = null;
		}

	};
}
