package com.dipacommerce.dipasport.network.json;

/**
 * Interface for receiving JSON data over HTTP.
 */
public interface IJSONManager<T> {

    /**
     * Callback to receive loaded data.
     */
    public interface Callback<T> {
        /**
         * @param results
         */
        public void onResults(final T results);

        /**
         * Error HTTP, URL invaild
         * 
         * @param errorMsg
         */
        public void onErrors(final int _errorCode, final String _errorMessage);

        /**
         * 
         */
        public void onDone();
    }
    
    public enum HTTP_METHODS{
        GET, POST
    }

    /**
     * Execute http get in new thread.
     */
    public void execute();
    
    /**
     * @param method
     */
    public void execute(HTTP_METHODS method);

    /**
     * @param url
     * @return
     */
    public void getJsonFromUrl();

    /**
     * @param url
     */
    public void setURL(final String url);

    /**
     * @param callback
     */
    public void registerCallback(final Callback<T> callback);

}