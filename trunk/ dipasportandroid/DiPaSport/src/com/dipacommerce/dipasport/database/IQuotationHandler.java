package com.dipacommerce.dipasport.database;

import java.util.ArrayList;

import com.dipacommerce.dipasport.shoppingcart.QuotationProduct;

public interface IQuotationHandler {
    // ====================================================
    // TABLE QUOTATION

    /**
     * Add new a product(quotation) to Database
     * 
     * @param product
     * @param customerId
     */
    public void addQuotation(QuotationProduct product, String customerId);

    /**
     * Get a product(quotation) from table matching with customer id (email)
     * 
     * @param productId
     * @param customerId
     * @return
     */
    public QuotationProduct getQuotation(String productId, String customerId);

    /**
     * Get all products(quotation) from table matching with customer id (email)
     * 
     * @param customerId
     * @return
     */
    public ArrayList<QuotationProduct> getAllQuotation(String customerId);

    /**
     * Count all products(quotation) in the table
     * 
     * @return
     */
    public int getQuotationCount();

    /**
     * Update the quantity of product(quotation)
     * 
     * @param product
     * @param customerId
     * @return
     */
    public int updateQuotation(QuotationProduct product, String customerId);

    /**
     * Update the quantity of product(quotation)
     * 
     * @param productCode
     * @param quantity
     * @param customerId
     * @return
     */
    public int updateQuotation(String productCode, int quantity, String customerId);

    /**
     * Delete a product from table
     * 
     * @param product
     * @param customerId
     */
    public void deleteQuotation(QuotationProduct product, String customerId);

    /**
     * Delete a product from table
     * 
     * @param productCode
     * @param customerId
     */
    public void deleteQuotation(String productCode, String customerId);

    /**
     * Delete all products(quotation) of customer when order finished
     * 
     * @param customerId
     */
    public void deleteAllQuotation(String customerId);

}
