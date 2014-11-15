package com.dipacommerce.dipasport.views;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.customer.CustomerGroup;
import com.dipacommerce.dipasport.data.Constants;
import com.dipacommerce.dipasport.network.imagedownloader.ImageLoader;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;
import com.dipacommerce.dipasport.products.IProduct;
import com.dipacommerce.dipasport.products.ProductInfo;
import com.dipacommerce.dipasport.shoppingcart.AddToCartButton;
import com.dipacommerce.dipasport.shoppingcart.AddToQuotationButton;
import com.dipacommerce.dipasport.shoppingcart.ShopImp.OptionsChanged;

public class FragmentProductViews extends DiPaSport<Object, Object> implements OptionsChanged {

    private ImageLoader<ImageView> mImageLoader;
    private View mRootView;
    private String mProductCode = "";
    private String mProductName = "";
    private int mTabId = 0;
    private boolean mQRCode;

    private ProductInfo mCurrentProductInfo;

    public FragmentProductViews() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mImageLoader = new ImageLoader<ImageView>(sCtx);

        UpdateProgressDialogContent();
        BuildActionBar();

        sShoppingCart.registerOptionsChanged(this);
    }

    @Override
    public void UpdateProgressDialogContent() {
        sLoading.setMessage(sCtx.getString(R.string.str_loading));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_product_detail_page, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mProductCode = bundle.getString(IProduct.BUNDLE.PRODUCT_ID);
            mProductName = bundle.getString(IProduct.BUNDLE.PRODUCT_NAME);
            mTabId = bundle.getInt("tabid");
            mQRCode = bundle.getBoolean("QRCode");

        }

        if (CaptureActivity.mProductInfo != null) {
            UpdateViews(mRootView, CaptureActivity.mProductInfo);
            CaptureActivity.mProductInfo = null;
        } else {
            sLoading.showDialog();

            sProduct.registerCallbackProductDetaill(mCallbackProductDetail);
            sProduct.fetch(mProductCode);
        }
        return mRootView;
    }

    private Callback<JSONObject> mCallbackProductDetail = new Callback<JSONObject>() {

        @Override
        public void onResults(JSONObject results) {
            if (results == null) {
                Toast.makeText(sCtx, sCtx.getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                return;
            }

            mCurrentProductInfo = sProduct.convertJSONProductDetail(results);
            mCurrentProductInfo.setRawJSON(results);
            if (mCurrentProductInfo == null) {
                return;
            }

            mCurrentProductInfo.setEntiryID(mProductCode);
            if (mProductName != null && mProductName.length() > 0) {
                mCurrentProductInfo.setName(mProductName);
            }
            
            UpdateViews(mRootView, mCurrentProductInfo);
        }

        @Override
        public void onErrors(int _errorCode, String _errorMessage) {
            Toast.makeText(sCtx, sCtx.getString(R.string.str_server_not_response), Toast.LENGTH_SHORT).show();
            back();
        }

        @Override
        public void onDone() {
            sLoading.closeDialog();
        }
    };

    @Override
    public void UpdateViews(final View rootView, final ProductInfo productInfo) {
        if (!sCustomer.isLogin()) {
            showAlertNotLogin();
        }
        mCurrentProductInfo = productInfo;
        // product image
        ImageView productImg = (ImageView) rootView.findViewById(R.id.product_detail_image);
        mImageLoader.DisplayImage(productInfo.getImagePath(), 0, productImg, Constants.PRODUCT_DETAIL_WIDTH, Constants.PRODUCT_DETAIL_WIDTH);

        productImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent fullscreen = new Intent(sCtx, FullScreenImageSlideActivity.class);
                fullscreen.putStringArrayListExtra(IProduct.BUNDLE.PRODUCT_IMAGE_PATH, productInfo.getMedia_image());
                fullscreen.putExtra(IProduct.BUNDLE.PRODUCT_NAME, productInfo.getName());
                startActivity(fullscreen);
            }
        });

        // product name
        TextView productName = (TextView) rootView.findViewById(R.id.product_detail_name);
        productName.setText(productInfo.getName());

        // product code (sku)
        TextView productCode = (TextView) rootView.findViewById(R.id.product_detail_code);
        productCode.setText(productInfo.getCodeWithFormatter());

        // product price
        TextView productPrice = (TextView) rootView.findViewById(R.id.product_detail_price);
        String priceHtml = "";
        if (sCustomer.isLogin()) {
            int groupId = sCustomer.getCustomerInfo().getGroupId(); 
            if (groupId == CustomerGroup.NOPREZZI) {
                priceHtml = getString(R.string.str_detail_price_unavailable);
            } else if (sCustomer.getCustomerInfo().getGroupId() == CustomerGroup.BANNATI) {
                priceHtml = getString(R.string.str_customer_banned);
            } else {
                priceHtml = productInfo.getPriceWithFormatter();
                
                if(groupId == CustomerGroup.RICAMBISTA_A){ // (GROUP ID 13) add suggest price for garage shop
                    try {
                        String suggestPrice = mCurrentProductInfo.getRawJSON().getJSONObject(IProduct.JSON_TAG.DATA).getString(IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.OFFICINA);
                        mCurrentProductInfo.addSuggestPrice(String.format(Locale.getDefault(), "<font color='black' size='12'>%s%.2f</font>", 
                                sCtx.getString(R.string.str_price_garage_shop), Double.parseDouble(suggestPrice)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(groupId == CustomerGroup.GROSSISTA_A){ // (GROUP ID 14)  add suggest price for garage shop, retailer
                    try {
                        String suggestPrice = mCurrentProductInfo.getRawJSON().getJSONObject(IProduct.JSON_TAG.DATA).getString(IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.OFFICINA);
                        mCurrentProductInfo.addSuggestPrice(String.format(Locale.getDefault(), "<font color='black' size='12'>%s%.2f</font>", 
                                sCtx.getString(R.string.str_price_garage_shop), Double.parseDouble(suggestPrice)));
                        suggestPrice = mCurrentProductInfo.getRawJSON().getJSONObject(IProduct.JSON_TAG.DATA).getString(IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.RICAMBISTA_A);
                        mCurrentProductInfo.addSuggestPrice(String.format(Locale.getDefault(), "<font color='black' size='12'>%s%.2f</font>", 
                                sCtx.getString(R.string.str_price_retailer), Double.parseDouble(suggestPrice)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(groupId == CustomerGroup.AMMINISTRATORE){ //  (GROUP ID 15) add suggest price for administrator
                    priceHtml = "";
                    try {
                        // Officina
                        String suggestPrice = mCurrentProductInfo.getRawJSON().getJSONObject(IProduct.JSON_TAG.DATA).getString(IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.OFFICINA);
                        if(!TextUtils.isEmpty(suggestPrice)){
                            mCurrentProductInfo.addSuggestPrice(String.format(Locale.getDefault(), "<font color='black' size='12'>- %s%.2f</font>", 
                                sCtx.getString(R.string.str_price_garage_shop), Double.parseDouble(suggestPrice)));
                        }
                        
                        // Ricambista+A
                        suggestPrice = mCurrentProductInfo.getRawJSON().getJSONObject(IProduct.JSON_TAG.DATA).getString(IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.RICAMBISTA_A);
                        if(!TextUtils.isEmpty(suggestPrice)){
                            mCurrentProductInfo.addSuggestPrice(String.format(Locale.getDefault(), "<font color='black' size='12'>- %s%.2f</font>", 
                                sCtx.getString(R.string.str_price_retailer), Double.parseDouble(suggestPrice)));
                        }
                        
                        // Grossista+A
                        suggestPrice = mCurrentProductInfo.getRawJSON().getJSONObject(IProduct.JSON_TAG.DATA).getString(IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.GROSSISTA_A);
                        if(!TextUtils.isEmpty(suggestPrice)){
                            mCurrentProductInfo.addSuggestPrice(String.format(Locale.getDefault(), "<font color='black' size='12'>- %s%.2f</font>", 
                                sCtx.getString(R.string.str_price_distributor), Double.parseDouble(suggestPrice)));
                        }
                        
                        // Purchase price
                        suggestPrice = mCurrentProductInfo.getRawJSON().getJSONObject(IProduct.JSON_TAG.DATA).getString(IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.PURCHASE_PRICE);
                        if(!TextUtils.isEmpty(suggestPrice)){
                            mCurrentProductInfo.addSuggestPrice(String.format(Locale.getDefault(), "<font color='black' size='12'>- %s%.2f</font>", 
                                sCtx.getString(R.string.str_purchase_price), Double.parseDouble(suggestPrice)));
                        }
                        
                        // Competitive prices
                        suggestPrice = mCurrentProductInfo.getRawJSON().getJSONObject(IProduct.JSON_TAG.DATA).getString(IProduct.JSON_PRODUCT_DETAIL_TAG.PRICE.COMPETITIVE_PRICE);
                        if(!TextUtils.isEmpty(suggestPrice)){
                            suggestPrice = suggestPrice.replace("\r\n", "<br/>- ");
                            mCurrentProductInfo.addSuggestPrice(String.format(Locale.getDefault(), "<font color='black' size='12'>%s<br/>- %s</font>", 
                                sCtx.getString(R.string.str_competitive_price), suggestPrice));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            productPrice.setVisibility(View.VISIBLE);
        } else {
            productPrice.setVisibility(View.GONE);
        }
        
        String suggest_price_garage_shop = mCurrentProductInfo.getSuggestPrice();
        if(suggest_price_garage_shop != null){
            priceHtml += suggest_price_garage_shop;
        }
        productPrice.setText(Html.fromHtml(priceHtml));
        mCurrentProductInfo.clearSuggestPrice();
        // Code OEM
        TextView codeOem = (TextView) rootView.findViewById(R.id.product_detail_codeoem);
        codeOem.setText(productInfo.getCode_oem_withFormatter());

        // Part condition
        TextView partCondition = (TextView) rootView.findViewById(R.id.product_detail_part_condition);
        partCondition.setText(productInfo.getPartConditionWithFormatter());

        // Availability
        TextView productAvailability = (TextView) rootView.findViewById(R.id.product_detail_availability);
        productAvailability.setText(productInfo.getAvailabilityWithFormatter());

        // Quick overview
        TextView productQuickoverview = (TextView) rootView.findViewById(R.id.product_detail_quickoverview);
        productQuickoverview.setText(productInfo.getQuickoverviewWithFormatter());

        // Suitable for
        TextView productSuitableFor = (TextView) rootView.findViewById(R.id.product_detail_suitablefor);
        productSuitableFor.setText(productInfo.getSuitableforWithFormatter());

        // Add to cart
        AddToCartButton addToCart = (AddToCartButton) rootView.findViewById(R.id.product_detail_addtocart);
        addToCart.setProduct(mCurrentProductInfo);
        addToCart.update();

        // Add to quote
        AddToQuotationButton addToQuote = (AddToQuotationButton) rootView.findViewById(R.id.product_detail_addtoquote);
        addToQuote.setProduct(mCurrentProductInfo);
    }

    @Override
    protected void BuildActionBar() {
        if (sCtx != null) {
            Activity activity = ((Activity) sCtx);
            View mCustomTitle = activity.getLayoutInflater().inflate(R.layout.ctrl_common_title, null);

            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                ((TextView) mCustomTitle.findViewById(R.id.title_name)).setText("");

                ImageView logo = (ImageView) mCustomTitle.findViewById(R.id.logo);
                if (logo != null) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                    logo.setLayoutParams(params);
                }

                TextView back = (TextView) mCustomTitle.findViewById(R.id.back);
                if (back != null) {
                    back.setVisibility(View.VISIBLE);
                    back.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            back();
                        }
                    });
                }

                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setCustomView(mCustomTitle);

            }
        }
    }

    private void back() {
        FragmentManager fm = getFragmentManager();
        if (mTabId == TabsFragment.TAB_HOME_INDEX) {
            if (mQRCode) {
                Bundle params = new Bundle();
                params.putBoolean("QR", true);
                sHomeFragment.setArguments(params);
                mQRCode = false;
                Intent captureProduct = new Intent(sCtx, CaptureActivity.class);
                startActivityForResult(captureProduct, SEARCH_METHODS);
            }
            fm.beginTransaction().replace(R.id.tab_home, sHomeFragment).commit();
        } else if (mTabId == TabsFragment.TAB_SEARCH_INDEX) {
            fm.beginTransaction().replace(R.id.tab_search, sSearchFragment).commit();
        } else if (mTabId == TabsFragment.TAB_ACCOUNT_INDEX) {
            fm.beginTransaction().replace(R.id.tab_home, sSearchByCategory).commit();
        }
    }

    @Override
    public void UpdateTabChanged(String tabId) {
        BuildActionBar();
        UpdateProgressDialogContent();
    }

    /**
     * 
     */
    private void showAlertNotLogin() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(sCtx);
        dialog.setMessage(sCtx.getString(R.string.str_alert_login_register));
        dialog.setPositiveButton(sCtx.getString(R.string.str_dialog_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // add something code here
                TabsFragment.setTabSelected(TabsFragment.TAB_ACCOUNT_INDEX);
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(sCtx.getString(R.string.str_dialog_cancel), null);
        dialog.create().show();
    }

    @Override
    public void onContinueShopping() {

    }

    @Override
    public void onGoToShoppingCart() {
        TabsFragment.setTabSelected(TabsFragment.TAB_SHOPPINGCART_INDEX);
    }
}
