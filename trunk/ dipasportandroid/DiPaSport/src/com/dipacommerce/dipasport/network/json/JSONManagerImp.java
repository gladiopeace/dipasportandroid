package com.dipacommerce.dipasport.network.json;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

/**
 * Implementation of JSON data fetching over HTTP request.
 */
public final class JSONManagerImp implements IJSONManager<JSONObject> {

    private static final String THREAD_NAME = "JSONManager";

    /**
     * URL for request to Server
     */
    private String mUrl;

    /**
	 * 
	 */
    private Callback<JSONObject> mCallback = null;

    /**
	 * 
	 */
    private Handler mHandler;

    /**
     * true: lock | false: unlock
     */
    // private static boolean mLock;

    private String mHeaderName = "Content-type";
    private String mHeaderValue = "application/json";

    // private String mReadJSONEncoding = "UTF-8";

    /**
     * JSON data
     */
    private String json = "";

    /**
     * @param context
     */
    public JSONManagerImp(final Context context) {
        mHandler = new Handler();
    }

    /**
     * @param context
     * @param url
     *            URL to fetch JSON from
     */
    public JSONManagerImp(final Context context, final String url) {
        this(context);
        mUrl = url;
    }

    /**
     * Fetches JSON from the URL specified in constructor.
     * 
     * @param url
     * @return
     */
    @Override
    public void getJsonFromUrl() {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(this.mUrl);
            httpGet.setHeader(mHeaderName, mHeaderValue);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            final int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                HttpEntity httpEntity = httpResponse.getEntity();
                json = EntityUtils.toString(httpEntity);
            } else {
                callOnErrors("HttpResponse Status Code: " + statusCode);
            }

        } catch (UnsupportedEncodingException e) {
            callOnErrors(e);
        } catch (ClientProtocolException e) {
            callOnErrors(e);
        } catch (IOException e) {
            callOnErrors(e);
        } catch (Exception e) {
            callOnErrors(e);
        }

        /**
         * try parse the string to a JSON object
         */
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
            callOnErrors(e);
        }

        callOnResults(jsonObj);

        callOnDone();
    } // END func getJsonFromUrl

    /**
     * Get JSON on thread
     */
    @Override
    public void execute() {
        // mLock = true; // Lock method
        new Thread(new Runnable() {

            @Override
            public void run() {
                // URL set in constructor
                getJsonFromUrl();
            }
        }, THREAD_NAME).start();
    }

    @Override
    @Deprecated
    public void execute(com.dipacommerce.dipasport.network.json.IJSONManager.HTTP_METHODS method) {
        
    }

    /**
     * Regiser callback with JSONManager to notify imlemented class
     */
    @Override
    public void registerCallback(final Callback<JSONObject> callback) {
        try {
            this.mCallback = callback;
        } catch (ClassCastException e) {
            throw new ClassCastException("You must implement interface IJSONManager.Callback<T>");
        }
    }

    /**
	 * 
	 */
    private void callOnResults(final JSONObject results) {
        if (null != this.mCallback) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // mLock = false; // release lock
                    mCallback.onResults(results);
                }
            });
        }
    }

    /**
	 * 
	 */
    private void callOnErrors(final Object errorMsg) {
        if (null != this.mCallback) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // mLock = false; // release lock
                    mCallback.onErrors(-1, errorMsg.toString());
                }
            });
        }
    }

    /**
     * 
     */
    private void callOnDone() {
        if (null != this.mCallback) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    mCallback.onDone();
                }
            });
        }
    }

    /**
     * Sets URL to fetch JSON from.
     * 
     * @param url
     */
    @Override
    public void setURL(final String url) {
        this.mUrl = url;
    }

}
