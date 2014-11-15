package com.dipacommerce.dipasport.shoppingcart;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.customer.CustomerInfo;
import com.dipacommerce.dipasport.data.Constants;
import com.dipacommerce.dipasport.data.URLFormatter;
import com.dipacommerce.dipasport.network.json.IJSONManager;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;

class CartImp extends ShoppingCartManager implements ICart {

    private static ArrayList<OrderProduct> mOrderProduct;

    private ShoppingCartChanged mCartUpdated;
    private OptionsChanged mOptionsChanged;
    
    private IJSONManager.Callback<JSONObject> mCallbackOrder;
    private IJSONManager.Callback<JSONObject> mCallbackDiscount;

    private int mDiscountAmount = 0;
    private static String mDiscountCode = "";

    public CartImp() {
        mOrderProduct = new ArrayList<OrderProduct>();
    }

    @Override
    public void add(OrderProduct orderProduct) {
        sDatabaseHandler.addProduct(orderProduct, sCustomer.getCustomerInfo().getUserId());
    }

    @Override
    public void remove(OrderProduct orderProduct) {
        sDatabaseHandler.deleteProduct(orderProduct, sCustomer.getCustomerInfo().getUserId());
    }

    @Override
    public void remove(String productCode) {
        sDatabaseHandler.deleteProduct(productCode, sCustomer.getCustomerInfo().getUserId());
    }

    @Override
    public void removeAll() {
        sDatabaseHandler.deleteAllProduct(sCustomer.getCustomerInfo().getUserId());
    }

    @Override
    public void update(String productCode, int quantity, String customerId) {
        sDatabaseHandler.updateProduct(productCode, quantity, customerId);
    }

    @Override
    public long getTotalWithoutTax() {
        long price = 0;
        if (!sCustomer.isLogin()) {
            return price;
        }
        mOrderProduct = sDatabaseHandler.getAllProducts(sCustomer.getCustomerInfo().getUserId());
        for (int i = 0; i < mOrderProduct.size(); i++) {
            price += mOrderProduct.get(i).getProductInfo().getPriceNumber() * mOrderProduct.get(i).getQuantity();
        }
        return price;
    }

    @Override
    public String getTotalWithoutTaxFormatter() {
        String formatter = sCtx.getString(R.string.euro) + " " + getTotalWithoutTax() + Constants.DOUBLE_ZERO;
        return formatter;
    }

    @Override
    public float getTotalWithTax() {
        return getTotalWithoutTax() + getTax() - getDiscount(); // added 22% of
                                                                // total price
    }

    @Override
    public String getTotalWithTaxFormatter() {
        String formatter = sCtx.getString(R.string.euro) + " " + getTotalWithTax();
        return formatter;
    }

    @Override
    public float getTax() {
        float tax = (DiPaSport.getLanguage() == DiPaSport.LANGUAGE_UK) ? 0f : (Constants.TAX * (getTotalWithoutTax() - getDiscount())) / (float) 100;
        return tax; // 22% of total price
    }

    @Override
    public String getTaxFormatter() {
        String formatter = sCtx.getString(R.string.euro) + " " + getTax();
        return formatter;
    }

