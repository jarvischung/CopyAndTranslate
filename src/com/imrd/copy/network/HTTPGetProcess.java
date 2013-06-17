package com.imrd.copy.network;

/**
 * Created by jarvis on 13/5/20.
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.util.Log;

class HTTPGetProcess {
    private static final String TAG = "HTTPGetProcess";
    
    private static String url = null;

    public HTTPGetProcess() {}
    
    public HTTPGetProcess(String url) {
    	this.url = url;
    }

    public static String getHttpResponse() {
        Log.d(TAG, "Going to make a get request");
        StringBuilder response = new StringBuilder();
        try {
            HttpGet get = new HttpGet(url);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                Log.d(TAG, "HTTP Get succeeded:" + url);

                HttpEntity messageEntity = httpResponse.getEntity();
                InputStream is = messageEntity.getContent();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() + " " + url);
        }
        Log.d(TAG, "Done with HTTP getting\n" + response.toString());

        return response.toString();
    }
}
