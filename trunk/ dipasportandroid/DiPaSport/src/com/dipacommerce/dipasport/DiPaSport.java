package com.dipacommerce.dipasport;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.dipacommerce.dipasport.IDiPaSport.OnRequestCallback;
import com.dipacommerce.dipasport.categories.Categories;
import com.dipacommerce.dipasport.categories.CategoryManager;
import com.dipacommerce.dipasport.controls.DialogLoading;
import com.dipacommerce.dipasport.controls.DialogCallback.OnLoadingTimeOutEvents;
import com.dipacommerce.dipasport.customer.CustomerInfo;
import com.dipacommerce.dipasport.customer.CustomerManager;
import com.dipacommerce.dipasport.customer.Customers;
import com.dipacommerce.dipasport.data.Constants;
import com.dipacommerce.dipasport.database.DatabaseHandler;
import com.dipacommerce.dipasport.network.json.IJSONManager;
import com.dipacommerce.dipasport.products.ProductInfo;
import com.dipacommerce.dipasport.products.ProductManager;
import com.dipacommerce.dipasport.products.Products;
import com.dipacommerce.dipasport.shoppingcart.Quotation;
import com.dipacommerce.dipasport.shoppingcart.QuotationManager;
import com.dipacommerce.dipasport.shoppingcart.ShoppingCart;
import com.dipacommerce.dipasport.shoppingcart.ShoppingCartManager;
import com.dipacommerce.dipasport.views.FragmentAccount;
import com.dipacommerce.dipasport.views.FragmentCartTabs;
import com.dipacommerce.dipasport.views.FragmentContact;
import com.dipacommerce.dipasport.views.FragmentHome;
import com.dipacommerce.dipasport.views.FragmentLogin;
import com.dipacommerce.dipasport.views.FragmentProductViews;
import com.dipacommerce.dipasport.views.FragmentProductsByCategory;
import com.dipacommerce.dipasport.views.FragmentQuotationList;
import com.dipacommerce.dipasport.views.FragmentSearchRequeted;
import com.dipacommerce.dipasport.views.FragmentSearchResults;
import com.dipacommerce.dipasport.views.FragmentShoppingCart;

