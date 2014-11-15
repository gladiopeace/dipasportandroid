package com.dipacommerce.dipasport.customer;

import android.os.Bundle;

import com.dipacommerce.dipasport.network.json.IJSONManager;

interface ICustomerActions<T> extends ICustomerManager {

    /**
     * 
     * @return
     */
    public boolean isLogin();

    /**
     * 
     * @return
     */
    public boolean isLogout();

    /**
     * 
     * @return
     */
    public CustomerInfo getCustomerInfo();

    /**
     * 
     * @param customerInfo
     */
    public void customerLostPassword(String email);

    /**
     * 
     * @param customerInfo
     */
    public void customerLogin(final CustomerInfo customerInfo);

    /**
     * 
     * @param customerInfo
     */
    public void customerRegister(final Bundle customerInfo);
    
    public void updateCustomerInfo(int groupId);

    // CALLBACK
    /**
     * 
     * @param callback
     */
    public void registerCallbackLogin(final IJSONManager.Callback<T> callback);

    /**
     * 
     * @param callback
     */
    public void registerCallbackReg(final IJSONManager.Callback<T> callback);

    /**
     * 
     * @param callback
     */
    public void registerCallbackLostPassword(final IJSONManager.Callback<T> callback);
}
