package com.dipacommerce.dipasport.customer;

import org.json.JSONObject;

interface ICustomerManager {

    /**
     * 
     */
    public void login();

    /**
     * 
     */
    public void logout();

    /**
     * 
     * @param jsonObject
     * @return
     */
    public boolean loginJSONChecker(final JSONObject jsonObject);

    /**
     * 
     * @param jsonObj
     * @return
     */
    public boolean registerChecker(final JSONObject jsonObject);

    /**
     * 
     * @param jsonObject
     * @return
     */
    public boolean lostPasswordChecker(final JSONObject jsonObject);

    /**
     * 
     * @param jsonObject
     * @return
     */
    public boolean couponChecker(final JSONObject jsonObject);
}