public abstract class DiPaSport<T, RE> extends Fragment implements IDiPaSport, OnRequestCallback<JSONObject, RE>,
        OnLoadingTimeOutEvents {
    public static final int LANGUAGE_IT = 0;
    public static final int LANGUAGE_UK = 1;
    
    private static int mLanguages = LANGUAGE_IT;
    
    /**
     *  
     */
    protected static Context sCtx;

    protected static FragmentLogin sLoginFragment;
    protected static FragmentHome sHomeFragment;
    protected static FragmentSearchResults sSearchFragment;
    protected static FragmentAccount sAccountFragment;
    protected static FragmentContact sContactFragment;
    protected static FragmentProductViews sProductDetailFragment;
    protected static FragmentProductsByCategory sSearchByCategory;
    protected static FragmentShoppingCart sShoppingCartFragment;
    protected static FragmentQuotationList sQuotationFrament;
    protected static FragmentCartTabs sCartFragment;
    protected static FragmentSearchRequeted sSearchRequested;

    protected static DialogLoading sLoading;

    protected static CustomerManager sCustomer;
    protected static CategoryManager sCategory;
    protected static ProductManager sProduct;
    protected static ShoppingCartManager sShoppingCart;
    protected static QuotationManager sQuotation;
    protected static DatabaseHandler sDatabaseHandler;

    protected static int mPage = 1; // as default
    protected static ArrayList<ProductInfo> mListSearchResults = new ArrayList<ProductInfo>();
    protected static ArrayList<ProductInfo> mListSearchResultsByCategory = new ArrayList<ProductInfo>();

    protected static boolean mIsSearchByBarCodeScanner = false;

    public DiPaSport() {

    }

    /**
	 * 
	 */
    protected static IJSONManager<JSONObject> sJSONManager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        sCtx = getActivity();
        
        String languageToLoad = "it"; // your language
        if (DiPaSport.getLanguage() == DiPaSport.LANGUAGE_UK) {
            languageToLoad = "uk"; // your language
        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        sCtx.getResources().updateConfiguration(config,
                sCtx.getResources().getDisplayMetrics());
        
        if (sLoading != null) {
            sLoading = null;
        }
        sLoading = new DialogLoading(sCtx);
        sLoading.setTimeOut(Constants.REQUEST_TIME_OUT);
        sLoading.RegisterCallbackLoadingTimeOut(this);

        initSystem();

        if (sLoginFragment == null) {
            sLoginFragment = new FragmentLogin();
        }

        if (sHomeFragment == null) {
            sHomeFragment = new FragmentHome();
        }

        if (sSearchFragment == null) {
            sSearchFragment = new FragmentSearchResults();
        }

        if (sAccountFragment == null) {
            sAccountFragment = new FragmentAccount();
        }

        if (sShoppingCartFragment == null) {
            sShoppingCartFragment = new FragmentShoppingCart();
        }

        if (sQuotationFrament == null) {
            sQuotationFrament = new FragmentQuotationList();
        }

        if (sContactFragment == null) {
            sContactFragment = new FragmentContact();
        }

        if (sProductDetailFragment == null) {
            sProductDetailFragment = new FragmentProductViews();
        }

        if (sSearchByCategory == null) {
            sSearchByCategory = new FragmentProductsByCategory();
        }

        if (sCartFragment == null) {
            sCartFragment = new FragmentCartTabs();
        }

        if (sSearchRequested == null) {
            sSearchRequested = new FragmentSearchRequeted();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String languageToLoad = "it"; // your language
        if (DiPaSport.getLanguage() == DiPaSport.LANGUAGE_UK) {
            languageToLoad = "uk"; // your language
        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        sCtx.getResources().updateConfiguration(config,
                sCtx.getResources().getDisplayMetrics());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static void initContext(Activity activity) {
        sCtx = activity;
    }

    public static void showKeyboard() {
        // For showing keyboard
        InputMethodManager imm = (InputMethodManager) sCtx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideKeyboard() {
        // For hiding keyboard
        InputMethodManager imm = (InputMethodManager) sCtx.getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = ((Activity) sCtx).getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    /**
     * Override by inherited classes
     * 
     * @param tabId
     */
    public void UpdateTabChanged(final String tabId) {
    }

    /**
     * Override by inherited classes
     */
    public void UpdateViews(final View rootView) {
    }

    public void UpdateProgressDialogContent() {
    }

    /**
     * Override by inherited classes
     * 
     * @param rootView
     * @param productInfo
     */
    public void UpdateViews(final View rootView, final ProductInfo productInfo) {
    }

    protected void BuildActionBar() {
    }

    public void saveSearch(Bundle bundle) {
        SharedPreferences.Editor editor = sCtx.getSharedPreferences(IDiPaSport.Prefs.KEYWORD, Context.MODE_PRIVATE).edit();
        if (null != editor) {

            editor.putString(Constants.BUNDLE_KEYWORD, bundle.getString(Constants.BUNDLE_KEYWORD));
            editor.putString(Constants.BUNDLE_CATEGORY, bundle.getString(Constants.BUNDLE_CATEGORY));

            editor.commit();
        }
    }

    /**
     * 
     * @return
     */
    public Bundle loadSearch() {
        if (sCtx != null) {
            SharedPreferences editor = sCtx.getSharedPreferences(IDiPaSport.Prefs.KEYWORD, Context.MODE_PRIVATE);
            if (null != editor) {
                String keyword = editor.getString(Constants.BUNDLE_KEYWORD, "");
                String category = editor.getString(Constants.BUNDLE_CATEGORY, "");

                Bundle bundle = new Bundle();
                bundle.putString(Constants.BUNDLE_KEYWORD, keyword);
                bundle.putString(Constants.BUNDLE_CATEGORY, category);

                return bundle;
            }
        }
        return null;
    }

    /**
     * 
     * @return
     */
    public static boolean clearSearch() {
        if(sCtx == null) return false;
        SharedPreferences.Editor editor = sCtx.getSharedPreferences(IDiPaSport.Prefs.KEYWORD, Context.MODE_PRIVATE).edit();
        if (null != editor) {
            editor.clear();
        }
        return editor.commit();
    }

    private void initSystem() {
        sCustomer = Customers.getInstance();
        sCategory = Categories.getInstance();
        sProduct = Products.getInstance();
        if (sCustomer.isLogin()) {
            sDatabaseHandler = DatabaseHandler.getInstance(sCtx);
        }
        sShoppingCart = ShoppingCart.getInstance();
        sQuotation = Quotation.getInstance();
    }

    @Override
    public void OnLoadingTimeOut() {
        // Toast.makeText(sCtx,
        // sCtx.getString(R.string.str_server_not_response),
        // Toast.LENGTH_SHORT).show();
    }

    public static void saveIndex(int index) {
        SharedPreferences.Editor editor = sCtx.getSharedPreferences(IDiPaSport.Prefs.INDEX_SEARCH, Context.MODE_PRIVATE).edit();
        if (null != editor) {
            editor.putInt(Constants.BUNDLE_INDEX, index);
            editor.commit();
        }
    }

    /**
     * 
     * @return
     */
    public static int loadIndex() {
        if (sCtx != null) {
            SharedPreferences editor = sCtx.getSharedPreferences(IDiPaSport.Prefs.INDEX_SEARCH, Context.MODE_PRIVATE);
            if (null != editor) {
                int index = editor.getInt(Constants.BUNDLE_INDEX, 0);
                return index;
            }
        }
        return 0;
    }

    /**
     * 
     * @return
     */
    public static boolean clearIndex() {
        SharedPreferences.Editor editor = sCtx.getSharedPreferences(IDiPaSport.Prefs.INDEX_SEARCH, Context.MODE_PRIVATE).edit();
        if (null != editor) {
            editor.clear();
        }
        return editor.commit();
    }

    @Override
    public CharSequence getMessage(JSONObject jsonObject) {
        CharSequence message = "";
        if (jsonObject != null) {
            try {
                message = jsonObject.getString(CustomerInfo.JSON_TAG.MESSAGE);
                return message;
            } catch (JSONException e) {
                if (Constants.DEBUG_MODE) {
                    e.printStackTrace();
                }
            }
        }
        return message;
    }

    @Override
    public CODE getStatus(JSONObject jsonObject) {
        Object errorCode;
        try {
            errorCode = jsonObject.get(IDiPaSport.JSON_STATUS_TAG.ERROR_CODE);
            if (errorCode.equals(CODE.OK.getStatusCode())) {
                return CODE.OK;
            }
        } catch (JSONException e) {
            if (Constants.DEBUG_MODE) {
                e.printStackTrace();
            }
        }
        return CODE.ERROR;
    }

    @Override
    public RE getData(JSONObject t) {
        return null;
    }

    protected void setEditTextNormal(EditText t) {
        t.setBackgroundResource(R.drawable.di_textbox_border);
        t.setEms(10);
        t.setPadding(10, 10, 10, 10);
        t.setSingleLine(true);
    }

    /**
     * Set edit text with red border
     * 
     * @param t
     */
    protected void setEditTextHighLight(EditText t) {
        t.setBackgroundResource(R.drawable.di_textbox_border_highlight);
        t.setEms(10);
        t.setPadding(10, 10, 10, 10);
        t.setSingleLine(true);
    }

    public static DatabaseHandler getDatabaseHandler() {
        return sDatabaseHandler;
    }

    public static CustomerManager getCustomerManager() {
        return sCustomer;
    }

    public static ShoppingCartManager getShoppingCartManager() {
        return sShoppingCart;
    }

    protected void showTesting(String content) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(sCtx);
        dialog.setMessage(content);
        dialog.setPositiveButton("OK", null);
        dialog.create().show();
    }
    
    public static void setLanguage(int languageId){
        mLanguages = languageId;
        SharedPreferences.Editor prefs = sCtx.getSharedPreferences(DiPaSport.class.getPackage().getName(), Context.MODE_PRIVATE).edit();
        if(prefs != null){
            prefs.putInt("language", languageId);
            prefs.commit();
        }
    }
    
    public static int getLanguage(){
        return mLanguages;
    }

}
