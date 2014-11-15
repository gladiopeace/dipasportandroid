package com.dipacommerce.dipasport.shoppingcart;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.products.IProduct;
import com.dipacommerce.dipasport.products.ProductInfo;

public abstract class ShopImp<T extends OrderProduct> extends DiPaSport<Object, Object> implements IShop<T> {

    public interface ShoppingCartChanged {
        public void onCartUpdated();
    }

    public interface OptionsChanged {
        public void onContinueShopping();

        public void onGoToShoppingCart();
    }

    @Override
    public void add(T orderProduct) {

    }

    @Override
    public void remove(T orderProduct) {

    }

    @Override
    public void remove(String productCode) {

    }

    @Override
    public void removeAll() {

    }

    @Override
    public boolean isExist(String productId) {
        return false;
    }

    @Override
    public ArrayList<T> getItems() {
        return null;
    }

    @Override
    public void update(String productCode, int quantity, String customerId) {

    }

    @Override
    public ArrayList<T> getItem(String productId) {
        return null;
    }

    @Override
    public int chooseQuantityAdd(final ProductInfo product, int quantity) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(sCtx);
        View mQuantity = LayoutInflater.from(sCtx).inflate(R.layout.ctrl_quantity, null, false);

        final NumberPicker mNumberPicker = (NumberPicker) mQuantity.findViewById(R.id.numberPicker1);
        if (mNumberPicker != null) {
            mNumberPicker.setMinValue(IProduct.QUANTITY_MIN); // min value 0
            mNumberPicker.setMaxValue(IProduct.QUANTITY_MAX); // max value 50
            mNumberPicker.setWrapSelectorWheel(false);
        }
        dialog.setTitle(sCtx.getString(R.string.str_quantity_title));
        dialog.setView(mQuantity);
        dialog.setPositiveButton(sCtx.getString(R.string.str_dialog_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // to search results if search by manually
                int mProductQuantity = mNumberPicker.getValue();
                @SuppressWarnings("unchecked")
                T orderProduct = (T) new OrderProduct();
                orderProduct.setProductInfo(product);
                orderProduct.setQuantity(mProductQuantity);
                add(orderProduct);
                Toast.makeText(sCtx, sCtx.getString(R.string.str_addtocart_msg), Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                display2Options(sCtx);
            }
        });
        dialog.setNegativeButton(sCtx.getString(R.string.str_dialog_cancel), null);

        mNumberPicker.setValue(quantity);

        dialog.create().show();
        return -1;
    }

    @Override
    public int chooseQuantityUpdate(final String productCode, int quantity) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(sCtx);
        View mQuantity = LayoutInflater.from(sCtx).inflate(R.layout.ctrl_quantity, null, false);

        final NumberPicker mNumberPicker = (NumberPicker) mQuantity.findViewById(R.id.numberPicker1);
        if (mNumberPicker != null) {
            mNumberPicker.setMinValue(IProduct.QUANTITY_MIN); // min value 0
            mNumberPicker.setMaxValue(IProduct.QUANTITY_MAX); // max value 50
            mNumberPicker.setWrapSelectorWheel(false);
        }
        dialog.setTitle(sCtx.getString(R.string.str_quantity_title));
        dialog.setView(mQuantity);
        dialog.setPositiveButton(sCtx.getString(R.string.str_dialog_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int mProductQuantity = mNumberPicker.getValue();
                update(productCode, mProductQuantity, sCustomer.getCustomerInfo().getUserId());
                if (getCartUpdated() != null) {
                    getCartUpdated().onCartUpdated();
                }
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(sCtx.getString(R.string.str_dialog_cancel), null);

        mNumberPicker.setValue(quantity);

        dialog.create().show();
        return -1;
    }

    @Override
    public void removeProductInterface(final String productCode) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(sCtx);
        dialog.setMessage(sCtx.getString(R.string.str_dialog_confirm_remove_form_cart));
        // OK button
        dialog.setPositiveButton(sCtx.getString(R.string.str_dialog_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (productCode != null) {
                    remove(productCode);

                    // update on the layout
                    if (getCartUpdated() != null) {
                        getCartUpdated().onCartUpdated();
                    }
                }
            }
        });
        // Cancel button
        dialog.setNegativeButton(sCtx.getString(R.string.str_dialog_cancel), null);
        dialog.create().show();
    }

    /**
     * 1. Back to barcode scanner or results search 2. Go to shopping cart
     */
    @Override
    public void display2Options(Context ctx) {
        final String[] items = ctx.getResources().getStringArray(R.array.options);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        dialog.setCancelable(false);
        dialog.setTitle(sCtx.getString(R.string.str_option_title));
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    if (getOptionsChanged() != null) {
                        getOptionsChanged().onContinueShopping();
                    }
                }else if(which == 1){
                    if (getOptionsChanged() != null) {
                        getOptionsChanged().onGoToShoppingCart();
                    }
                }
            }
        });

        dialog.create().show();
    }

}
