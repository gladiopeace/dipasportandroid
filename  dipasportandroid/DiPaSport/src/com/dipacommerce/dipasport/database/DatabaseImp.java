package com.dipacommerce.dipasport.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import com.dipacommerce.dipasport.customer.CustomerInfo;
import com.dipacommerce.dipasport.products.IProduct;
import com.dipacommerce.dipasport.products.ProductInfo;
import com.dipacommerce.dipasport.shoppingcart.OrderProduct;
import com.dipacommerce.dipasport.shoppingcart.QuotationProduct;

class DatabaseImp extends DatabaseHandler {

    // EMAIL TABLE
    private static final String KEY_EMAILID = "userid";
    private static final String KEY_EMAILNAME = "name";

    // ORDER TABLE, QUOTATION: name(id), codicedipa, price, imagepath, quantity
    private static final String KEY_ID = "name";
    private static final String KEY_EMAILID_FK = "emailid"; // FK
    private static final String KEY_NAME = "codicedipa";
    private static final String KEY_PRICE = "price";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_ENTITYID = "entityid";
    private static final String KEY_QUOTATION_COMMENT = "quotationcomment";

    // BARCODE TABLE
    private static final String KEY_BARCODE_CODE = "code";
    private static final String KEY_BARCODE_NAME = "name";
    private static final String KEY_BARCODE_TIME = "datetime";

    public DatabaseImp(Context context) {
        super(context);
    }

    private String buildCreateTableEmailQuery() {
        String sql = String.format("CREATE TABLE %s (%s TEXT, %s TEXT)", TABLE_EMAIL, KEY_EMAILID, KEY_EMAILNAME);
        return sql;
    }

    /**
     * Create table ORDER
     */
    private String buildCreateTableOrderQuery() {
        String sql = String.format("CREATE TABLE %s (%s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT, %s INTEGER, %s TEXT, %s INTEGER, %s TEXT, PRIMARY KEY(%s, %s))", TABLE_ORDER, KEY_ID, KEY_EMAILID_FK, KEY_NAME, KEY_PRICE, KEY_IMAGE, KEY_QUANTITY, KEY_ENTITYID, KEY_ID, KEY_EMAILID_FK);
        return sql;
    }

    /**
     * Create table Quotation
     */
    private String buildCreateTableQuotationQuery() {
        String sql = String.format("CREATE TABLE %s (%s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT, %s INTEGER, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, PRIMARY KEY(%s, %s))", TABLE_QUOTATION, KEY_ID, KEY_EMAILID_FK, KEY_NAME, KEY_PRICE, KEY_IMAGE, KEY_QUANTITY, KEY_ENTITYID, KEY_QUOTATION_COMMENT, KEY_ID, KEY_EMAILID_FK);
        return sql;
    }

    /**
     * Create table barcode
     */
    private String buildCreateTableBarCodeQuery() {
        String sql = String.format("CREATE TABLE %s ( %s TEXT NOT NULL,  %s TEXT NOT NULL, %s TEXT, %s INTEGER)", TABLE_BARCODE_HISTORY, KEY_EMAILID_FK, KEY_BARCODE_CODE, KEY_BARCODE_NAME, KEY_BARCODE_TIME);
        return sql;
    }

    /**
     * Drop table Email
     */
    private String buildDropTableEmailQuery() {
        String sql = String.format("DROP TABLE IF EXISTS %s", TABLE_EMAIL);
        return sql;
    }

    /**
     * Drop table Order
     */
    private String buildDropTableOrderQuery() {
        String sql = String.format("DROP TABLE IF EXISTS %s", TABLE_ORDER);
        return sql;
    }

    /**
     * Dtop table barcode
     */
    private String buildDropTableBarcodeQuery() {
        String sql = String.format("DROP TABLE IF EXISTS %s", TABLE_BARCODE_HISTORY);
        return sql;
    }

    /**
     * Drop table Quotation
     */
    private String buildDropTableQuotationQuery() {
        String sql = String.format("DROP TABLE IF EXISTS %s", TABLE_QUOTATION);
        return sql;
    }

    /**
     * Delete all rows in the table Email
     * 
     * @param customerId
     * @return
     */
    private String buildDeleteTableEmailQuery(String customerId) {
        String sql = String.format("DELETE FROM %s WHERE %s = %s;", TABLE_EMAIL, KEY_EMAILID, customerId);
        return sql;
    }

    /**
     * Delete all rows in the table Order
     * 
     * @param customerId
     * @return
     */
    private String buildDeleteTableOrderQuery(String customerId) {
        String sql = String.format("DELETE FROM %s WHERE %s = %s;", TABLE_ORDER, KEY_EMAILID_FK, customerId);
        return sql;
    }

