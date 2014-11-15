package com.dipacommerce.dipasport.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.categories.CategoryInfo;
import com.dipacommerce.dipasport.controls.SearchBox;
import com.dipacommerce.dipasport.controls.SearchBox.ISearch;
import com.dipacommerce.dipasport.data.Constants;
import com.dipacommerce.dipasport.data.URLFormatter;
import com.dipacommerce.dipasport.network.imagedownloader.ImageLoader;
import com.dipacommerce.dipasport.network.json.AppConnection;
import com.dipacommerce.dipasport.network.json.IBaseAppConnection;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;
import com.dipacommerce.dipasport.products.IProduct;
import com.dipacommerce.dipasport.products.ProductInfo;
import com.dipacommerce.dipasport.utils.NetworkUtils;
import com.dipacommerce.dipasport.utils.camera.CameraManager;
import com.dipacommerce.dipasport.utils.camera.ICamera.CaptureEventChanged;
import com.dipacommerce.dipasport.utils.camera.ImageObject;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class FragmentHome extends DiPaSport<Object, Object> implements ISearch, IBaseAppConnection {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String PROPERTY_APP_VERSION = "appVersion";

    private Spinner mCategoryView;
    private View mRootView;
    private SearchBox mSearchBox;
    private ImageLoader<ImageView> mImageLoader;
    private static String mKeyword = "";
    private CameraManager mCamera;
    public static ArrayList<ImageObject> mFotos;
    public TextView mQRCode;
    private WebView mWebView;

    // For GCM
    private GoogleCloudMessaging mGcm;
    private String mRegId;
    private AppConnection mConnect;

    public FragmentHome() {
        BuildActionBar();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mImageLoader = new ImageLoader<ImageView>(sCtx);
        mCamera = CameraManager.getInstance();
        mCamera.registerEventChanged(OnCaptureEvent);

        UpdateProgressDialogContent();
        BuildActionBar();

        mFotos = new ArrayList<ImageObject>();

    }

    @Override
    public void UpdateProgressDialogContent() {
        sLoading.setMessage(sCtx.getString(R.string.str_loading));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_home_page, container, false);

        Bundle bundle = getArguments();
        if (bundle != null && !bundle.isEmpty()) {
            mIsSearchByBarCodeScanner = true;
            // onScanBarCode();
            bundle.clear();
        }

        UpdateViews(mRootView);
        sLoading.showDialog();

        sCategory.registerCallbackCategories(mCallbackCategories);
        sCategory.fetchAll();
        
         // for Push Message
        registerDevice();
        return mRootView;
    }

    @Override
    public void UpdateViews(View rootView) {
        mCategoryView = (Spinner) rootView.findViewById(R.id.home_categories);

        mSearchBox = (SearchBox) rootView.findViewById(R.id.searchresult_searchbox);
        if (mSearchBox != null) {
            mSearchBox.setCallbacks(this);
            loadSearchData();
            if (mCategoryView != null) {
                mSearchBox.setContent(mKeyword);
            }
        }

        TextView call = (TextView) rootView.findViewById(R.id.home_call);
        if (call != null) {
            call.setOnClickListener(onCall);
        }

        TextView captureProduct = (TextView) rootView.findViewById(R.id.home_capture);
        if (captureProduct != null) {
            captureProduct.setOnClickListener(onCaptureProduct);
        }

        mQRCode = (TextView) rootView.findViewById(R.id.home_qrcode);
        if (mQRCode != null) {
            mQRCode.setOnClickListener(OnQRCodeClick);
        }
        mWebView = (WebView) rootView.findViewById(R.id.home_webview);
        if (mWebView != null) {
            if (!NetworkUtils.isConnected(sCtx)) {
                mWebView.setVisibility(View.GONE);
            }
            String device_type = sCtx.getResources().getString(R.string.screen_type);
            String cms_page = sCtx.getResources().getString(R.string.cms_page_phone);
            if (device_type != null) {
                if (device_type.equals("tablet")) {
                    cms_page = sCtx.getResources().getString(R.string.cms_page_tablet);
                }
            }
            mWebView.loadUrl(cms_page);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onPause() {
        super.onPause();

        setSearchData();
    }

    @Override
    public void onStop() {
        super.onStop();
        // clearSearch();
    }

    @Override
    public void onSearch(CharSequence searchContent) {

        clearSearch();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_KEYWORD, searchContent.toString());
        bundle.putString(Constants.BUNDLE_CATEGORY, mCategoryView.getSelectedItemId() + "");
        saveSearch(bundle);

        TabsFragment.setTabSelected(TabsFragment.TAB_SEARCH_INDEX);
    }

    @Override
    public void onScanBarCode() {
        Intent captureProduct = new Intent(sCtx, CaptureActivity.class);
        startActivityForResult(captureProduct, SEARCH_METHODS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_METHODS) {
            if (resultCode == TabsFragment.TAB_SHOPPINGCART_INDEX) {
                TabsFragment.setTabSelected(resultCode);
            } else if (resultCode == Activity.RESULT_OK) {
                Bundle resultData = data.getExtras();
                if (resultData != null && resultData.getBoolean("status")) {
                    FragmentManager fm = getFragmentManager();
                    Bundle bundle = new Bundle();
                    bundle.putInt("tabid", TabsFragment.TAB_HOME_INDEX);
                    bundle.putBoolean("QRCode", true);
                    sProductDetailFragment.setArguments(bundle);
                    FragmentTransaction ta = fm.beginTransaction();
                    ta.replace(R.id.tab_home, sProductDetailFragment);
                    ta.commit();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Callback<JSONObject> mCallbackCategories = new Callback<JSONObject>() {

        @Override
        public void onResults(JSONObject results) {
            if (results == null) {
                Toast.makeText(sCtx, sCtx.getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                return;
            }

            final List<CategoryInfo> categories = sCategory.getData(results);
            if (categories == null) {
                Toast.makeText(sCtx, sCtx.getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                return;
            }

            categories.add(categories.size(), new CategoryInfo(sCtx.getString(R.string.str_home_categoria), "-1"));

            if (mCategoryView != null) {
                ArrayAdapter<CategoryInfo> adapter = new ArrayAdapter<CategoryInfo>(sCtx, android.R.layout.simple_dropdown_item_1line, categories) {
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);
                        if (position == getCount()) {
                            ((TextView) v.findViewById(android.R.id.text1)).setText("");
                            ((TextView) v.findViewById(android.R.id.text1)).setHint(sCtx.getString(R.string.str_home_categoria));
                        }
                        return v;
                    };

                    public int getCount() {
                        return super.getCount() - 1;
                    };
                };
                mCategoryView.setAdapter(adapter);
                mCategoryView.setSelection(adapter.getCount());
            }

            mCategoryView.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    CategoryInfo categorySelected = categories.get(position);
                    sCategory.setCategorySelected(categorySelected);
                    if (!categorySelected.getId().equals("-1")) {
                        loadSubCategory();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }

            });
        }

        @Override
        public void onErrors(int _errorCode, String _errorMessage) {
            sLoading.closeDialog();
            // Toast.makeText(sCtx, _errorMessage, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDone() {
            sProduct.registerCallbackProductsFeature(mCallbackProductsFeature);
            sProduct.fetchProductFeatures();
        }
    };

    private void loadSubCategory() {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.tab_home, new FragmentSubCategory()).commit();
    }

    /**
	 * 
	 */
    private Callback<JSONObject> mCallbackProductsFeature = new Callback<JSONObject>() {

        @Override
        public void onResults(JSONObject results) {
            if (results == null) {
                Toast.makeText(sCtx, sCtx.getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                return;
            }

            List<ProductInfo> listProduct = sProduct.getData(results);
            if (listProduct == null)
                return;
            // Display product 1
            TextView product1_text = (TextView) mRootView.findViewById(R.id.home_product_one_text);
            ImageView product1_img = (ImageView) mRootView.findViewById(R.id.home_product_one_img);
            if (product1_text != null) {
                String product_name = listProduct.get(0).getName();
                product1_text.setText(product_name);
            }

            if (product1_img != null) {
                String product_image = listProduct.get(0).getImagePath();

                mImageLoader.DisplayImage(product_image, 0, product1_img, Constants.PRODUCT_FEATURES_WIDTH, Constants.PRODUCT_FEATURES_HEIGHT);

                final String product_id1 = listProduct.get(0).getEntityID();

                product1_img.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        FragmentManager fm = getFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString(IProduct.BUNDLE.PRODUCT_ID, product_id1);
                        sProductDetailFragment.setArguments(bundle);
                        FragmentTransaction ta = fm.beginTransaction();
                        ta.replace(R.id.tab_home, sProductDetailFragment);
                        ta.commit();
                    }
                });
            }

            // Display product 2
            TextView product2_text = (TextView) mRootView.findViewById(R.id.home_product_two_text);
            ImageView product2_img = (ImageView) mRootView.findViewById(R.id.home_product_two_img);
            if (product2_text != null) {
                String product_name = listProduct.get(1).getName();
                product2_text.setText(product_name);
            }

            if (product2_img != null) {
                String product_image = listProduct.get(1).getImagePath();
                mImageLoader.DisplayImage(product_image, 0, product2_img, Constants.PRODUCT_FEATURES_WIDTH, Constants.PRODUCT_FEATURES_HEIGHT);

                final String product_id2 = listProduct.get(1).getEntityID();

                product2_img.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        FragmentManager fm = getFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString(IProduct.BUNDLE.PRODUCT_ID, product_id2);
                        sProductDetailFragment.setArguments(bundle);
                        FragmentTransaction ta = fm.beginTransaction();
                        ta.replace(R.id.tab_home, sProductDetailFragment);
                        ta.commit();
                    }
                });
            }

            // Display product 3
            TextView product3_text = (TextView) mRootView.findViewById(R.id.home_product_three_text);
            ImageView product3_img = (ImageView) mRootView.findViewById(R.id.home_product_three_img);
            if (product3_text != null) {
                String product_name = listProduct.get(2).getName();
                product3_text.setText(product_name);
            }

            if (product3_img != null) {
                String product_image = listProduct.get(2).getImagePath();
                mImageLoader.DisplayImage(product_image, 0, product3_img, Constants.PRODUCT_FEATURES_WIDTH, Constants.PRODUCT_FEATURES_HEIGHT);

                final String product_id3 = listProduct.get(2).getEntityID();

                product3_img.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        FragmentManager fm = getFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString(IProduct.BUNDLE.PRODUCT_ID, product_id3);
                        sProductDetailFragment.setArguments(bundle);
                        FragmentTransaction ta = fm.beginTransaction();
                        ta.replace(R.id.tab_home, sProductDetailFragment);
                        ta.commit();
                    }
                });
            }

            // Display product 4
            TextView product4_text = (TextView) mRootView.findViewById(R.id.home_product_four_text);
            ImageView product4_img = (ImageView) mRootView.findViewById(R.id.home_product_four_img);
            if (product4_text != null) {
                String product_name = listProduct.get(3).getName();
                product4_text.setText(product_name);
            }

            if (product4_img != null) {
                String product_image = listProduct.get(3).getImagePath();
                mImageLoader.DisplayImage(product_image, 0, product4_img, Constants.PRODUCT_FEATURES_WIDTH, Constants.PRODUCT_FEATURES_HEIGHT);

                final String product_id4 = listProduct.get(3).getEntityID();

                product4_img.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        FragmentManager fm = getFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString(IProduct.BUNDLE.PRODUCT_ID, product_id4);
                        sProductDetailFragment.setArguments(bundle);
                        FragmentTransaction ta = fm.beginTransaction();
                        ta.replace(R.id.tab_home, sProductDetailFragment);
                        ta.commit();
                    }
                });
            }

        }

        @Override
        public void onErrors(int _errorCode, String _errorMessage) {
            // Toast.makeText(sCtx, _errorMessage, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDone() {
            sLoading.closeDialog();
        }
    };

    @Override
    protected void BuildActionBar() {
        if (sCtx != null) {
            Activity activity = ((Activity) sCtx);
            View mCustomTitle = activity.getLayoutInflater().inflate(R.layout.ctrl_common_title, null);
            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                ((TextView) mCustomTitle.findViewById(R.id.title_name)).setText(activity.getString(R.string.str_home_title));

                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setCustomView(mCustomTitle);
            }
        }
    }

    /**
	 * 
	 */
    private void setSearchData() {
        SharedPreferences.Editor editor = sCtx.getSharedPreferences("KEYWORD", Context.MODE_PRIVATE).edit();
        if (null != editor) {

            editor.putString("KEYWORD", mSearchBox.getContent().toString());
            editor.putInt("CAT_INDEX", (int) mCategoryView.getSelectedItemId());
            editor.commit();
        }
    }

    /**
     * 
     * @return
     */
    private void loadSearchData() {
        SharedPreferences editor = sCtx.getSharedPreferences("KEYWORD", Context.MODE_PRIVATE);
        if (null != editor) {
            String result = editor.getString("KEYWORD", "");
            // int index = editor.getInt("CAT_ID", -1);

            mKeyword = result;
            // mCatIndex = index;
        }
    }

    @SuppressWarnings("unused")
    private void clearLogin() {
        SharedPreferences.Editor editor = sCtx.getSharedPreferences("KEYWORD", Context.MODE_PRIVATE).edit();
        if (null != editor) {
            editor.clear();
            editor.commit();
        }
    }

    @Override
    public void UpdateTabChanged(final String tabId) {
        BuildActionBar();
        if (mSearchBox != null) {
            Bundle bundle = loadSearch();
            if (bundle != null) {
                mSearchBox.setContent(bundle.getString(Constants.BUNDLE_KEYWORD).toString());
            }
        }
        UpdateProgressDialogContent();
    }

    private OnClickListener onCall = new OnClickListener() {

        @Override
        public void onClick(View v) {

            final AlertDialog.Builder confirm = new AlertDialog.Builder(sCtx);
            confirm.setMessage(sCtx.getString(R.string.str_home_call_confirm));

            // Call
            confirm.setPositiveButton(sCtx.getString(R.string.str_home_call_confirm_ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + getString(R.string.phone_number)));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callIntent);
                }

            });

            // Cancel and back to Home
            confirm.setNegativeButton(sCtx.getString(R.string.str_home_call_confirm_cancel), null);
            confirm.create().show();
        }
    };

    private OnClickListener onCaptureProduct = new OnClickListener() {

        @Override
        public void onClick(View v) {
            mCamera.setAutoCallback(false);
            mCamera.openCamera();
        }
    };

    private OnClickListener OnQRCodeClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            mIsSearchByBarCodeScanner = true;
            onScanBarCode();
        }
    };

    /**
     * 
     */
    private void displayTakePictureOptions() {
        String languageToLoad = "it"; // your language
        if (DiPaSport.getLanguage() == DiPaSport.LANGUAGE_UK) {
            languageToLoad = "uk"; // your language
        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getResources().updateConfiguration(config,
                getActivity().getResources().getDisplayMetrics());

        final String[] items = sCtx.getResources().getStringArray(R.array.str_home_send_request_options);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(sCtx);
        dialog.setCancelable(false);
        dialog.setTitle(sCtx.getString(R.string.str_option_title));
        dialog.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    mCamera.openCamera();
                } else if (which == 1) {
                    HomePageActivity home = (HomePageActivity) sCtx;
                    home.isCaptureProduct = true;
                    home.onPostResume();
                }
            }
        });
        dialog.create().show();
    }

    private CaptureEventChanged OnCaptureEvent = new CaptureEventChanged() {

        public void OnCaptureFail(String errorMessage) {
            Toast.makeText(sCtx, "Fail while captures the picture from your camera", Toast.LENGTH_SHORT).show();
        };

        public void OnCaptureSuccess(Bitmap bmp) {

        };

        public void OnCaptureSeccess(ImageObject bmpObj) {
            mFotos.add(bmpObj);
            displayTakePictureOptions();
        };

    };
    

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(sCtx);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (FragmentActivity)sCtx,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                // Log.i(TAG, "This device is not supported.");
                ((FragmentActivity)sCtx).finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(Constants.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences,
        // but
        // how you store the regID in your app is up to you.
        return sCtx.getSharedPreferences(sCtx.getClass().getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (mGcm == null) {
                        mGcm = GoogleCloudMessaging.getInstance(sCtx);
                    }
                    mRegId = mGcm.register(Constants.SENDER_ID);
                    
                    // You should send the registration ID to your server over
                    // HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your
                    // app.
                    // The request to your server should be authenticated if
                    // your app
                    // is using accounts.
                    // sendRegistrationIdToBackend();
                    //Secure.getString(getContentResolver(), Secure.ANDROID_ID);
                    // For this demo: we don't need to send it because the
                    // device
                    // will send upstream messages to a server that echo back
                    // the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(sCtx, mRegId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return mRegId;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.e("GCM", "ID: " + msg);
                List<NameValuePair> args = new ArrayList<NameValuePair>();
                args.add(new BasicNameValuePair("udid", msg));
                mConnect.registerCallbackListener(FragmentHome.this);
                String url = URLFormatter.buildUrl(args, Constants.Query.URL_REG_PUSH_SERVICE);
                mConnect.request(0, new String[]{ url }, true);
            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context
     *            application's context.
     * @param regId
     *            registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    @Override
    public void onPreRequest() {
        //final InputMethodManager im = (InputMethodManager) sCtx.getSystemService(Context.INPUT_METHOD_SERVICE);
        //im.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onRequestFinished(int requestId) {
        if(requestId == 0 && mConnect.isResultsOK()){
            Log.i("GCM", mConnect.getResultJsonObject().toString());
        }
        Log.e("GCM", mConnect.getResponseMessage());
        
    }

    @Override
    public void onRequestCanceled(int requestId) {
        // TODO Auto-generated method stub
        
    }
    
    private void registerDevice(){
        ((FragmentActivity)sCtx).runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                mConnect = AppConnection.getInstance((FragmentActivity)sCtx);
                
                if (checkPlayServices()) {
                    mGcm = GoogleCloudMessaging.getInstance(sCtx);
                    mRegId = getRegistrationId(sCtx);
                    if (mRegId.isEmpty()) {
                        registerInBackground();
                    }
                } else {
                    Toast.makeText(sCtx, "You be must get valid Play Services APK ", Toast.LENGTH_LONG).show();
                }
                
            }
        });
    }
}
