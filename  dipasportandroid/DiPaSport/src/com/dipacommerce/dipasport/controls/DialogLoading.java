package com.dipacommerce.dipasport.controls;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Loading progress dialog.
 */
public class DialogLoading extends DialogManagement {

    private boolean mFlagShowLog = false;
    private long mMilisecond = 0;
    private Timer mTimerTimeOut;

    public DialogLoading(Context _context) {
        super(_context);
    }

    /**
     * @param _value
     */
    public void setFlagShowLog(boolean _value) {
        this.mFlagShowLog = _value;
    }

    /**
     * Flag on cancel loading
     */
    private ProgressDialog mLoadingDialog;

    @Override
    public synchronized void showDialog() {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                if (mFlagShowLog) {
                }
                if (null != mContext) {
                    if (null == mLoadingDialog) {
                        mLoadingDialog = new ProgressDialog(mContext);
                    }
                    if (null != mMessage) {
                        mLoadingDialog.setMessage(mMessage);
                    }

                    if (null != mTitle) {
                        mLoadingDialog.setTitle(mTitle);
                    }
                    mLoadingDialog.setCancelable(mIsCancel);
                    if (mOnCancelDialogListener != null) {
                        mLoadingDialog.setOnCancelListener(mOnCancelDialogListener);
                    }
                    executeTimeOut();
                    mLoadingDialog.show();
                }

            }
        });
    }

    @Override
    public synchronized void closeDialog() {
        super.closeDialog();
        destroyTimeout();
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                if (mFlagShowLog) {
                }
                if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }

                if (mCallbackLoadingCompleted != null) {
                    mCallbackLoadingCompleted.onLoadingCompleted();
                }
                runLoadingCompleted();
            }
        });
    }

    /**
     * Set timeout for loading dialog. It will auto close after _milisecond
     * 
     * @param _milisecond
     */
    public void setTimeOut(long _milisecond) {
        this.mMilisecond = _milisecond;
    }

    /**
     * Cancel schedule timer
     */
    public void destroyTimeout() {
        if (mTimerTimeOut != null) {
            this.mTimerTimeOut.cancel();
            this.mTimerTimeOut.purge();
            this.mTimerTimeOut = null;
        }
    }

    /**
     * 
     */
    private void executeTimeOut() {
        if (mMilisecond > 0) {
            this.mTimerTimeOut = new Timer(true);
            this.mTimerTimeOut.schedule(new TimerTask() {

                @Override
                public void run() {
                    if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                        mLoadingDialog = null;
                    }
                    runLoadingTimeOut();
                    destroyTimeout();
                }
            }, mMilisecond);
        }
    }

}
