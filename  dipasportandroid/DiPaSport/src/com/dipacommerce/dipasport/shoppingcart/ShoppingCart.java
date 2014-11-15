package com.dipacommerce.dipasport.shoppingcart;

public final class ShoppingCart {
    private static ShoppingCartManager sCart;

    public static ShoppingCartManager getInstance() {
        if (sCart == null) {
            sCart = new CartImp();
        }

        return sCart;
    }
}
