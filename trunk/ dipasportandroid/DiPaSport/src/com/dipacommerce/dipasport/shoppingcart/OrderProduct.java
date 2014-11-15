package com.dipacommerce.dipasport.shoppingcart;

import com.dipacommerce.dipasport.products.ProductInfo;

public class OrderProduct {

    private ProductInfo mProductInfo;

    private int mQuantity;

    public void setProductInfo(ProductInfo productInfo) {
        mProductInfo = productInfo;
    }

    public ProductInfo getProductInfo() {
        return mProductInfo;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    public int getQuantity() {
        return mQuantity;
    }
}
