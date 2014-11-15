package com.dipacommerce.dipasport.data;

import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;

import android.os.Bundle;
import android.util.Log;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.customer.CustomerInfo;
import com.dipacommerce.dipasport.shoppingcart.OrderProduct;
import com.dipacommerce.dipasport.shoppingcart.QuotationProduct;
import com.dipacommerce.dipasport.utils.URLFix;

public class URLFormatter {

    private static String mBuildURL = "";

    /**
     * 
     * @param userName
     * @param passwd
     * @return
     */
    public static String buildUrlLogin(final CustomerInfo customerInfo) {
        mBuildURL = String.format(Constants.Query.URL_LOGIN, customerInfo.getEmail(), customerInfo.getPassword());
        if (Constants.DEBUG_MODE) {
            Log.i("URLFormatter", "Login: " + mBuildURL);
        }
        return mBuildURL;
    }

    /**
     * 
     * @return
     */
    public static String buildUrlCategories() {
        mBuildURL = Constants.Query.URL_HOMEPAGE_CATEGORIES;
        if(DiPaSport.getLanguage() == DiPaSport.LANGUAGE_UK){
            mBuildURL += "&lang=en";
        }
        if (Constants.DEBUG_MODE) {
            Log.i("URLFormatter", "Categories: " + mBuildURL);
        }
        return mBuildURL;
    }

    /**
     * 
     * @param catId
     * @return
     */
    public static String buildUrlProductByCategories(String catId, int page, String idUser) {
        mBuildURL = String.format(Constants.Query.URL_HOMEPAGE_PRODUCTS_BY_CATEGORIE, catId, page, idUser);
        if(DiPaSport.getLanguage() == DiPaSport.LANGUAGE_UK){
            mBuildURL += "&lang=en";
        }
        if (Constants.DEBUG_MODE) {
            Log.i("URLFormatter", "Prodcut by categories: " + mBuildURL);
        }
        return mBuildURL;
    }

    /**
     * 
     * @return
     */
    public static String buildUrlProductFeature() {
        mBuildURL = Constants.Query.URL_HOMEPAGE_PRODUCTS;
        if(DiPaSport.getLanguage() == DiPaSport.LANGUAGE_UK){
            mBuildURL += "&lang=en";
        }
        if (Constants.DEBUG_MODE) {
            Log.i("URLFormatter", "ProductFeature: " + mBuildURL);
        }
        return mBuildURL;
    }

    /**
     * 
     * @return
     */
    public static String buildUrlProductDetail(String entityId, String idUser) {
        mBuildURL = String.format(Constants.Query.URL_PRODUCT_DETAIL, entityId, idUser);
        if(DiPaSport.getLanguage() == DiPaSport.LANGUAGE_UK){
            mBuildURL += "&lang=en";
        }
        if (Constants.DEBUG_MODE) {
            Log.i("URLFormatter", "Product details: " + mBuildURL);
        }
        return mBuildURL;
    }

    /**
     * 
     * @param keyword
     * @param categoryId
     * @return
     */
    public static String buildUrlSearchResult(String keyword, String categoryId, String idUser) {
        mBuildURL = String.format(Constants.Query.URL_SEARCH, keyword, idUser);
        mBuildURL = URLFix.Fix4(mBuildURL); 
        if(DiPaSport.getLanguage() == DiPaSport.LANGUAGE_UK){
            mBuildURL += "&lang=en";
        }
        if (Constants.DEBUG_MODE) {
            Log.i("URLFormatter", "Search results: " + mBuildURL);
        }
        return mBuildURL;
    }

    public static String buildUrlSearchResult(String keyword, int page, String idUser) {
        mBuildURL = String.format(Constants.Query.URL_SEARCH, keyword, page, idUser);
        mBuildURL = URLFix.Fix4(mBuildURL);
        if(DiPaSport.getLanguage() == DiPaSport.LANGUAGE_UK){
            mBuildURL += "&lang=en";
        }
        if (Constants.DEBUG_MODE) {
            Log.i("URLFormatter", "Search results: " + mBuildURL);
        }
        return mBuildURL;
    }

    /**
     * 
     * @param email
     * @param l
     * @return
     */
    public static String buildUrlOrder(CustomerInfo c, List<OrderProduct> l) {
        String orderItems = "";
        for (int i = 0; i < l.size(); i++) {
            orderItems += String.format("%s:%d", l.get(i).getProductInfo().getEntityID(), l.get(i).getQuantity());
            if (i < (l.size() - 1)) {
                orderItems += "|";
            }
        }
        mBuildURL = String.format(Constants.Query.URL_ORDER, c.getEmail(), c.getUserId(), orderItems);
        mBuildURL = URLFix.Fix2(mBuildURL);
        if (Constants.DEBUG_MODE) {
            Log.i("URLFormatter", "Order: " + mBuildURL);
        }
        return mBuildURL;
    }

