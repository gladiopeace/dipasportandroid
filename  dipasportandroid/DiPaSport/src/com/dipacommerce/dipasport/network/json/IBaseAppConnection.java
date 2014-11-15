package com.dipacommerce.dipasport.network.json;

/**
 *
 */
public interface IBaseAppConnection {
    /**
     * Support actions(hide keyboard, check network connection,...) before request
     */
    public void onPreRequest();

    /**
     * Call when all request finished. Must register callback
     * {@link com.zero.android.zntrackerpro.network.AppConnection#registerCallbackListener(com.zero.android.zntrackerpro.network.IBaseAppConnection)}
     * to get notify
     */
    public void onRequestFinished(int requestId);

    /**
     * Cancel a request
     *
     * @param requestId : A request Id called before with
     *                  {@link com.zero.android.zntrackerpro.network.AppConnection#request(int, String[])}
     *                  or
     *                  {@link com.zero.android.zntrackerpro.network.AppConnection#request(int, String[], boolean)}
     *                  or
     *                  {@link com.zero.android.zntrackerpro.network.AppConnection#request(int, String[], boolean, boolean)}
     */
    public void onRequestCanceled(int requestId);
}
