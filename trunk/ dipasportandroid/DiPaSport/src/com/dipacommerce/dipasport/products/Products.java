package com.dipacommerce.dipasport.products;


public abstract class Products {
    private static ProductManager sProduct;

    public static ProductManager getInstance() {
        if (sProduct == null) {
            sProduct = new ProductImp();
        }
        return sProduct;
    }
}
