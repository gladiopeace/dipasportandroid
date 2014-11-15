package com.dipacommerce.dipasport.network.json;

import org.json.JSONObject;

/**
 * Callback to receive JSON.
 */
public interface JsonListener extends IBaseAppConnection {

    /**
     * A JSONObject will be returned, otherwise null returned
     *
     * @param results : A JSONObject, can be null.
     */
    public void onResults(final JSONObject results);

    /**
     * Error HTTP, URL invaild
     *
     * @param _errorCode    : always -1
     * @param _errorMessage : an exception message
     */
    public void onErrors(final int _errorCode, final String _errorMessage);

    /**
     * Call when all request finished. Must register callback
     * {@link com.zero.android.zntrackerpro.network.AppConnection#registerCallbackListener(com.zero.android.zntrackerpro.network.IBaseAppConnection)}
     * to get notify
     */
    public void onRequestFinished();
}
