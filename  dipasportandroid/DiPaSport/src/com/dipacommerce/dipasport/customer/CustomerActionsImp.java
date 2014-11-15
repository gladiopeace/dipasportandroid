package com.dipacommerce.dipasport.customer;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.dipacommerce.dipasport.IDiPaSport;
import com.dipacommerce.dipasport.data.Constants;
import com.dipacommerce.dipasport.data.URLFormatter;
import com.dipacommerce.dipasport.network.json.JSONManager;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;
import com.dipacommerce.dipasport.products.IProduct;

class CustomerActionsImp extends CustomerManager {

    private Callback<JSONObject> mCallbackLogin;
    private Callback<JSONObject> mCallbackRegister;
    private Callback<JSONObject> mCallbackLostPassword;

    private static boolean mIsLogin = false;

    private static String mJSONPrice = IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.NORMALE;

    public CustomerActionsImp() {
        if (sJSONManager == null) {
            sJSONManager = JSONManager.getNewInstance(sCtx);
        }
    }

    @Override
    public boolean isLogin() {
        return getLogin();
    }

    @Override
    public boolean isLogout() {
        return !getLogin();
    }

    @Override
    public void customerLostPassword(String email) {
        Log.i("Customer", "lost password");
        if (email != null) {
            if (sJSONManager != null) {
                if (mCallbackLostPassword != null) {
                    sJSONManager.registerCallback(mCallbackLostPassword);
                }
                sJSONManager.setURL(URLFormatter.buildUrlLostPassword(email));
                sJSONManager.execute();
            }
        }
    }

    @Override
    public void customerLogin(CustomerInfo customerInfo) {
        if (customerInfo != null) {
            saveCustomerInfo(customerInfo);
            if (sJSONManager != null) {
                if (mCallbackLogin != null) {
                    sJSONManager.registerCallback(mCallbackLogin);
                }
                sJSONManager.setURL(URLFormatter.buildUrlLogin(customerInfo));
                sJSONManager.execute();
            }
        }
    }

    @Override
    public void customerRegister(Bundle customerInfo) {
        if (customerInfo != null) {
            if (sJSONManager != null) {
                if (mCallbackRegister != null) {
                    sJSONManager.registerCallback(mCallbackRegister);
                }
                sJSONManager.setURL(URLFormatter.buildUrlRegister(customerInfo));
                sJSONManager.execute();
            }
        }
    }

    @Override
    public void registerCallbackLogin(Callback<JSONObject> callback) {
        mCallbackLogin = callback;
    }

    @Override
    public void registerCallbackReg(Callback<JSONObject> callback) {
        mCallbackRegister = callback;
    }

    @Override
    public void registerCallbackLostPassword(Callback<JSONObject> callback) {
        mCallbackLostPassword = callback;
    }

    @Override
    public void logout() {
        clearLogin();
        sDatabaseHandler.close();
    }

