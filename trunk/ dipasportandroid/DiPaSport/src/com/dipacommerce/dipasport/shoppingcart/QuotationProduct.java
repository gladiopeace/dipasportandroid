package com.dipacommerce.dipasport.shoppingcart;

public final class QuotationProduct extends OrderProduct {
    private String comment;

    public QuotationProduct(){
        
    }
    
    public QuotationProduct(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