    /**
     * Delete all rows in the table Quotation
     * 
     * @param customerId
     * @return
     */
    private String buildDeleteTableQuotationQuery(String customerId) {
        String sql = String.format("DELETE FROM %s WHERE %s = %s;", TABLE_QUOTATION, KEY_EMAILID_FK, customerId);
        return sql;
    }

    private String buildDeleteTableHistoryQuery(String customerId) {
        String sql = String.format("DELETE FROM %s WHERE %s = '%s';", TABLE_BARCODE_HISTORY, KEY_EMAILID_FK, customerId);
        return sql;
    }

    /**
     * Get all rows in the table Email
     * 
     * @param customerId
     * @return
     */
    private String buildSelectEmailQuery(String customerId) {
        String sql = String.format("SELECT * FROM %s WHERE %s = %s", TABLE_EMAIL, KEY_EMAILID, customerId);
        return sql;
    }

    /**
     * Get all rows in the table Order
     * 
     * @param customerId
     * @return
     */
    private String buildSelectOrderQuery(String customerId) {
        String sql = String.format("SELECT * FROM %s WHERE %s = %s ORDER BY ROWID DESC", TABLE_ORDER, KEY_EMAILID_FK, customerId);
        return sql;
    }

    /**
     * Get all rows in the table Quotation
     * 
     * @param customerId
     * @return
     */
    private String buildSelectQuotationQuery(String customerId) {
        String sql = String.format("SELECT * FROM %s WHERE %s = %s ORDER BY ROWID DESC", TABLE_QUOTATION, KEY_EMAILID_FK, customerId);
        return sql;
    }

    private String buildSelectBarcodeHistory() {
        String sql = String.format("SELECT * FROM %s ORDER BY %s DESC", TABLE_BARCODE_HISTORY, KEY_BARCODE_TIME);
        return sql;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EMAIL_TABLE = buildCreateTableEmailQuery();
        String CREATE_ORDER_TABLE = buildCreateTableOrderQuery();
        String CREATE_QUOTATION_TABLE = buildCreateTableQuotationQuery();
        String CREATE_BARCODE_TABLE = buildCreateTableBarCodeQuery();
        db.execSQL(CREATE_EMAIL_TABLE);
        db.execSQL(CREATE_ORDER_TABLE);
        db.execSQL(CREATE_QUOTATION_TABLE);
        db.execSQL(CREATE_BARCODE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_EMAIL_TABLE = buildDropTableEmailQuery();
        String DROP_ORDER_TABLE = buildDropTableOrderQuery();
        String DROP_QUOTATION_TABLE = buildDropTableQuotationQuery();
        String DROP_BARCODE_TABLE = buildDropTableBarcodeQuery();
        db.execSQL(DROP_EMAIL_TABLE);
        db.execSQL(DROP_ORDER_TABLE);
        db.execSQL(DROP_QUOTATION_TABLE);
        db.execSQL(DROP_BARCODE_TABLE);

        onCreate(db);
    }

    @Override
    public void addProduct(OrderProduct product, String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, product.getProductInfo().getCode());
        values.put(KEY_EMAILID_FK, customerId);
        values.put(KEY_NAME, product.getProductInfo().getName());
        values.put(KEY_PRICE, product.getProductInfo().getPriceNumber());
        values.put(KEY_IMAGE, product.getProductInfo().getImagePath());
        values.put(KEY_QUANTITY, product.getQuantity());
        values.put(KEY_ENTITYID, product.getProductInfo().getEntityID());

        db.insert(TABLE_ORDER, null, values);
        db.close();
    }

    @Override
    public OrderProduct getProduct(String productId, String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_ORDER, new String[] { KEY_ID,// 0
                KEY_EMAILID_FK,// 1
                KEY_NAME,// 2
                KEY_PRICE,// 3
                KEY_IMAGE,// 4
                KEY_QUANTITY,// 5
                KEY_ENTITYID // 6
        }, KEY_ID + " = ? AND " + KEY_EMAILID_FK + " = ?", new String[] { productId, customerId }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        OrderProduct order = new OrderProduct();
        ProductInfo product = new ProductInfo(cursor.getString(0), cursor.getString(2), cursor.getLong(3), cursor.getString(4));
        product.setEntiryID(cursor.getString(6));
        int quantity = cursor.getInt(5);
        order.setProductInfo(product);
        order.setQuantity(quantity);

        return order;
    }

    @Override
    public ArrayList<OrderProduct> getAllProducts(String customerId) {
        ArrayList<OrderProduct> orderList = new ArrayList<OrderProduct>();
        String select = buildSelectOrderQuery(customerId);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(select, null);
        } catch (SQLiteException e) {
            return orderList;
        }

