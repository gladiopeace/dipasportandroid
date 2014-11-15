package com.dipacommerce.dipasport.views;

import java.util.List;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.customer.CustomerInfo;
import com.dipacommerce.dipasport.data.Constants;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;
import com.dipacommerce.dipasport.shoppingcart.OrderProduct;
import com.dipacommerce.dipasport.shoppingcart.ShopImp.ShoppingCartChanged;
import com.dipacommerce.dipasport.shoppingcart.ShoppingCartAdapter;

public class FragmentShoppingCart extends DiPaSport<Object, Object> implements ShoppingCartChanged {

    private static ViewGroup mRootView;
    private static ShoppingCartAdapter<OrderProduct> mAdapter;
    private AlertDialog mDialog;

    public FragmentShoppingCart() {
        BuildActionBar();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        init();
        BuildActionBar();
    }

    private void init() {
        sShoppingCart.registerCartUpdated(this);
        UpdateProgressDialogContent();
    }

    @Override
    public void UpdateProgressDialogContent() {
        sLoading.setMessage(sCtx.getString(R.string.str_please_wait));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_shopping_cart_page, container, false);
        UpdateViews(mRootView);
        return mRootView;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void UpdateViews(View rootView) {

        if (mRootView != null) {

            if (mAdapter == null) {
                mAdapter = new ShoppingCartAdapter<OrderProduct>(sCtx, R.layout.ctrl_product_add_to_cart, sShoppingCart.getItems());
            }

            Button orderComplete = (Button) rootView.findViewById(R.id.shca_complete_order_button);
            if (orderComplete != null) {
                orderComplete.setOnClickListener(OnOrderCompleted);
            }

            Button discount = (Button) rootView.findViewById(R.id.shca_discount_button);
            if (discount != null) {
                discount.setOnClickListener(OnDiscountChecking);
            }

            ListView list = (ListView) rootView.findViewById(R.id.shca_content);
            if (!sCustomer.isLogin()) {
                mAdapter.clearData();
                UpdatePrice(rootView);
                return;
            }

            if (list != null) {
                mAdapter.refreshData(sShoppingCart.getItems());
                list.setAdapter(mAdapter);
            }

            UpdatePrice(rootView);
        }
    }

    @Override
    public void onCartUpdated() {
        UpdatePrice(mRootView);
        mAdapter.refreshData(sShoppingCart.getItems());
    }

    private void UpdatePrice(View rootView) {
        TextView totalWithoutTax = (TextView) rootView.findViewById(R.id.shca_total_without_tax);
        if (totalWithoutTax != null) {
            totalWithoutTax.setText(sShoppingCart.getTotalWithoutTaxFormatter());
        }

        TextView tax = (TextView) rootView.findViewById(R.id.shca_tax);
        if (tax != null) {
            tax.setText(sShoppingCart.getTaxFormatter());
        }

        TextView totalWithTax = (TextView) rootView.findViewById(R.id.shca_total_with_tax);
        if (totalWithTax != null) {
            totalWithTax.setText(sShoppingCart.getTotalWithTaxFormatter());
        }
    }

