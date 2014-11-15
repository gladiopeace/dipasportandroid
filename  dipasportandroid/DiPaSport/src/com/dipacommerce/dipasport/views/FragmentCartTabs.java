package com.dipacommerce.dipasport.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;

public class FragmentCartTabs extends DiPaSport<Object, Object> implements OnTabChangeListener {

    private static View mRoot;
    private static TabHost mTabHost;
    private static int mCurrentTabIndex;

    public static final String TAG_SHOPPING_CART = "cart";
    public static final String TAG_QUOTATION = "quotation";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.cart_tabs_fragment, null);
        mTabHost = (TabHost) mRoot.findViewById(android.R.id.tabhost);
        setupTabs();
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTabHost.setOnTabChangedListener(this);
        updateTab(TAG_SHOPPING_CART, R.id.tab_cart, sShoppingCartFragment);
    }

    @Override
    public void onTabChanged(String tabId) {
        //if (tabId.equals(TAG_SHOPPING_CART)) {
            updateTab(tabId, R.id.tab_cart, sShoppingCartFragment);
        //} else {
        //    updateTab(tabId, R.id.tab_quotation, sQuotationFrament);
        //}
    }

    private void setupTabs() {
        mTabHost.setup(); // important!
        mTabHost.addTab(newTab(TAG_SHOPPING_CART, sCtx.getString(R.string.str_quotaion_tab_shopping_cart), R.id.tab_cart));
        //mTabHost.addTab(newTab(TAG_QUOTATION, sCtx.getString(R.string.str_quotaion_tab_quotation), R.id.tab_quotation));
    }

    private TabSpec newTab(String tag, String label, int tabContentId) {
        View indicator = LayoutInflater.from(getActivity()).inflate(R.layout.ctrl_cart_tab, (ViewGroup) mRoot.findViewById(android.R.id.tabs), false);
        TextView textview = ((TextView) indicator.findViewById(R.id.text));
        if (label != null) {
            textview.setText(label);
        }

        TabSpec tabSpec = mTabHost.newTabSpec(tag);
        tabSpec.setIndicator(indicator);
        tabSpec.setContent(tabContentId);
        return tabSpec;
    }

    @SuppressWarnings("unchecked")
    private void updateTab(String tabId, int placeholder, Fragment fragment) {
        
        FragmentManager fm = getFragmentManager();
        if (fm == null)
            return;
        Fragment f = fm.findFragmentByTag(tabId);
        // if (f == null) {
        fm.beginTransaction().replace(placeholder, fragment, tabId).commit();
        // }
        ((DiPaSport<Object, Object>) fragment).UpdateTabChanged(tabId);
    }

    @Override
    public void UpdateTabChanged(String tabId) {
        updateTab(TAG_SHOPPING_CART, R.id.tab_cart, new FragmentShoppingCart());
    }

}
