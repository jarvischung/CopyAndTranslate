package com.imrd.copy.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.util.EntityUtils;
import android.util.Log;
import com.imrd.copy.network.NetworkFactory.APITYPE;

class HTTPPostProcess extends RetryHttp {
	private static final String TAG = "HTTPPostProcess";
	private APITYPE apiType;

	public HTTPPostProcess(APITYPE apiType) {
		this.apiType = apiType;
	}

	@Override
	public String doPost() throws IOException {
		HttpClient client = HttpClientFactory.getThreadSafeClient();
		HttpPost request = new HttpPost(getUrl());
		
		if(apiType==APITYPE.MultiPartEntity)
			request.setEntity(this.createMultiPartEntity());
		else
			request.setEntity(this.createNormalEntity());
		
		String responseText = client.execute(request, responseHandler);
		Log.d(TAG, "url:" + getUrl() + " Response:" + responseText);
		return responseText;
	}
	
	ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
	    public String handleResponse(final HttpResponse response)
	        throws HttpResponseException, IOException {
	        StatusLine statusLine = response.getStatusLine();
	        if (statusLine.getStatusCode() >= 300) {
	            throw new HttpResponseException(statusLine.getStatusCode(),
	                    statusLine.getReasonPhrase());
	        }

	        HttpEntity entity = response.getEntity();
	        return entity == null ? null : EntityUtils.toString(entity, "UTF-8");
	    }
	};
	
	private String getUrl(){
		return "";
	}
	
	private MultipartEntity createMultiPartEntity(){
		MultipartEntity reqEntity = new MultipartEntity();
		//reqEntity = (MultipartEntity) DataProvider.getPostData(apiType);
		return reqEntity;
	}

	private UrlEncodedFormEntity createNormalEntity()
			throws UnsupportedEncodingException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//params = (List<NameValuePair>) DataProvider.getPostData(apiType);
		
		return new UrlEncodedFormEntity(params, "UTF-8");
	}
}