    @Override
    public boolean isExist(String productId) {
        mOrderProduct = sDatabaseHandler.getAllProducts(sCustomer.getCustomerInfo().getUserId());
        for (int i = 0; i < mOrderProduct.size(); i++) {
            String id = mOrderProduct.get(i).getProductInfo().getCode();
            if (id.equals(productId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ArrayList<OrderProduct> getItems() {
        if (sCustomer.isLogin()) {
            mOrderProduct = sDatabaseHandler.getAllProducts(sCustomer.getCustomerInfo().getUserId());
        } else {
            mOrderProduct.clear();
        }
        return mOrderProduct;
    }

    @Override
    public ShoppingCartChanged getCartUpdated() {
        return mCartUpdated;
    }
    
    @Override
    public OptionsChanged getOptionsChanged() {
        return mOptionsChanged;
    }

    @Override
    public void changeQuantity(OrderProduct orderProduct, int quantity) {

    }

    @Override
    public void registerCartUpdated(ShoppingCartChanged cartUpdated) {
        mCartUpdated = cartUpdated;
    }
    
    @Override
    public void registerOptionsChanged(OptionsChanged optionsChanged) {
        mOptionsChanged = optionsChanged;
    }

    @Override
    public void registerCallbackOrderItem(Callback<JSONObject> callback) {
        mCallbackOrder = callback;
    }

    @Override
    public void order(CustomerInfo c, List<OrderProduct> list) {
        if (mCallbackOrder != null) {
            sJSONManager.registerCallback(mCallbackOrder);
        }
        String url = URLFormatter.buildUrlOrder(c, list);
        if (mDiscountCode.length() > 0) {
            url = String.format("%s%s%s", url, Constants.Query.COUPON, mDiscountCode);
            Log.i("URLFormatter", url);
        }
        
        if(DiPaSport.getLanguage() == DiPaSport.LANGUAGE_UK){
            url += "&lang=en";
        }

        sJSONManager.setURL(url);
        sJSONManager.execute();
    }

    @Override
    public String getOrderId(JSONObject json) {
        if (json != null) {
            try {

                if (getStatus(json) == CODE.OK) {
                    String orderId = json.getString(ICart.ORDER_STATUS.OrderId);
                    return orderId;
                }
            } catch (JSONException e) {
                if (Constants.DEBUG_MODE) {
                    e.printStackTrace();
                }
                return "";
            }
        }
        return "";
    }

    @Override
    public void registerCallbackDiscount(Callback<JSONObject> callback) {
        mCallbackDiscount = callback;
    }

    @Override
    public void discountChecking(String discountCode) {
        if (mCallbackDiscount != null) {
            sJSONManager.registerCallback(mCallbackDiscount);
        }

        sJSONManager.setURL(URLFormatter.buildUrlCoupon(discountCode));
        sJSONManager.execute();
    }

    @Override
    public boolean validateCouponCode(JSONObject json) {
        JSONObject j;
        try {
            j = json.getJSONObject("data");
            if (j != null) {
                String from_date = j.getString(ICart.ORDER_STATUS.FromDate);
                String to_date = j.getString(ICart.ORDER_STATUS.ToDate);
                Date s = parseStringToDate(from_date);
                Date e = parseStringToDate(to_date);
                Date current = new Date(System.currentTimeMillis());
                return current.after(s) && current.before(e);
            }
        } catch (JSONException e) {
            if (Constants.DEBUG_MODE) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean isUsed(JSONObject json) {
        String j;
        try {
            j = json.getString(ICart.ORDER_STATUS.TimesUsed);
            int i = Integer.parseInt(j);
            if (i > 0) {
                return true;
            }
        } catch (JSONException e) {
            if (Constants.DEBUG_MODE) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private Date parseStringToDate(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        try {
            date = dateFormat.parse(str);
        } catch (ParseException e) {
            if (Constants.DEBUG_MODE) {
                e.printStackTrace();
            }
            return null;
        }
        return date;
    }

    @Override
    public int getDiscount() {
        return mDiscountAmount;
    }

    @Override
    public void setDiscount(int discount) {
        mDiscountAmount = discount;
    }

    @Override
    public String getDiscountWithFormatter() {
        String formatter = "-" + sCtx.getString(R.string.euro) + " " + getDiscount();
        return formatter;
    }

    @Override
    public int getDiscountAmount(JSONObject json) {
        JSONObject j;
        try {
            j = json.getJSONObject("data");
            if (j != null) {
                String str = j.getString(ICart.ORDER_STATUS.DiscountAmount);
                if (str != null) {
                    String[] tmp = str.split("\\.");
                    if (tmp.length > 0) {
                        mDiscountAmount = Integer.parseInt(tmp[0]);
                    }
                    return mDiscountAmount;
                }
            }
        } catch (JSONException e) {
            if (Constants.DEBUG_MODE) {
                e.printStackTrace();
            }
        }
        mDiscountAmount = 0;
        return mDiscountAmount;
    }

    @Override
    public void setDiscountCode(String discount) {
        mDiscountCode = discount;
    }

    // QUOTATION SECTION

}
