package com.dipacommerce.dipasport.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import com.dipacommerce.dipasport.customer.CustomerInfo;
import com.dipacommerce.dipasport.shoppingcart.OrderProduct;
import com.dipacommerce.dipasport.shoppingcart.QuotationProduct;

public abstract class DatabaseHandler extends SQLiteOpenHelper
        implements IDatabaseHelper, IShoppingCartHandler, IQuotationHandler, IBarcodeHistory {

    /**
     * Database version
     */
    protected static final int DATABASE_VERSION = 1;

    /**
     * Database name
     */
    protected static final String DATABASE_NAME = "DiPaSportCart";

    /**
     * Database table name
     */
    protected final static String TABLE_QUOTATION = "quotation"; // default
    protected final static String TABLE_ORDER = "product"; // default
    protected final static String TABLE_EMAIL = "email"; // default
    protected final static String TABLE_BARCODE_HISTORY = "barcode_history";

    private static DatabaseHandler sDatabaseHandler;

    public static DatabaseHandler getInstance(Context context) {
        if (sDatabaseHandler == null) {
            sDatabaseHandler = new DatabaseImp(context);
        }
        return sDatabaseHandler;
    }

    protected DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // SHOPPING CART
    @Override
    public void addEmail(CustomerInfo customer) {

    }

    @Override
    public void addProduct(OrderProduct product, String customerId) {

    }

    @Override
    public void deleteAllProduct(String customerId) {

    }

    @Override
    public void deleteAllEmail() {

    }

    @Override
    public void deleteEmail(CustomerInfo customer) {

    }

    @Override
    public void deleteProduct(OrderProduct product, String customerId) {

    }

    @Override
    public void deleteProduct(String productCode, String customerId) {

    }

    @Override
    public ArrayList<OrderProduct> getAllProducts(String customerId) {
        return null;
    }

    @Override
    public ArrayList<CustomerInfo> getEmails() {
        return null;
    }

    @Override
    public OrderProduct getProduct(String productId, String customerId) {
        return null;
    }

    @Override
    public int getProductCount() {
        return 0;
    }

    @Override
    public boolean isEmailExist(CustomerInfo customer) {
        return false;
    }

    // QUOTATION
    @Override
    public void addQuotation(QuotationProduct product, String customerId) {

    }

    @Override
    public void deleteQuotation(QuotationProduct product, String customerId) {

    }

    @Override
    public void deleteQuotation(String productCode, String customerId) {

    }

    @Override
    public ArrayList<QuotationProduct> getAllQuotation(String customerId) {
        return null;
    }

    public ArrayList<QuotationProduct> getAllQuotation(String customerId, String productId) {
        return null;
    }

    @Override
    public QuotationProduct getQuotation(String productId, String customerId) {
        return null;
    }

    @Override
    public int getQuotationCount() {
        return 0;
    }

    @Override
    public int updateProduct(OrderProduct product, String customerId) {
        return 0;
    }

    @Override
    public int updateProduct(String productCode, int quantity, String customerId) {
        return 0;
    }

    @Override
    public int updateQuotation(QuotationProduct product, String customerId) {
        return 0;
    }

    @Override
    public int updateQuotation(String productCode, int quantity, String customerId) {
        return 0;
    }

    @Override
    public void deleteAllQuotation(String customerId) {

    }

    // BARCODE HISTORY

    @Override
    public void addHistory(Bundle history, String customerId) {

    }

    @Override
    public Bundle getHistory(String code, String customerId) {
        return null;
    }

    @Override
    public ArrayList<Bundle> getHistories(String customerId) {
        return null;
    }

    @Override
    public void removeHistory(Bundle bundle, String customerId) {

    }

    @Override
    public void removeAll(String customerId) {

    }

    @Override
    public void removeHistory(String customerId) {

    }

    @Override
    public int updateHistory(Bundle history, String customerId) {
        return -1;
    }

}
