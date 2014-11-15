package com.dipacommerce.dipasport.network.json;

import android.content.Context;

import org.json.JSONObject;

/**
 * JSonManager factory.
 */
public abstract class JSONManager {

    private static JSONManagerImp sJSONManagerImp;

    /**
     * @param context
     * @return
     */
    public static IJSONManager<JSONObject> getNewInstance(final Context context) {
        sJSONManagerImp = new JSONManagerImp(context);
        return sJSONManagerImp;
    }

    /*
	 * 
	 */
    public static IJSONManager<JSONObject> getNewInstance(final Context context, final String url) {
        sJSONManagerImp = new JSONManagerImp(context, url);
        return sJSONManagerImp;
    }

}
