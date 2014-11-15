package com.dipacommerce.dipasport.shoppingcart;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.dipacommerce.dipasport.customer.CustomerInfo;
import com.dipacommerce.dipasport.data.URLFormatter;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;

class QuotationImp extends QuotationManager {

    private static ArrayList<QuotationProduct> mQuotationProduct;
    private ShoppingCartChanged mQuotationChanged;
    private OptionsChanged mOptionsChanged;
    private Callback<JSONObject> mCallback;

    public QuotationImp() {
        mQuotationProduct = new ArrayList<QuotationProduct>();
    }

    @Override
    public void add(QuotationProduct quotationProduct) {
        sDatabaseHandler.addQuotation(quotationProduct, sCustomer.getCustomerInfo().getUserId());
    }

    @Override
    public void remove(QuotationProduct quotationProduct) {
        sDatabaseHandler.deleteQuotation(quotationProduct, sCustomer.getCustomerInfo().getUserId());
    }

    @Override
    public void remove(String productCode) {
        sDatabaseHandler.deleteQuotation(productCode, sCustomer.getCustomerInfo().getUserId());
    }

    @Override
    public void removeAll() {
        sDatabaseHandler.deleteAllQuotation(sCustomer.getCustomerInfo().getUserId());
    }

    @Override
    public void update(String productCode, int quantity, String customerId) {
        sDatabaseHandler.updateQuotation(productCode, quantity, customerId);
    }

    @Override
    public void update(QuotationProduct q, String customerId) {
        sDatabaseHandler.updateQuotation(q, customerId);
    }

    @Override
    public boolean isExist(String productId) {
        mQuotationProduct = sDatabaseHandler.getAllQuotation(sCustomer.getCustomerInfo().getUserId());
        for (int i = 0; i < mQuotationProduct.size(); i++) {
            String id = mQuotationProduct.get(i).getProductInfo().getCode();
            if (id.equals(productId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ArrayList<QuotationProduct> getItems() {
        if (sCustomer.isLogin()) {
            mQuotationProduct = sDatabaseHandler.getAllQuotation(sCustomer.getCustomerInfo().getUserId());
        } else {
            mQuotationProduct.clear();
        }
        return mQuotationProduct;
    }

    @Override
    public ArrayList<QuotationProduct> getItem(String productId) {
        ArrayList<QuotationProduct> list = new ArrayList<QuotationProduct>();
        list = sDatabaseHandler.getAllQuotation(sCustomer.getCustomerInfo().getUserId(), productId);
        return list;
    }

    @Override
    public ShoppingCartChanged getCartUpdated() {
        return mQuotationChanged;
    }

    @Override
    public OptionsChanged getOptionsChanged() {
        return mOptionsChanged;
    }

    @Override
    public void registerCartUpdated(ShoppingCartChanged cartUpdated) {
        mQuotationChanged = cartUpdated;
    }

    @Override
    public void registerOptionsChanged(OptionsChanged optionsChanged) {
        mOptionsChanged = optionsChanged;
    }

    @Override
    public void quote(CustomerInfo c, List<QuotationProduct> list) {
        if (mCallback != null) {
            sJSONManager.registerCallback(mCallback);
        }

        sJSONManager.setURL(URLFormatter.buildUrlQuote(c, list));
        sJSONManager.execute();
    }

    @Override
    public void registerCallbackQuote(Callback<JSONObject> callback) {
        mCallback = callback;
    }

}
