package com.dipacommerce.dipasport.utils;

public abstract class URLFix {

    /**
     * Replace '\\' with ''
     * 
     * @param url
     * @return
     */
    public static String Fix(String url) {
        String newStr = url.replace("\\", "");
        return newStr;
    }

    /**
     * Replace '|' characters in url by '%7C'
     * 
     * @param url
     * @return
     */
    public static String Fix2(String url) {
        String newStr = url.replace("|", "%7C");
        return newStr;
    }

    /**
     * Replace ' ' with %20
     * @param url
     * @return
     */
    public static String Fix3(String url) {
        String newStr = url.replace(" ", "%20");
        return newStr;
    }
    
    /**
     * Trim all spaces
     * @param url
     * @return
     */
    public static String Fix4(String url){
        String newStr = url.replace(" ", "");
        newStr = newStr.replace("%20", "");
        return newStr;
    }
}
