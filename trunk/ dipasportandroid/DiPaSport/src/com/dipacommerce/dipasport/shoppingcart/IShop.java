package com.dipacommerce.dipasport.shoppingcart;

import java.util.ArrayList;

import android.content.Context;

import com.dipacommerce.dipasport.products.ProductInfo;
import com.dipacommerce.dipasport.shoppingcart.ShopImp.OptionsChanged;
import com.dipacommerce.dipasport.shoppingcart.ShopImp.ShoppingCartChanged;

interface IShop<T>{
    abstract void add(T orderProduct);

    abstract void remove(T orderProduct);

    abstract void remove(String productCode);

    abstract void removeAll();
    
    abstract boolean isExist(String productId);
    
    abstract ArrayList<T> getItems();
    
    abstract ArrayList<T> getItem(final String productId);
    
    abstract int chooseQuantityAdd(final ProductInfo product, final int quantity);
    
    abstract int chooseQuantityUpdate(final String productCode, final int quantity);
    
    abstract void removeProductInterface(final String productCode);
    
    abstract ShoppingCartChanged getCartUpdated();
    
    abstract OptionsChanged getOptionsChanged();
    
    abstract public void display2Options(Context ctx);
    
    abstract void registerCartUpdated(ShoppingCartChanged cartUpdated);
    
    abstract void registerOptionsChanged(OptionsChanged optionsChanged);
    
    abstract void update(String productCode, int quantity, String customerId);
}
