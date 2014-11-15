package com.dipacommerce.dipasport.products;

import org.json.JSONObject;

import com.dipacommerce.dipasport.data.URLFormatter;
import com.dipacommerce.dipasport.network.json.JSONManager;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;

class ProductImp extends ProductManager {

    private Callback<JSONObject> mCallbackProductFeature;

    private Callback<JSONObject> mCallbackProductDetail;

    private Callback<JSONObject> mCallbackSearchResult;

    private Callback<JSONObject> mCallbackSearchByCategory;

    public ProductImp() {
        if (sJSONManager == null) {
            sJSONManager = JSONManager.getNewInstance(sCtx);
        }
    }

    @Override
    public void registerCallbackProductsFeature(Callback<JSONObject> callback) {
        mCallbackProductFeature = callback;
    }

    @Override
    public void registerCallbackProductDetaill(Callback<JSONObject> callback) {
        mCallbackProductDetail = callback;
    }

    @Override
    public void registerCallbackSearchResult(Callback<JSONObject> callback) {
        mCallbackSearchResult = callback;
    }

    @Override
    public void registerCallbackSearchByCategory(Callback<JSONObject> callback) {
        mCallbackSearchByCategory = callback;
    }

    @Override
    public void fetchProductFeatures() {
        if (mCallbackProductFeature != null) {
            sJSONManager.registerCallback(mCallbackProductFeature);
        }

        sJSONManager.setURL(URLFormatter.buildUrlProductFeature());
        sJSONManager.execute();
    }

    @Override
    public void fetch(String productCode) {
        if (mCallbackProductDetail != null) {
            sJSONManager.registerCallback(mCallbackProductDetail);
        }
        
        String idUser = sCustomer.getCustomerInfo().getUserId();
        sJSONManager.setURL(URLFormatter.buildUrlProductDetail(productCode, idUser));
        sJSONManager.execute();
    }

    @Override
    @Deprecated
    public void filter(String productCode, String categoryId, final String idUser) {
        if (mCallbackSearchResult != null) {
            sJSONManager.registerCallback(mCallbackSearchResult);
        }

        sJSONManager.setURL(URLFormatter.buildUrlSearchResult(productCode, categoryId, idUser));
        sJSONManager.execute();
    }

    @Override
    public void filter(String productCode, int page, final String idUser) {
        if (mCallbackSearchResult != null) {
            sJSONManager.registerCallback(mCallbackSearchResult);
        }

        sJSONManager.setURL(URLFormatter.buildUrlSearchResult(productCode, page, idUser));
        sJSONManager.execute();
    }

    @Override
    public void filterByCategory(String categoryId, int page, final String idUser) {
        if (mCallbackSearchByCategory != null) {
            sJSONManager.registerCallback(mCallbackSearchByCategory);
        }
        sJSONManager.setURL(URLFormatter.buildUrlProductByCategories(categoryId, page, idUser));
        sJSONManager.execute();
    }

}
