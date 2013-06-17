package com.imrd.copy.network;

import java.io.IOException;

import android.util.Log;

public abstract class RetryHttp {
    private static final String TAG = "RetryHttp";
    private int retry = 3;

    public String doGet() throws IOException {
        throw new UnsupportedOperationException("doGet()");
    };

    public String doPost() throws IOException {
        throw new UnsupportedOperationException("doPost()");
    };

    public String load(boolean post) throws Exception {
        int current = 0;
        while (current < this.retry) {
            current++;
            try {
                if (post) {
                    return this.doPost();
                }
                return this.doGet();
            }
            catch (IOException e) {
                Log.e(TAG, current + "Network error!" + e.getMessage());
                if (current >= this.retry) {
                    Log.e(TAG, "Retry fail!");
                    throw e;
                }
            }
            catch (Exception e) {
                Log.e(TAG, current + "unknow error!" + e.getMessage());
                if (current >= this.retry) {
                    Log.e(TAG, "Retry fail!");
                    throw e;
                }
            }
        }
        return null;
    }
}