        if (cursor.moveToFirst()) {
            do {
                OrderProduct order = new OrderProduct();
                ProductInfo product = new ProductInfo(cursor.getString(0), cursor.getString(2), cursor.getLong(3), cursor.getString(4));
                product.setEntiryID(cursor.getString(6));
                int quantity = cursor.getInt(5);

                order.setProductInfo(product);
                order.setQuantity(quantity);

                orderList.add(order);
            } while (cursor.moveToNext());
        }

        return orderList;
    }

    @Override
    public int updateProduct(OrderProduct product, String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, product.getProductInfo().getCode());
        values.put(KEY_QUANTITY, product.getQuantity());

        int result = db.update(TABLE_ORDER, values, KEY_ID + " = ? AND " + KEY_EMAILID_FK + " = ?", new String[] {
                product.getProductInfo().getCode(), customerId });
        return result;
    }

    @Override
    public int updateProduct(String productCode, int quantity, String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, productCode);
        values.put(KEY_QUANTITY, quantity);

        int result = db.update(TABLE_ORDER, values, KEY_ID + " = ? AND " + KEY_EMAILID_FK + " = ?", new String[] {
                productCode, customerId });
        return result;
    }

    @Override
    public void deleteProduct(OrderProduct product, String customerId) {
        deleteProduct(product, customerId, TABLE_ORDER);
    }

    private void deleteProduct(OrderProduct product, String customerId, String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table, KEY_ID + " = ?", new String[] { product.getProductInfo().getCode() });
        db.close();
    }

    @Override
    public void deleteProduct(String productCode, String customerId) {
        deleteProduct(productCode, customerId, TABLE_ORDER);
    }

    private void deleteProduct(String productCode, String customerId, String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table, KEY_ID + " = ? AND " + KEY_EMAILID_FK + " = ?", new String[] { productCode, customerId });
        db.close();
    }

    @Override
    public void deleteAllProduct(String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(buildDeleteTableOrderQuery(customerId));
    }

    @Override
    public boolean isEmailExist(CustomerInfo customer) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_EMAIL, new String[] { KEY_EMAILID, KEY_EMAILNAME }, KEY_EMAILID + " = ?", new String[] { customer.getUserId() }, null, null, null, null);

        if (cursor != null) {
            return cursor.moveToFirst();
        }
        return false;
    }

    @Override
    public void addEmail(CustomerInfo customer) {
        if (!isEmailExist(customer)) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_EMAILID, customer.getUserId());
            values.put(KEY_EMAILNAME, customer.getEmail());

            db.insert(TABLE_EMAIL, null, values);
            db.close();
        }
    };

    @Deprecated
    @Override
    public void deleteAllEmail() {
    }

    @Override
    public void deleteEmail(CustomerInfo customer) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EMAIL, KEY_EMAILID + " = ?", new String[] { customer.getUserId() });
        db.close();
    }

    @Deprecated
    @Override
    public ArrayList<CustomerInfo> getEmails() {
        return null;
    }

    @Override
    public void addQuotation(QuotationProduct product, String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, product.getProductInfo().getCode());
        values.put(KEY_EMAILID_FK, customerId);
        values.put(KEY_NAME, product.getProductInfo().getName());
        values.put(KEY_PRICE, product.getProductInfo().getPriceNumber());
        values.put(KEY_IMAGE, product.getProductInfo().getImagePath());
        values.put(KEY_QUANTITY, product.getQuantity());
        values.put(KEY_ENTITYID, product.getProductInfo().getEntityID());
        values.put(KEY_QUOTATION_COMMENT, product.getComment());

        db.insert(TABLE_QUOTATION, null, values);
        db.close();
    }

    @Override
    public void deleteQuotation(QuotationProduct product, String customerId) {
        deleteProduct(product, customerId, TABLE_QUOTATION);
    }

    @Override
    public void deleteQuotation(String productCode, String customerId) {
        deleteProduct(productCode, customerId, TABLE_QUOTATION);
    }

    @Override
    public void deleteAllQuotation(String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(buildDeleteTableQuotationQuery(customerId));
    }

    @Override
    public ArrayList<QuotationProduct> getAllQuotation(String customerId) {
        ArrayList<QuotationProduct> quotationList = new ArrayList<QuotationProduct>();
        String select = buildSelectQuotationQuery(customerId);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(select, null);
        } catch (SQLiteException e) {
            return quotationList;
        }

        if (cursor.moveToFirst()) {
            do {
                QuotationProduct order = new QuotationProduct();
                ProductInfo product = new ProductInfo(cursor.getString(0), cursor.getString(2), cursor.getLong(3), cursor.getString(4));
                product.setEntiryID(cursor.getString(6));

                int quantity = cursor.getInt(5);

                order.setProductInfo(product);
                order.setQuantity(quantity);
                order.setComment(cursor.getString(7));

                quotationList.add(order);
            } while (cursor.moveToNext());
        }

        return quotationList;
    }

    @Override
    public ArrayList<QuotationProduct> getAllQuotation(String customerId, String productId) {
        ArrayList<QuotationProduct> list = new ArrayList<QuotationProduct>();
        QuotationProduct p = getQuotation(productId, customerId);
        list.add(p);
        return list;
    }

    @Override
    public QuotationProduct getQuotation(String productId, String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_QUOTATION, new String[] { KEY_ID,// 0
                KEY_EMAILID_FK,// 1
                KEY_NAME,// 2
                KEY_PRICE,// 3
                KEY_IMAGE,// 4
                KEY_QUANTITY,// 5
                KEY_ENTITYID, // 6
                KEY_QUOTATION_COMMENT }, KEY_ENTITYID + " = ? AND " + KEY_EMAILID_FK + " = ?", new String[] {
                productId, customerId }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        QuotationProduct order = new QuotationProduct();
        String code = cursor.getString(0);
        String name = cursor.getString(2);
        long price = cursor.getLong(3);
        String imagePath = cursor.getString(4);
        String entityId = cursor.getString(6);
        int quantity = cursor.getInt(5);
        String comment = cursor.getString(7);

        ProductInfo product = new ProductInfo(code, name, price, imagePath);
        product.setEntiryID(entityId);
        order.setProductInfo(product);
        order.setQuantity(quantity);
        order.setComment(comment);

        return order;
    }

    @Override
    public int updateQuotation(QuotationProduct product, String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, product.getProductInfo().getCode());
        // values.put(KEY_QUANTITY, product.getQuantity());
        values.put(KEY_QUOTATION_COMMENT, product.getComment());

        int result = db.update(TABLE_QUOTATION, values, KEY_ID + " = ? AND " + KEY_EMAILID_FK + " = ?", new String[] {
                product.getProductInfo().getCode(), customerId });
        return result;
    }

    @Override
    public int updateQuotation(String productCode, int quantity, String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, productCode);
        values.put(KEY_QUANTITY, quantity);

        int result = db.update(TABLE_QUOTATION, values, KEY_ID + " = ? AND " + KEY_EMAILID_FK + " = ?", new String[] {
                productCode, customerId });
        return result;
    }

    @Override
    public void addHistory(Bundle history, String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EMAILID_FK, customerId);
        values.put(KEY_BARCODE_CODE, history.getString(IProduct.HISTORY.CODE));
        values.put(KEY_BARCODE_NAME, history.getString(IProduct.HISTORY.NAME));
        values.put(KEY_BARCODE_TIME, history.getLong(IProduct.HISTORY.TIME));

        db.insert(TABLE_BARCODE_HISTORY, null, values);
        db.close();
    }

    @Override
    public void removeHistory(Bundle bundle, String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BARCODE_HISTORY, KEY_EMAILID_FK + " = ?", new String[] { customerId });
        db.close();
    }

    @Override
    public void removeHistory(String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BARCODE_HISTORY, KEY_EMAILID_FK + " = ?", new String[] { customerId });
        db.close();
    }

    @Override
    public void removeAll(String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(buildDeleteTableHistoryQuery(customerId));
    }

    @Override
    public ArrayList<Bundle> getHistories(String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Bundle> orderList = new ArrayList<Bundle>();
        Cursor cursor = db.query(TABLE_BARCODE_HISTORY, new String[] { KEY_EMAILID_FK, // 0
                KEY_BARCODE_CODE,// 1
                KEY_BARCODE_NAME,// 2
                KEY_BARCODE_TIME,// 3
        }, KEY_EMAILID_FK + " = ?", new String[] { customerId }, null, null, KEY_BARCODE_TIME + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                String code = cursor.getString(1);
                String name = cursor.getString(2);
                long time = cursor.getLong(3);
                Bundle bundle = new Bundle();
                bundle.putString(IProduct.HISTORY.CODE, code);
                bundle.putString(IProduct.HISTORY.NAME, name);
                bundle.putLong(IProduct.HISTORY.TIME, time);

                orderList.add(bundle);
            } while (cursor.moveToNext());
        }

        return orderList;
    }

    @Override
    public int updateHistory(Bundle history, String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BARCODE_NAME, history.getString(IProduct.HISTORY.NAME));

        int result = db.update(TABLE_BARCODE_HISTORY, values, KEY_EMAILID_FK + " = ? AND " + KEY_BARCODE_CODE + " = ?", new String[] {
                customerId, history.getString(IProduct.HISTORY.CODE) });
        return result;
    }

}
