package com.dipacommerce.dipasport.controls;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;

/**
 * Base class for splash screens.
 */
public abstract class SplashScreen extends FragmentActivity {

    private boolean mIsBackButtonPressed = false;
    protected int mTimeDuration = 3; // default 2s
    private boolean mCancelable = false;
    private Callback mCallback;

    /**
     * Callback to receive splash-screen time-out event.
     */
    public interface Callback {
        public void onTimeOut();
    }

    public void RegisterCallback(Callback _activity) {
        /**
         * Make sure Parent activity implement from interface
         * NISplashScreen.Callback
         */
        try {
            mCallback = (Callback) _activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(_activity.toString() + " must implement NISplashScreen.Callback");
        }
    }

    /**
     * Start splash with set new time duration
     * 
     * @param _timeduration
     */
    protected void StartSplashScreen(int _timeduration) {
        /**
         * Run a thread after 2 seconds(default) to start the home screen
         */
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mCancelable) {
                    SplashScreen.this.finish(); // Detroy this activity
                }
                if (!mIsBackButtonPressed) {
                    // Notify to parent implement
                    mCallback.onTimeOut();
                }
            }

        }, _timeduration * 1000);
    }

    /**
     * Start Splash with default time duration
     */
    protected void StartSplashScreen() {
        StartSplashScreen(mTimeDuration);
    }

    @Override
    public void onBackPressed() {
        mIsBackButtonPressed = true;
        super.onBackPressed();
    }
    
    public void setCancelable(boolean cancelable){
        mCancelable = cancelable;
    }
}
// end of file TRSplashScreen.java
