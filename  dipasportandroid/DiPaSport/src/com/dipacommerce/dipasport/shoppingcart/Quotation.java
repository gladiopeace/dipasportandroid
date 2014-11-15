package com.dipacommerce.dipasport.shoppingcart;

public final class Quotation {
    private static QuotationManager sQuotation;

    public static QuotationManager getInstance() {
        if (sQuotation == null) {
            sQuotation = new QuotationImp();
        }
        return sQuotation;
    }
}
