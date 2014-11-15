package com.dipacommerce.dipasport.views;

import android.app.Activity;
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
import com.dipacommerce.dipasport.data.Constants;

public class TabsFragment extends DiPaSport<Object, Object> implements OnTabChangeListener {

    private static View mRoot;
    protected static TabHost mTabHost;
    private static int mCurrentTab;

    public static final int TAB_HOME_INDEX = 0;
    public static final int TAB_SEARCH_INDEX = 1;
    public static final int TAB_ACCOUNT_INDEX = 2;
    public static final int TAB_SHOPPINGCART_INDEX = 3;
    public static final int TAB_CONTACT_INDEX = 4;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.tabs_fragment, null);
        mTabHost = (TabHost) mRoot.findViewById(android.R.id.tabhost);
        setupTabs();
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        mTabHost.setOnTabChangedListener(this);
        mCurrentTab = 0;
        updateTab(Constants.TAB_HOME, R.id.tab_home, sHomeFragment = new FragmentHome());
        mTabHost.setCurrentTab(mCurrentTab);

    }

    private void setupTabs() {
        mTabHost.setup(); // important!
        mTabHost.addTab(newTab(Constants.TAB_HOME, getString(R.string.str_menu_home), R.id.tab_home, R.drawable.home));
        mTabHost.addTab(newTab(Constants.TAB_SEARCH, getString(R.string.str_menu_cerca), R.id.tab_search, R.drawable.search));
        mTabHost.addTab(newTab(Constants.TAB_ACCOUNT, getString(R.string.str_menu_account), R.id.tab_account, R.drawable.account));
        mTabHost.addTab(newTab(Constants.TAB_SHOPPINGCART, getString(R.string.str_menu_carrello), R.id.tab_shoppingcart, R.drawable.shoppingcart));
        mTabHost.addTab(newTab(Constants.TAB_CONTACT, getString(R.string.str_menu_contatti), R.id.tab_contact, R.drawable.contact));

    }

    private TabSpec newTab(String tag, String label, int tabContentId, int iconId) {
        View indicator = LayoutInflater.from(getActivity()).inflate(R.layout.ctrl_tab, (ViewGroup) mRoot.findViewById(android.R.id.tabs), false);
        TextView textview = ((TextView) indicator.findViewById(R.id.text));
        if (label != null) {
            textview.setText(label);
            textview.setCompoundDrawablesWithIntrinsicBounds(0, iconId, 0, 0);
        }

        TabSpec tabSpec = mTabHost.newTabSpec(tag);
        tabSpec.setIndicator(indicator);
        tabSpec.setContent(tabContentId);
        return tabSpec;
    }

    @Override
    public void onTabChanged(String tabId) {
        if (Constants.TAB_HOME.equals(tabId)) {
            mCurrentTab = 0;
            updateTab(tabId, R.id.tab_home, sHomeFragment);
            return;
        } else if (Constants.TAB_SEARCH.equals(tabId)) {
            mCurrentTab = 1;
            updateTab(Constants.TAB_SEARCH, R.id.tab_search, sSearchFragment);
        } else if (Constants.TAB_ACCOUNT.equals(tabId)) {
            mCurrentTab = 2;
            if (sCustomer.isLogin()) {
                updateTab(tabId, R.id.tab_account, sAccountFragment);
            } else {
                updateTab(tabId, R.id.tab_account, sLoginFragment);
            }
            return;
        } else if (Constants.TAB_SHOPPINGCART.equals(tabId)) {
            mCurrentTab = 3;
            updateTab(Constants.TAB_SHOPPINGCART, R.id.tab_shoppingcart, sCartFragment);
        } else {
            mCurrentTab = 4;
            updateTab(Constants.TAB_CONTACT, R.id.tab_contact, sContactFragment);
        }
    }

    @SuppressWarnings("unchecked")
    private void updateTab(String tabId, int placeholder, Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        Fragment f = fm.findFragmentByTag(tabId);
        if (f == null) {
            fm.beginTransaction().replace(placeholder, fragment, tabId).commit();
        }
        ((DiPaSport<Object, Object>) fragment).UpdateTabChanged(tabId);
    }

    public static void setTabSelected(int id) {
        mTabHost.setCurrentTab(id);
        mCurrentTab = id;
    }

    public static View getRoot() {
        return mRoot;
    }
}