    @Override
    public boolean loginJSONChecker(final JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                Object login = jsonObject.getInt(CustomerInfo.JSON_TAG.LoginTag);
                Object errorCode = jsonObject.get(CustomerInfo.JSON_TAG.ERROR_CODE);

                if (login.equals(CODE.OK.getStatusCode()) && errorCode.equals(CODE.OK.getStatusCode())) {
                    JSONObject data = jsonObject.getJSONObject(IDiPaSport.JSON_STATUS_TAG.DATA);
                    if (data != null) {
                        String userId = data.getString(CustomerInfo.JSON_TAG.UserID);
                        String fullname = data.getString(CustomerInfo.JSON_TAG.FullName);
                        String gid = data.getString(CustomerInfo.JSON_TAG.GroupId);
                        String company = data.getString(CustomerInfo.JSON_TAG.Company);
                        String telephone = data.getString(CustomerInfo.JSON_TAG.Telephone);

                        // checking group type
                        int groupId = Integer.parseInt(gid);

                        setJSONPrice(getPriceType(data));
                        addCustomerInfo(userId, fullname, groupId, company, telephone);
                    }
                    setLogin();
                    mIsLogin = true;
                    return mIsLogin;
                }
            } catch (JSONException e) {
                return mIsLogin;
            }

        }
        return mIsLogin;
    }

    @Override
    public String getPriceType(JSONObject json) {
        String gidObj = "0";
        try {
            gidObj = json.getString(CustomerInfo.JSON_TAG.GroupId);
        } catch (JSONException e) {
            if (Constants.DEBUG_MODE) {
                e.printStackTrace();
            }
        }
        int gid = Integer.parseInt(gidObj);
        updateCustomerInfo(gid);

        String priceType = "";
        switch (gid) {
        case CustomerGroup.OFFICINA:
            priceType = IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.OFFICINA;
            break;
        case CustomerGroup.RICAMBISTA:
        case CustomerGroup.RICAMBISTA_A:
            priceType = IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.RICAMBISTA;
            break;
        case CustomerGroup.GROSSISTA:
        case CustomerGroup.GROSSISTA_A:
            priceType = IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.GROSSISTA;
            break;
        case CustomerGroup.OFF_S:
            priceType = IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.OFF_S;
            break;
        default:
            priceType = IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.NORMALE;
            break;
        }
        return priceType;
    }

    /**
     * 
     * @param jsonObject
     * @return
     */
    @Override
    public String getMessage(final JSONObject jsonObject) {
        String message = "";
        if (jsonObject != null) {
            try {
                message = jsonObject.getString(CustomerInfo.JSON_TAG.MESSAGE);
                return message;
            } catch (JSONException e) {
                if (Constants.DEBUG_MODE) {
                    e.printStackTrace();
                }
            }
        }
        return message;
    }

    /**
     * 
     */
    private void setLogin() {
        SharedPreferences.Editor editor = sCtx.getSharedPreferences(CustomerInfo.Prefs.LOGIN, Context.MODE_PRIVATE).edit();
        if (null != editor) {
            editor.putBoolean("LOGIN", true);
            editor.commit();
        }
    }

    /**
     * 
     * @return
     */
    private boolean getLogin() {
        SharedPreferences editor = sCtx.getSharedPreferences(CustomerInfo.Prefs.LOGIN, Context.MODE_PRIVATE);
        if (null != editor) {
            boolean result = editor.getBoolean("LOGIN", false);
            return result;
        }
        return false;
    }

    /**
	 * 
	 */
    private void clearLogin() {
        SharedPreferences.Editor editor = sCtx.getSharedPreferences(CustomerInfo.Prefs.LOGIN, Context.MODE_PRIVATE).edit();
        if (null != editor) {
            editor.clear();
            editor.commit();
        }
    }

    @Override
    public void login() {

    }

    @Override
    public CustomerInfo getCustomerInfo() {
        return loadCustomerInfo();
    }

    /**
     * 
     * @param customerInfo
     */
    public void saveCustomerInfo(CustomerInfo customerInfo) {
        SharedPreferences.Editor editor = sCtx.getSharedPreferences(CustomerInfo.Prefs.INFO, Context.MODE_PRIVATE).edit();
        if (null != editor) {
            editor.putString(CustomerInfo.BUNDLE.Email, customerInfo.getEmail());
            editor.commit();
        }
    }

    public void addCustomerInfo(String userId, String fullname, int groupId, String company, String telephone) {
        SharedPreferences.Editor editor = sCtx.getSharedPreferences(CustomerInfo.Prefs.INFO, Context.MODE_PRIVATE).edit();
        if (null != editor) {
            editor.putString(CustomerInfo.JSON_TAG.UserID, userId);
            editor.putString(CustomerInfo.JSON_TAG.FullName, fullname);
            editor.putInt(CustomerInfo.JSON_TAG.GroupId, groupId);
            editor.putString(CustomerInfo.JSON_TAG.Company, company);
            editor.putString(CustomerInfo.JSON_TAG.Telephone, telephone);
            editor.commit();
        }
    }

    /**
     * Update customer group id when get product details
     * 
     * @param groupId
     */
    public void updateCustomerInfo(int groupId) {
        SharedPreferences.Editor editor = sCtx.getSharedPreferences(CustomerInfo.Prefs.INFO, Context.MODE_PRIVATE).edit();
        if (null != editor) {
            editor.putInt(CustomerInfo.JSON_TAG.GroupId, groupId);
            editor.commit();
        }
    }

    /**
     * 
     * @return
     */
    public CustomerInfo loadCustomerInfo() {
        SharedPreferences editor = sCtx.getSharedPreferences(CustomerInfo.Prefs.INFO, Context.MODE_PRIVATE);
        if (null != editor) {
            String email = editor.getString(CustomerInfo.BUNDLE.Email, "");
            String userId = editor.getString(CustomerInfo.JSON_TAG.UserID, "");
            String fullname = editor.getString(CustomerInfo.JSON_TAG.FullName, "");
            int groupId = editor.getInt(CustomerInfo.JSON_TAG.GroupId, CustomerGroup.NORMALE);
            String company = editor.getString(CustomerInfo.JSON_TAG.Company, "");
            String telephone = editor.getString(CustomerInfo.JSON_TAG.Telephone, "");

            CustomerInfo c = new CustomerInfo();
            c.setEmail(email);
            c.setUserId(userId);
            c.setFullname(fullname);
            c.setGroupId(groupId);
            c.setCompany(company);
            c.setTelephone(telephone);

            return c;
        }
        return null;
    }

    /**
     * 
     * @return
     */
    public boolean clearCustomerInfo() {
        SharedPreferences.Editor editor = sCtx.getSharedPreferences(CustomerInfo.Prefs.INFO, Context.MODE_PRIVATE).edit();
        if (null != editor) {
            editor.clear();
        }
        return editor.commit();
    }

    @Override
    public void release() {
        clearCustomerInfo();
    }

    @Override
    public boolean registerChecker(JSONObject jsonObject) {
        if (jsonObject == null || getStatus(jsonObject) == CODE.ERROR) {
            return false;
        }
        return true;
    }

    @Override
    public boolean lostPasswordChecker(JSONObject jsonObject) {
        return registerChecker(jsonObject);
    }

    @Override
    public boolean couponChecker(JSONObject jsonObject) {
        return registerChecker(jsonObject);
    }

    @Override
    public String getJSONPrice() {
        return mJSONPrice;
    }

    @Override
    public void setJSONPrice(String price) {
        mJSONPrice = price;
    }
}
