package com.dipacommerce.dipasport.shoppingcart;

import java.util.ArrayList;

public abstract class QuotationManager extends ShopImp<QuotationProduct> implements IQuotation {

    @Override
    public void add(QuotationProduct orderProduct) {

    }

    @Override
    public void remove(QuotationProduct orderProduct) {

    }

    @Override
    public void remove(String productCode) {

    }

    @Override
    public void removeAll() {

    }

    @Override
    public boolean isExist(String productId) {
        return false;
    }

    @Override
    public ArrayList<QuotationProduct> getItems() {
        return null;
    }

    @Override
    public ShoppingCartChanged getCartUpdated() {
        return null;
    }

    @Override
    public void registerCartUpdated(ShoppingCartChanged cartUpdated) {
    }

}
