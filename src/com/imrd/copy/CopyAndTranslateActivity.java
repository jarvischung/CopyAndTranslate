package com.imrd.copy;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.imrd.copy.network.NetworkFactory;
import com.imrd.copy.service.ICountService;
import com.imrd.copy.service.UpdateService;
import com.imrd.copy.util.LogProcessUtil;

public class CopyAndTranslateActivity extends Activity {

	private String TAG = CopyAndTranslateActivity.class.getName();
	private String sourceLang = "en";
    private String desLang = "zh-TW";
    private String text = "Hello!!";
    private boolean isRunService = false;
    
    private ICountService countService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy);

        //ClipboardManager cm = (ClipboardManager) this.getSystemService(CLIPBOARD_SERVICE);
        
        String url = String.format(getString(R.string.translate_url), this.sourceLang, this.desLang, this.text);
        new NetworkFactory(url, NetworkFactory.GET).start();
        
        final Intent intent = new Intent(CopyAndTranslateActivity.this, UpdateService.class);
    	//startService(intent);
        
        Button bb = (Button) this.findViewById(R.id.button);
        bb.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!isRunService){
					Intent intent2 = new Intent(CopyAndTranslateActivity.this, UpdateService.class);
					bindService(intent2, serviceConnection, Context.BIND_AUTO_CREATE);
					isRunService = true;
				}else{
					unbindService(serviceConnection);
					isRunService = false;
				}
			}
        	
        });
    	//this.getApplicationContext().bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }
    
    private ServiceConnection serviceConnection = new ServiceConnection() {
    	 
        public void onServiceConnected(ComponentName name, IBinder service) {
            countService = (ICountService)service;
            LogProcessUtil.LogPushD(TAG, "onServiceConnected count is " + countService.getCount());
        }
 
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
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