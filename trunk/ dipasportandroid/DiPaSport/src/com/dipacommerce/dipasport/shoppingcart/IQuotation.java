package com.dipacommerce.dipasport.shoppingcart;

import java.util.List;

import org.json.JSONObject;

import com.dipacommerce.dipasport.customer.CustomerInfo;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;

interface IQuotation extends IShop<QuotationProduct> {

    public void update(QuotationProduct q, String customerId);

    public void quote(CustomerInfo c, List<QuotationProduct> list);
    
    public void registerCallbackQuote(Callback<JSONObject> callback);

}
