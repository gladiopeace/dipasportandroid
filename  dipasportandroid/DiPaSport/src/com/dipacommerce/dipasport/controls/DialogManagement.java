package com.dipacommerce.dipasport.controls;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.dipacommerce.dipasport.controls.DialogCallback.OnLoadingCancelEvents;
import com.dipacommerce.dipasport.controls.DialogCallback.OnLoadingCompletedEvents;
import com.dipacommerce.dipasport.controls.DialogCallback.OnLoadingTimeOutEvents;

/**
 * Class supports managing dialogs.
 */
public abstract class DialogManagement implements IOnDialogListener {

    protected Context mContext;
    protected OnLoadingCompletedEvents mCallbackLoadingCompleted;
    protected OnLoadingCancelEvents mCallbackLoadingCancel;
    protected OnLoadingTimeOutEvents mCallbackLoadingTimeOut;

    /* Progress dialog parameters */
    protected boolean mIsCancel = false;
    protected boolean mIsClosed = false;
    protected String mTitle = null;
    protected String mMessage = null;

    protected int mDialogStyle = AlertDialog.THEME_HOLO_LIGHT;

    /**
     * Constructor
     * 
     * @param _context
     */
    public DialogManagement(Context _context) {
        this.mContext = _context;
    }

    /**
     * @param _callback
     */
    public void RegisterCallbackLoadingCancel(OnLoadingCancelEvents _callback) {
        this.mCallbackLoadingCancel = _callback;
    }

    /**
     * 
     * @param _callback
     */
    public void RegisterCallbackLoadingTimeOut(OnLoadingTimeOutEvents _callback) {
        this.mCallbackLoadingTimeOut = _callback;
    }

    /**
     * 
     * @param _callback
     */
    public void RegisterCallbackLoadingCompleted(OnLoadingCompletedEvents _callback) {
        this.mCallbackLoadingCompleted = _callback;
    }

    /**
     * 
     */
    @Deprecated
    protected synchronized void runLoadingCancel() {
        if (this.mCallbackLoadingCancel != null) {
            ((Activity) mContext).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mCallbackLoadingCancel.onLoadingCancel();
                }
            });
        }

    }

    protected synchronized void runLoadingCompleted() {
        if (this.mCallbackLoadingCompleted != null) {
            ((Activity) mContext).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mCallbackLoadingCompleted.onLoadingCompleted();
                }
            });
        }

    }

    protected synchronized void runLoadingTimeOut() {
        if (this.mCallbackLoadingTimeOut != null) {
            ((Activity) mContext).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mCallbackLoadingTimeOut.OnLoadingTimeOut();
                }
            });
        }

    }

    /**
     * Flag on cancel loading
     */
    protected boolean mOnCancalLoading = false;

    /**
     * Close dialog by user (return true and notify to callback)
     * 
     * @return
     */
    public boolean IsInterrupted() {
        return mOnCancalLoading;
    }

    /**
     * @param _title
     */
    public void setTitle(String _title) {
        this.mTitle = _title;
    }

    /**
     * @param _message
     */
    public void setMessage(String _message) {
        this.mMessage = _message;
    }

    @Override
    public void closeDialog() {
        this.mOnCancalLoading = false;
        this.mIsClosed = true;
    }

    /**
     * 
     */
    protected OnCancelListener mOnCancelDialogListener = new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
            mOnCancalLoading = true;
            runLoadingCancel();
        }
    };

    /**
     * User can cancel dialog
     * 
     * @param _value
     */
    public void setCancel(boolean _value) {
        this.mIsCancel = _value;
    }

    public void setDialogStyle(int _style) {
        this.mDialogStyle = _style;
    }

}
