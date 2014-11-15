package com.dipacommerce.dipasport.controls;

/**
 * Class support notify to parent class status loading
 */
public class DialogCallback {
    /**
     * When loading finished
     */
    public interface OnLoadingCompletedEvents {
        public void onLoadingCompleted();
    }

    /**
     * When user cancel dialog
     */
    public interface OnLoadingCancelEvents {
        public void onLoadingCancel();
    }
    
    /**
     * When loading time out
     *
     */
    public interface OnLoadingTimeOutEvents{
        public void OnLoadingTimeOut();
    }
}
