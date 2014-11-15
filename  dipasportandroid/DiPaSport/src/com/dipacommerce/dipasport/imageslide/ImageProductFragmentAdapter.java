package com.dipacommerce.dipasport.imageslide;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ImageProductFragmentAdapter extends FragmentPagerAdapter {

    private List<String> mDataSource;

    private ImageProductFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public ImageProductFragmentAdapter(FragmentManager fm, ArrayList<String> datasource) {
        this(fm);
        this.mDataSource = datasource;
    }

    @Override
    public Fragment getItem(int position) {
        return ImageProductFragment.newInstance(mDataSource.get(position));
    }

    @Override
    public int getCount() {
        int count = (mDataSource != null) ? mDataSource.size() : -1;
        return count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}