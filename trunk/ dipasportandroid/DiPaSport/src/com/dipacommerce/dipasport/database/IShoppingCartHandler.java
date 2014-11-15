package com.dipacommerce.dipasport.database;

import java.util.ArrayList;

import com.dipacommerce.dipasport.shoppingcart.OrderProduct;

public interface IShoppingCartHandler {
 // ====================================================
    // TABLE ORDER PRODUCT

    /**
     * Add new a product to Database
     * 
     * @param product
     * @param customerId
     */
    public void addProduct(OrderProduct product, String customerId);

    /**
     * Get a product from table matching with customer id (email)
     * 
     * @param productId
     * @param customerId
     * @return
     */
    public OrderProduct getProduct(String productId, String customerId);

    /**
     * Get all products from table matching with customer id (email)
     * 
     * @param customerId
     * @return
     */
    public ArrayList<OrderProduct> getAllProducts(String customerId);

    /**
     * Count all products in the table
     * 
     * @return
     */
    public int getProductCount();

    /**
     * Update the quantity of product
     * 
     * @param product
     * @param customerId
     * @return
     */
    public int updateProduct(OrderProduct product, String customerId);

    /**
     * Update the quantity of product
     * 
     * @param productCode
     * @param quantity
     * @param customerId
     * @return
     */
    public int updateProduct(String productCode, int quantity, String customerId);

    /**
     * Delete a product from table
     * 
     * @param product
     * @param customerId
     */
    public void deleteProduct(OrderProduct product, String customerId);

    /**
     * Delete a product from table
     * 
     * @param productCode
     * @param customerId
     */
    public void deleteProduct(String productCode, String customerId);

    /**
     * Delete all products of customer when order finished
     * 
     * @param customerId
     */
    public void deleteAllProduct(String customerId);
}
