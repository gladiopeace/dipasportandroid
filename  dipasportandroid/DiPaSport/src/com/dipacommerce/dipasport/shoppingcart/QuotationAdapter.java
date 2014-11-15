package com.dipacommerce.dipasport.shoppingcart;

import java.util.ArrayList;

import android.content.Context;

public class QuotationAdapter<T> extends ShoppingCartAdapter<T> {

    @SuppressWarnings("unchecked")
    public QuotationAdapter(Context context, int layoutID, ArrayList<T> dataSource) {
        super(context, layoutID, dataSource);
        mShoppingCart = (IShop<T>) Quotation.getInstance();
    }

}
