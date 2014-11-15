package com.dipacommerce.dipasport.views;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.controls.SearchBox;
import com.dipacommerce.dipasport.controls.SearchBox.ISearch;
import com.dipacommerce.dipasport.data.Constants;
import com.dipacommerce.dipasport.data.SearchResultAdapter;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;
import com.dipacommerce.dipasport.products.ProductInfo;
import com.dipacommerce.dipasport.shoppingcart.ShopImp.OptionsChanged;

public class FragmentSearchResults extends DiPaSport<Object, Object> implements ISearch, OptionsChanged {
    private View mRootView;
    private SearchBox mSearchBox;
    private Button mLoadMore;
    private ProgressBar mProgressLoadMore;
    private ListView mListResults;

    private String mKeyword;
    private String mCategoryId;
    private int mIndexSelection = 0;
    private boolean mIsLoadMore = false;
    
    private boolean isRestoreState = false;
    
    private boolean mIsNextPage = true;

    public FragmentSearchResults() {
        BuildActionBar();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        UpdateProgressDialogContent();
        UpdateSearchResults();
        BuildActionBar();
        mPage = 1;
        clearIndex();
        mListSearchResults.clear();
        sShoppingCart.registerOptionsChanged(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void UpdateProgressDialogContent() {
        sLoading.setMessage(sCtx.getString(R.string.str_searching));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_result_page, container, false);
        mSearchBox = (SearchBox) mRootView.findViewById(R.id.searchresult_searchbox);
        mSearchBox.setCallbacks(this);

        Bundle bundle = loadSearch();
        if (bundle != null) {

            mKeyword = bundle.getString(Constants.BUNDLE_KEYWORD);
            mCategoryId = bundle.getString(Constants.BUNDLE_CATEGORY);

            if (mKeyword != null || mCategoryId != null) {
                mSearchBox.setContent(mKeyword);
                if (mListSearchResults != null && mListSearchResults.size() == 0 && mSearchBox.getContent().length() > 0) {
                    if (!isRestoreState) {
                        onSearchClick(mSearchBox.getContent().toString());
                        isRestoreState = true;
                    }
                } else {
                    UpdateSearchResults();
                }
            }
        }

        UpdateViews(mRootView);

        return mRootView;
    }

    @Override
    public void UpdateViews(View rootView) {
        mListResults = (ListView) mRootView.findViewById(R.id.searchresult_content);

        LayoutInflater layout = (LayoutInflater) sCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout searchFooter = (LinearLayout) layout.inflate(R.layout.ctrl_loadmore, (ViewGroup) mRootView, false);
        searchFooter.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
        mListResults.addFooterView(searchFooter);
        mLoadMore = (Button) searchFooter.findViewById(R.id.search_loadmore);
        if (mLoadMore != null) {
            mLoadMore.setOnClickListener(OnLoadMore);
        }

        mProgressLoadMore = (ProgressBar) searchFooter.findViewById(R.id.search_progress);
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePosition();
    }

    private void onSearchClick(String keyword) {
        mIsLoadMore = false;
        sLoading.showDialog();
        sProduct.registerCallbackSearchResult(mCallbackSearchResult);
        String idUser = sCustomer.getCustomerInfo().getUserId();
        sProduct.filter(keyword, mPage, idUser);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_KEYWORD, keyword);
        bundle.putString(Constants.BUNDLE_CATEGORY, mCategoryId);

        saveSearch(bundle);
    }

