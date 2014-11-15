package com.dipacommerce.dipasport.shoppingcart;

import java.util.List;

import org.json.JSONObject;

import com.dipacommerce.dipasport.IDiPaSport;
import com.dipacommerce.dipasport.customer.CustomerInfo;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;
import com.dipacommerce.dipasport.products.ProductInfo;

public interface ICart extends IShop<OrderProduct> {

    public interface ORDER_STATUS extends IDiPaSport.JSON_STATUS_TAG {
        public static final String StatusOrder = "StatusOrder";
        public static final String OrderId = "IdOrde";
        public static final String DiscountAmount = "discount_amount";
        public static final String FromDate = "from_date";
        public static final String ToDate = "to_date";
        public static final String TimesUsed = "apply";
    }
    
    public void changeQuantity(OrderProduct orderProduct, int quantity);

    public long getTotalWithoutTax();

    public String getTaxFormatter();

    public float getTax();

    public String getTotalWithTaxFormatter();

    public float getTotalWithTax();

    public String getTotalWithoutTaxFormatter();


    public int chooseQuantityAdd(ProductInfo product, int quantity);

    public int chooseQuantityUpdate(String productCode, int quantity);

    public void removeProductInterface(String productCode);

    public int getDiscount();

    public void setDiscount(int discount);

    public void setDiscountCode(String discount);

    public String getDiscountWithFormatter();

    public int getDiscountAmount(JSONObject jsonObj);

    // Order
    public void registerCallbackOrderItem(Callback<JSONObject> callback);

    public void order(CustomerInfo c, List<OrderProduct> list);

    // Discount
    public void registerCallbackDiscount(Callback<JSONObject> callback);

    public void discountChecking(String discountCode);

}