    /**
     * 
     * @param registerinfo
     * @return
     */
    public static String buildUrlRegister(Bundle registerInfo) {
        String cusomertype = registerInfo.getString(CustomerInfo.REGISTER_INFO.customer_type);
        String fname = registerInfo.getString(CustomerInfo.REGISTER_INFO.first_name);
        String lname = registerInfo.getString(CustomerInfo.REGISTER_INFO.last_name);
        String email = registerInfo.getString(CustomerInfo.REGISTER_INFO.email);
        String passwd = registerInfo.getString(CustomerInfo.REGISTER_INFO.password);
        String company = registerInfo.getString(CustomerInfo.REGISTER_INFO.company);
        String taxvat = registerInfo.getString(CustomerInfo.REGISTER_INFO.taxvat);
        String street = registerInfo.getString(CustomerInfo.REGISTER_INFO.streetaddress);
        String city = registerInfo.getString(CustomerInfo.REGISTER_INFO.city);
        String state = registerInfo.getString(CustomerInfo.REGISTER_INFO.stateprovince);
        String postalcode = registerInfo.getString(CustomerInfo.REGISTER_INFO.postalcode);
        String country = registerInfo.getString(CustomerInfo.REGISTER_INFO.country);
        String telephone = registerInfo.getString(CustomerInfo.REGISTER_INFO.telephone);
        String fax = registerInfo.getString(CustomerInfo.REGISTER_INFO.fax);
        String newsletter = registerInfo.getString(CustomerInfo.REGISTER_INFO.newsletter);
        String client = registerInfo.getString(CustomerInfo.REGISTER_INFO.client);
        mBuildURL = String.format(Constants.Query.URL_REGISTER, cusomertype, fname, lname, email, passwd, company, taxvat, street, city, state, postalcode, country, telephone, fax, newsletter, client);
        mBuildURL = URLFix.Fix3(mBuildURL);
        if (Constants.DEBUG_MODE) {
            Log.i("URLFormatter", "Register: " + mBuildURL);
        }
        return mBuildURL;
    }

    /**
     * 
     * @param email
     * @return
     */
    public static String buildUrlLostPassword(String email) {
        mBuildURL = String.format(Constants.Query.URL_LOST_PASSWORD, email);
        if (Constants.DEBUG_MODE) {
            Log.i("URLFormatter", "Lost password: " + mBuildURL);
        }
        return mBuildURL;
    }

    /**
     * 
     * @param couponCode
     * @return
     */
    public static String buildUrlCoupon(String couponCode) {
        mBuildURL = String.format(Constants.Query.URL_COUPON, couponCode);
        if (Constants.DEBUG_MODE) {
            Log.i("URLFormatter", "Coupon: " + mBuildURL);
        }
        return mBuildURL;
    }

    public static String buildUrlQuote(CustomerInfo c, List<QuotationProduct> l) {
        String orderItems = "";
        for (int i = 0; i < l.size(); i++) {
            orderItems += String.format("%s:%d:%s", l.get(i).getProductInfo().getEntityID(), l.get(i).getQuantity(), l.get(i).getComment());
            if (i < (l.size() - 1)) {
                orderItems += "|";
            }
        }
        mBuildURL = String.format(Constants.Query.URL_QUOTATION, c.getEmail(), c.getUserId(), orderItems);
        mBuildURL = URLFix.Fix2(mBuildURL);
        if (Constants.DEBUG_MODE) {
            Log.i("URLFormatter", "Quote: " + mBuildURL);
        }
        return mBuildURL;
    }
    
    public static String buildUrl(List<NameValuePair> args, String url) {
        String query = generateArgs(args);
        return url + query;
    }

    /**
     * Build a list parameters used GET request
     *
     * @param args
     * @return
     */
    private static String generateArgs(List<NameValuePair> args) {
        String requestUrl = "";
        for (NameValuePair nameValuePair : args) {
            requestUrl += String.format(Locale.getDefault(), "&%s=%s", nameValuePair.getName(),
                    nameValuePair.getValue());
        }
        requestUrl = requestUrl.replace(" ", "%20");
        return requestUrl;
    }
}