    /**
     * 
     */
    private Callback<JSONObject> mCallbackSearchResult = new Callback<JSONObject>() {

        @Override
        public void onResults(JSONObject results) {
            if (results == null) {
                Toast.makeText(sCtx, sCtx.getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                return;
            } else {
                ArrayList<ProductInfo> tmp = sProduct.convertJSONSearchResultToList(results);
                try {
                    mIsNextPage = results.getJSONObject("Paging").getBoolean("next-page");
                } catch (JSONException e) {
                    e.printStackTrace();
                    mIsNextPage = false;
                }
                if (tmp != null && tmp.size() > 0) {
                    isRestoreState = false;
                    mLoadMore.setVisibility(View.VISIBLE);
                    mLoadMore.setVisibility(View.VISIBLE);
                    if (!mIsLoadMore) {
                        mListSearchResults.clear();
                        clearIndex();
                    }

                    mListSearchResults.addAll(tmp);
                    UpdateSearchResults();
                    if (mListSearchResults.size() > tmp.size()) {
                        mIndexSelection = mListSearchResults.size() - tmp.size();
                    }

                    if (!mIsLoadMore) {
                        mListResults.setSelection(0);
                    } else {
                        mListResults.setSelection(mIndexSelection);
                    }

                    saveIndex(mIndexSelection);
                } else {
                    isRestoreState = true;
                    mListSearchResults.clear();
                    UpdateSearchResults();
                    mLoadMore.setVisibility(View.GONE);
                    Toast.makeText(sCtx, sCtx.getString(R.string.str_search_not_found), Toast.LENGTH_LONG).show();

                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.tab_search, sSearchRequested).commit();
                }
            }
            sLoading.closeDialog();
        }

        @Override
        public void onErrors(int _errorCode, String _errorMessage) {
            sLoading.closeDialog();
            Toast.makeText(sCtx, sCtx.getString(R.string.no_network_connection), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDone() {
            if (mPage > 1) {
                if (mProgressLoadMore != null) {
                    mProgressLoadMore.setVisibility(View.GONE);
                    mLoadMore.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    public void onSearch(CharSequence searchContent) {
        mIsSearchByBarCodeScanner = false;
        this.onSearchClick(searchContent.toString());
    }

    public void onSearchWithBarCode(CharSequence searchContent) {
        mIsSearchByBarCodeScanner = true;
        this.onSearchClick(searchContent.toString());
    }

    @Override
    public void onScanBarCode() {
        Intent captureProduct = new Intent(sCtx, CaptureActivity.class);
        startActivityForResult(captureProduct, SEARCH_METHODS);
    }

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

    @Override
    public void UpdateTabChanged(String tabId) {

        BuildActionBar();
        UpdateSearchResults();
        UpdateProgressDialogContent();

        Bundle bundle = loadSearch();
        if (bundle != null) {

            String keyword = bundle.getString(Constants.BUNDLE_KEYWORD);
            String cat_id = bundle.getString(Constants.BUNDLE_CATEGORY);

            if (keyword != null || cat_id != null) {
                if (mSearchBox != null) {
                    mSearchBox.setContent(keyword);
                    if (!keyword.equals(mKeyword)) {
                        mKeyword = keyword;
                        mCategoryId = cat_id;
                        onSearchClick(mSearchBox.getContent().toString());
                    } else {
                        UpdateSearchResults();
                    }
                }
            }
        }

        updatePosition();
    }

    private void UpdateSearchResults() {
        if (mRootView != null) {
            SearchResultAdapter adapter = new SearchResultAdapter(sCtx, R.layout.ctrl_product_result, mListSearchResults, R.id.tab_search, 1);
            if (mListResults == null) {
                mListResults = (ListView) mRootView.findViewById(R.id.searchresult_content);
            }
            mListResults.setAdapter(adapter);
        }
    }

    private OnClickListener OnLoadMore = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if(mIsNextPage){
                ++mPage;
                onSearchClick(mSearchBox.getContent().toString());
                mIsLoadMore = true;
                v.setVisibility(View.GONE);
                mProgressLoadMore.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(sCtx, sCtx.getResources().getString(R.string.str_search_not_found), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void updatePosition() {
        if (mListResults != null) {
            mIndexSelection = loadIndex();
            mListResults.setSelection(mIndexSelection);
        }
    }

    @Override
    public void onContinueShopping() {
        if (mIsSearchByBarCodeScanner) {
            // back to barcode scanner
            Intent captureProduct = new Intent(sCtx, CaptureActivity.class);
            startActivityForResult(captureProduct, SEARCH_METHODS);
        }
    }

    @Override
    public void onGoToShoppingCart() {
        TabsFragment.setTabSelected(TabsFragment.TAB_SHOPPINGCART_INDEX);
    }

}
