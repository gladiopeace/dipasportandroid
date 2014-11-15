package com.dipacommerce.dipasport.shoppingcart;

import org.json.JSONObject;

public abstract class ShoppingCartManager extends ShopImp<OrderProduct> implements ICart {

    public abstract String getOrderId(JSONObject json);

    public abstract boolean validateCouponCode(JSONObject json);
    
    public abstract boolean isUsed(JSONObject jsonObj);

}