    @Override
    protected void BuildActionBar() {
        if (sCtx != null) {
            Activity activity = ((Activity) sCtx);
            View mCustomTitle = activity.getLayoutInflater().inflate(R.layout.ctrl_common_title, null);
            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                ((TextView) mCustomTitle.findViewById(R.id.title_name)).setText(sCtx.getString(R.string.str_shopping_cart_title));

                ImageView logo = (ImageView) mCustomTitle.findViewById(R.id.logo);
                if (logo != null) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                    logo.setLayoutParams(params);
                }

                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setCustomView(mCustomTitle);

            }
        }
    }

    @Override
    public void UpdateTabChanged(String tabId) {
        BuildActionBar();
        UpdateViews(mRootView);
        UpdateProgressDialogContent();
    }

    private boolean validateUserInput() {
        if (!sCustomer.isLogin()) {
            Toast.makeText(sCtx, sCtx.getString(R.string.str_order_confirm_to_pay), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (sShoppingCart.getItems().size() == 0) {
            Toast.makeText(sCtx, sCtx.getString(R.string.str_cart_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 
     */
    private OnClickListener OnOrderCompleted = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!validateUserInput()) {
                return;
            }

            String content = String.format("%s: %s", sCtx.getString(R.string.str_order_confirm_content), sShoppingCart.getTotalWithTaxFormatter());
            final AlertDialog.Builder dialog = new AlertDialog.Builder(sCtx);
            dialog.setTitle(sCtx.getString(R.string.str_order_confirm_title));
            dialog.setMessage(content);

            dialog.setPositiveButton(sCtx.getString(R.string.str_dialog_ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sLoading.showDialog();
                    sShoppingCart.registerCallbackOrderItem(mCallbackOrder);
                    CustomerInfo customerInfo = sCustomer.getCustomerInfo();
                    if (customerInfo != null) {
                        List<OrderProduct> orderProducts = sShoppingCart.getItems();
                        sShoppingCart.order(customerInfo, orderProducts);
                    } else {
                        Toast.makeText(sCtx, sCtx.getString(R.string.str_order_fail), Toast.LENGTH_LONG).show();
                    }

                }
            });
            dialog.setNegativeButton(sCtx.getString(R.string.str_dialog_cancel), null);

            dialog.create().show();

        }

    };

    private OnClickListener OnDiscountChecking = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (!validateUserInput()) {
                return;
            }

            final LayoutInflater inflater = (LayoutInflater) sCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = (View) inflater.inflate(R.layout.ctrl_dialog_discount_content, mRootView, false);
            if (view != null) {
                final EditText discountCode = (EditText) view.findViewById(R.id.discound_content);
                final TextView warningLabel = (TextView) view.findViewById(R.id.discount_warning_empty);

                final Button checker = (Button) view.findViewById(R.id.discount_checking_button);
                checker.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        InputMethodManager im = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(discountCode.getWindowToken(), 0);
                        String code = discountCode.getText().toString();
                        if (code.length() == 0) {
                            warningLabel.setVisibility(View.VISIBLE);
                            return;
                        } else {
                            warningLabel.setVisibility(View.INVISIBLE);
                        }

                        sShoppingCart.setDiscountCode(code);
                        sLoading.showDialog();
                        sShoppingCart.registerCallbackDiscount(mCallbackCheckingDiscount);
                        sShoppingCart.discountChecking(code);
                    }
                });

                if (Constants.DEBUG_MODE) {
                    discountCode.setText("BENV014DIPA");
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(sCtx);
                dialog.setView(view);
                mDialog = dialog.create();
                mDialog.show();
            }
        }
    };

    private Callback<JSONObject> mCallbackOrder = new Callback<JSONObject>() {

        @Override
        public void onResults(JSONObject results) {
            if (results == null) {
                Toast.makeText(sCtx, sCtx.getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                return;
            }

            if (getStatus(results) == CODE.OK) {
                String message = sCtx.getString(R.string.str_order_success); // sShoppingCart.getMessage(results).toString();
                Toast.makeText(sCtx, message, Toast.LENGTH_LONG).show();
                sShoppingCart.removeAll(); // remove all in DB
                mAdapter.notifyDataSetChanged();
                UpdatePrice(mRootView); // get data from DB, it conflict

                // reset discount
                sShoppingCart.setDiscount(0);
                sShoppingCart.setDiscountCode("");

                FragmentManager fm = getFragmentManager();
                FragmentOrderInfo o = new FragmentOrderInfo();
                Bundle bundle = new Bundle();

                String orderId = sShoppingCart.getOrderId(results);
                bundle.putString(FragmentOrderInfo.ORDER_ID_NUMBER, orderId);
                o.setArguments(bundle);

                fm.beginTransaction().replace(R.id.tab_shoppingcart, o).commit();
            } else {
                Toast.makeText(sCtx, sCtx.getString(R.string.str_order_fail), Toast.LENGTH_LONG).show();
            }

            if (Constants.DEBUG_MODE) {
                Log.i("SHOPPINGCART", results.toString());
            }
        }

        @Override
        public void onErrors(int _errorCode, String _errorMessage) {
            String err = _errorMessage;
            if (Constants.DEBUG_MODE) {
                Log.e("SHOPPINGCART", err);
            }
            Toast.makeText(sCtx, sCtx.getString(R.string.str_order_fail), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDone() {
            sLoading.closeDialog();
        }
    };

    /**
     * 
     */
    private Callback<JSONObject> mCallbackCheckingDiscount = new Callback<JSONObject>() {

        @Override
        public void onResults(JSONObject results) {
            if (results == null) {
                return;
            }
            if (getStatus(results) == CODE.OK) {
                boolean isValid = sShoppingCart.validateCouponCode(results);
                boolean isUsed = sShoppingCart.isUsed(results);

                if (!isValid) {
                    // coupon code expired
                    Toast.makeText(sCtx, sCtx.getString(R.string.str_discount_expired), Toast.LENGTH_LONG).show();
                    mDialog.dismiss();
                    resetDiscount(mRootView);
                    return;
                }

                if (isUsed) {
                    // coupon code is used
                    Toast.makeText(sCtx, sCtx.getString(R.string.str_coupon_used), Toast.LENGTH_LONG).show();
                    mDialog.dismiss();
                    resetDiscount(mRootView);
                    return;
                }

                sShoppingCart.getDiscountAmount(results);
                displayDiscount(mRootView);
                mDialog.dismiss();
            } else {
                String msg = sCtx.getString(R.string.str_coupon_not_valid);
                Toast.makeText(sCtx, msg, Toast.LENGTH_LONG).show();
                resetDiscount(mRootView);
            }
        }

        @Override
        public void onErrors(int _errorCode, String _errorMessage) {

        }

        @Override
        public void onDone() {
            sLoading.closeDialog();
            
        }
    };

    private void displayDiscount(View rootView) {
        UpdatePrice(rootView);
        TextView discountLabel = (TextView) rootView.findViewById(R.id.shca_discount_label);
        TextView discount = (TextView) rootView.findViewById(R.id.shca_discount);
        discountLabel.setVisibility(View.VISIBLE);
        discount.setVisibility(View.VISIBLE);   

        discount.setText(sShoppingCart.getDiscountWithFormatter());
    }
    
    /**
     * 
     * @param rootView
     */
    private void resetDiscount(View rootView){
        sShoppingCart.setDiscount(0);
        UpdatePrice(rootView);
        TextView discountLabel = (TextView) rootView.findViewById(R.id.shca_discount_label);
        TextView discount = (TextView) rootView.findViewById(R.id.shca_discount);
        discountLabel.setVisibility(View.GONE);
        discount.setVisibility(View.GONE);

        discount.setText(sShoppingCart.getDiscountWithFormatter());
    }
}
