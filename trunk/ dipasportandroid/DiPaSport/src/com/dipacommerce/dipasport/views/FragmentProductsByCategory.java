package com.dipacommerce.dipasport.views;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.data.SearchResultAdapter;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;
import com.dipacommerce.dipasport.products.ProductInfo;

public class FragmentProductsByCategory extends DiPaSport<Object, Object> {
    private View mRootView;
    private ListView mListProducts;
    private Button mLoadMore;
    private ProgressBar mProgressLoadMore;
    private String mCatId = "";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BuildActionBar();
        mPage = 1;
        mListSearchResultsByCategory.clear();
    }

    @Override
    public void UpdateProgressDialogContent() {
        sLoading.setMessage(sCtx.getString(R.string.str_searching));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_products_bycategory, container, false);

        UpdateViews(rootView);
        UpdateProgressDialogContent();
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        mListSearchResultsByCategory.clear();
    }

    @Override
    public void UpdateViews(View rootView) {
        mRootView = rootView;
        mListProducts = (ListView) mRootView.findViewById(R.id.search_products_bycategory_list);

        Bundle bundle = getArguments();

        if (bundle != null) {
            mCatId = bundle.getString("catid");
        }

        if (mListSearchResultsByCategory != null && mListSearchResultsByCategory.size() > 0) {
            SearchResultAdapter adapter = new SearchResultAdapter(sCtx, R.layout.ctrl_product_result, mListSearchResultsByCategory, R.id.tab_home, 2);
            mListProducts.setAdapter(adapter);
        } else {
            callSearch(mCatId);
        }

        LayoutInflater layout = (LayoutInflater) sCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout searchFooter = (LinearLayout) layout.inflate(R.layout.ctrl_loadmore, (ViewGroup) mRootView, false);
        searchFooter.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
        mListProducts.addFooterView(searchFooter);
        mLoadMore = (Button) searchFooter.findViewById(R.id.search_loadmore);
        if (mLoadMore != null) {
            mLoadMore.setOnClickListener(OnLoadMore);
        }

        mProgressLoadMore = (ProgressBar) searchFooter.findViewById(R.id.search_progress);
    }

    private void callSearch(String catid) {
        sLoading.showDialog();
        sProduct.registerCallbackSearchByCategory(mCallbackSearchByCategory);
        String idUser = sCustomer.getCustomerInfo().getUserId();
        sProduct.filterByCategory(catid, mPage, idUser);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListSearchResultsByCategory != null) {
            mListSearchResultsByCategory.clear();
        }
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
        fm.beginTransaction().replace(R.id.tab_home, new FragmentSubCategory()).commit();
    }

    private Callback<JSONObject> mCallbackSearchByCategory = new Callback<JSONObject>() {

        @Override
        public void onResults(JSONObject results) {
            if (results == null) {
                Toast.makeText(sCtx, sCtx.getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayList<ProductInfo> tmp = sProduct.convertJSONSearchResultToList(results);
            if (tmp != null && tmp.size() > 0) {
                if (mListProducts != null) {
                    int index = 0;
                    mListSearchResultsByCategory.addAll(tmp);
                    SearchResultAdapter adapter = new SearchResultAdapter(sCtx, R.layout.ctrl_product_result, mListSearchResultsByCategory, R.id.tab_home, 2);
                    mListProducts.setAdapter(adapter);
                    if (mListSearchResultsByCategory.size() > tmp.size()) {
                        index = mListSearchResultsByCategory.size() - tmp.size();
                    }
                    mListProducts.setSelection(index);
                }
            } else {
                Toast.makeText(sCtx, sCtx.getString(R.string.str_search_not_found), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onErrors(int _errorCode, String _errorMessage) {

        }

        @Override
        public void onDone() {
            sLoading.closeDialog();
            if (mPage > 1) {
                if (mProgressLoadMore != null) {
                    mProgressLoadMore.setVisibility(View.GONE);
                    mLoadMore.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    private OnClickListener OnLoadMore = new OnClickListener() {

        @Override
        public void onClick(View v) {
            ++mPage;
            callSearch(mCatId);

            v.setVisibility(View.GONE);
            mProgressLoadMore.setVisibility(View.VISIBLE);
        }
    };

}
