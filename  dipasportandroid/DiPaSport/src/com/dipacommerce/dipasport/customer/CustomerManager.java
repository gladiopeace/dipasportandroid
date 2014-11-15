package com.dipacommerce.dipasport.customer;

import org.json.JSONObject;

import android.os.Bundle;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;
import com.dipacommerce.dipasport.products.IProduct;

public abstract class CustomerManager extends DiPaSport<JSONObject, Object> implements ICustomerActions<JSONObject> {

    @Override
    public abstract void login();

    @Override
    public abstract void logout();

    @Override
    public abstract boolean loginJSONChecker(JSONObject jsonObject);

    @Override
    public abstract boolean isLogin();

    @Override
    public abstract boolean isLogout();

    @Override
    public abstract CustomerInfo getCustomerInfo();

    @Override
    public abstract void customerLostPassword(String email);

    @Override
    public abstract void customerLogin(CustomerInfo customerInfo);

    @Override
    public abstract void customerRegister(Bundle customerInfo);

    @Override
    public abstract void registerCallbackLogin(Callback<JSONObject> callback);

    @Override
    public abstract void registerCallbackReg(Callback<JSONObject> callback);

    public abstract void release();

    public String getLoginUserId() {
        return "";
    }

    public String getJSONPrice() {
        return IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.NORMALE;
    }

    public void setJSONPrice(String price) {

    }

    public String getPriceType(JSONObject json) {
        return IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.NORMALE;
    }

}
