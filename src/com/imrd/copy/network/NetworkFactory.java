package com.imrd.copy.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.HttpURLConnection;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import com.imrd.copy.util.LogProcessUtil;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.Context;
import android.util.Log;

public class NetworkFactory implements Runnable {

	public static final int GET = 0;
	public static final int POST = 1;
	private String url;
	private int getOrPost = 0;
	private int apiType = 0;
    private APITYPE eApiType;
    public static enum APITYPE{
        NormalEntity, MultiPartEntity
    }

	public NetworkFactory(String url, int type) {
		this.getOrPost = GET;
		this.apiType = type;
		this.url = url;
	}

	public NetworkFactory(APITYPE type) {
		this.getOrPost = POST;
		this.eApiType = type;
	}

	public void start() {
		Thread thread = new Thread(this);
		try {
			thread.start();
		} catch (IllegalThreadStateException itse) {
			LogProcessUtil.LogPushE(this, "The Thread has been started before.");
		}
	}

	@Override
	public void run() {
		doRequest();
	}

	public void doRequest() {
        String result = null;
		if (POST == getOrPost) {
			try {
	            result = new HTTPPostProcess(eApiType).load(true);

	            LogProcessUtil.LogPushD(this, "HTTPPostProcess result:" + result);
	        }
	        catch (Exception e) {
	        	e.printStackTrace();
	        	LogProcessUtil.LogPushE(this, "Error!");
	        }
		} else {
			try{
				result = new HTTPGetProcess(url).getHttpResponse();
				
				LogProcessUtil.LogPushD(this, "HTTPGetProcess result:" + result);
			}catch(Exception e){
				e.printStackTrace();
	        	LogProcessUtil.LogPushE(this, "Error!");
			}
		}
	}
}
