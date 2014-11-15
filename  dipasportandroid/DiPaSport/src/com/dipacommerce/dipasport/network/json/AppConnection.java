package com.dipacommerce.dipasport.network.json;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.utils.NetworkUtils;

/**
 *
 */
public abstract class AppConnection implements IAppConnection, JsonListener,
        DialogInterface.OnCancelListener {

    public static final int FLAG_CANCEL_ANIMATE_BACK = 1;
    public static final int FLAG_CANCEL_ANIMATE_TOUCH_OUTSIDE = 2;
    private static Activity mContext;
    private IBaseAppConnection mListener;
    private String mLoadingMessage;
    private boolean mResponseStatus;
    private JSONObject mJSONObject;
    private String mResonseMessage = "";
    private boolean mAnimated = false;
    private boolean mLockAnimated = false;
    private int mFlagCancel = 0;
    private int mRequestId = -1;

    private ProgressDialog mProgress;
    private HttpPostRequest mHttpPostRequest;

    /**
     * @param ctx
     */
    public AppConnection(FragmentActivity ctx) {
        mContext = ctx;
    }

    /**
     * @param ctx
     * @return
     */
    public static AppConnection getInstance(FragmentActivity ctx) {
        mContext = ctx;
        return new AppConnection(ctx) {
        };
    }

    /**
     * @param listener
     */
    public void registerCallbackListener(IBaseAppConnection listener) {
        mListener = listener;
    }

    /**
     *
     */
    @Override
    public final void onResults(JSONObject results) {
        mJSONObject = results;
        mResponseStatus = true;
    }

    /**
     *
     */
    @Override
    public final void onErrors(int _errorCode, String _errorMessage) {
        mResponseStatus = false;
        mResonseMessage = _errorMessage;
    }

    /**
     *
     */
    @Override
    public final void onCancel(DialogInterface dialog) {
        if (mHttpPostRequest != null) {
            mHttpPostRequest.cancel(true);
            mHttpPostRequest.release();
            mHttpPostRequest = null;
        }

        mLoadingMessage = "";
        mAnimated = false;
        mContext.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                onRequestCanceled(mRequestId);
            }
        });
    }

    /**
     * Cancel a request
     *
     * @param requestId
     */
    @Override
    public void onRequestCanceled(int requestId) {
        if (mListener != null) {
            mListener.onRequestCanceled(requestId);
        }
    }

    /**
     *
     */
    private boolean init() {
        if (mListener != null) {
            mListener.onPreRequest();
        }
        if (mHttpPostRequest == null) {
            if (!NetworkUtils.isConnected(mContext)) {
                Toast.makeText(mContext, mContext.getString(R.string.no_network_connection),
                        Toast.LENGTH_SHORT).show();
                return false;
            }

            if (mAnimated) {
                mProgress = new ProgressDialog(mContext);
                mProgress.setCancelable(!mLockAnimated);
                mProgress.setCanceledOnTouchOutside(false);
                if (TextUtils.isEmpty(mLoadingMessage)) {
                    mProgress.setMessage(mContext.getString(R.string.str_loading));
                } else {
                    mProgress.setMessage(mLoadingMessage);
                }
                mProgress.setOnCancelListener(this);
                mProgress.show();
            }

            mHttpPostRequest = new HttpPostRequest();
            mHttpPostRequest.setCallback(this);
            return true;
        }
        return false;
    }

    /**
     * Support actions(hide keyboard, check network connection,...) before
     * request
     */
    @Override
    public synchronized void onPreRequest() {

    }

    /**
     * Call when all request finished. Must register callback
     * {@link com.zero.android.zntrackerpro.network.AppConnection#registerCallbackListener(com.zero.android.zntrackerpro.network.IBaseAppConnection)}
     * to get notify
     *
     * @param requestId
     */
    @Override
    public synchronized void onRequestFinished(final int requestId) {
        if (mAnimated) {
            // Cancel progress dialog
            mProgress.dismiss();
            mProgress = null;
        }

        // Release HTTP request
        mHttpPostRequest.release();
        mHttpPostRequest = null;

        mLoadingMessage = "";
        mAnimated = false;
        mContext.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onRequestFinished(requestId);
                }
            }
        });
    }

    /**
     * Call when all request finished. Must register callback
     * {@link com.zero.android.zntrackerpro.network.AppConnection#registerCallbackListener(com.zero.android.zntrackerpro.network.IBaseAppConnection)}
     * to get notify
     */
    @Override
    public synchronized final void onRequestFinished() {
        this.onRequestFinished(mRequestId);
        mRequestId = -1; // reset it
    }

    /**
     * Avoid NullPointException when don't call {@link #init()} from override
     * method
     *
     * @return
     */
    public final boolean isNetworkConnected() {
        return mHttpPostRequest != null;
    }

    /**
     * Get status of JSON when request
     *
     * @return
     */
    public final boolean isResultsOK() {
        return mResponseStatus;
    }

    /**
     * Get message when response
     *
     * @return
     */
    public final String getResponseMessage() {
        return mResonseMessage;
    }

    /**
     * Get a JSONObject
     *
     * @return
     */
    public final JSONObject getResultJsonObject() {
        return mJSONObject;
    }

    /**
     * Set content of loading dialog
     *
     * @param message
     */
    public final void setLoadingMessage(String message) {
        mLoadingMessage = message;
    }

    /**
     * Get request object
     *
     * @return
     */
    public final void request(int requestId, String[] args) {
        mRequestId = requestId;
        if (init()) {
            mHttpPostRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args);
        }
    }

    /**
     * @param args
     * @param animated
     */
    public final void request(int requestId, String[] args, boolean animated) {
        mAnimated = animated;
        this.request(requestId, args);
    }

    public final void request(int requestId, String[] args, boolean animated, boolean lockAnimation) {
        mAnimated = animated;
        mLockAnimated = lockAnimation;
        this.request(requestId, args);
    }

    /**
     * @return : Returned current connection
     */
    public final IAppConnection getConnection() {
        return this;
    }

    /**
     * @return
     */
    @SuppressWarnings("unused")
    public final int getFlags() {
        return mFlagCancel;
    }

    /**
     * @param flags
     */
    @SuppressWarnings("unused")
    public final void setFlags(int flags) {
        mFlagCancel = flags;
    }
}
