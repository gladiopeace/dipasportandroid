package com.dipacommerce.dipasport.network.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.dipacommerce.dipasport.BuildConfig;

public final class HttpPostRequest extends AsyncTask<String, Void, JSONObject> {

    public static final int REQUEST_DELAY = 2000;
    private InputStream is = null;
    private JSONObject jObj = null;
    private String json = "";

    private List<NameValuePair> mParams;
    private JsonListener mCallback;

    private boolean isCancel;

    public void setArguments(List<NameValuePair> args) {
        mParams = args;
    }

    public void setCallback(JsonListener callback) {
        mCallback = callback;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        //LogUtils.showLog("======wait " + REQUEST_DELAY + "ms======");
        try {
            // User can cancel this progress in 2 seconds
            Thread.sleep(REQUEST_DELAY);
        } catch (InterruptedException e) {
        }

        if (this.isCancelled()) return null;
        String url = params[0];
        //LogUtils.showLog(url);
        //LogUtils.showLogArgs(mParams);
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            if (mParams != null) {
                httpPost.setEntity(new UrlEncodedFormEntity(mParams));
            }

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            //LogUtils.showLog("====size: " + httpEntity.getContentLength());
            //LogUtils.showLog("====type: " + httpEntity.getContentType());
            //LogUtils.showLog("====encoding: " + httpEntity.getContentEncoding());
            is = httpEntity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            //LogUtils.showLog(json);
            jObj = new JSONObject(json);
        } catch (UnsupportedEncodingException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            callError(e.getMessage());
        } catch (ClientProtocolException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            callError(e.getMessage());
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            callError(e.getMessage());
        } catch (JSONException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            callError(e.getMessage());
        }

        // return JSON String
        return jObj;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if (result != null) {
            //LogUtils.showLog(result.toString());
            callSuccess(result);
        }
        callDone();
    }

    private void callError(String msg) {
        if (mCallback != null && !isCancel) {
            mCallback.onErrors(-1, msg);
        }
    }

    private void callSuccess(JSONObject json) {
        if (mCallback != null && !isCancel) {
            mCallback.onResults(json);
        }
    }

    private void callDone() {
        if (mCallback != null && !isCancel) {
            mCallback.onRequestFinished();
        }
    }

    @Override
    protected void onCancelled() {
        isCancel = true;
        super.onCancelled();
    }

    /**
     * Release resources
     */
    public void release() {
        is = null;
        jObj = null;
        json = null;
        if (mParams != null) {
            mParams.clear();
            mParams = null;
        }
        mCallback = null;
        isCancel = false;
    }

}
