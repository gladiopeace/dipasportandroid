package com.dipacommerce.dipasport.views;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.categories.CategoryInfo;

public class FragmentSubCategory extends DiPaSport<Object, Object> {
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BuildActionBar();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listsubcategory, container, false);
        UpdateViews(rootView);
        return rootView;
    }

    @Override
    public void UpdateViews(View rootView) {
        final ListView list = (ListView) rootView.findViewById(R.id.subcategory);
        if (list != null) {
            ArrayAdapter<CategoryInfo> adapter = new ArrayAdapter<CategoryInfo>(sCtx, android.R.layout.simple_list_item_1, sCategory.getCategorySelected().getSubmenu());
            list.setAdapter(adapter);
        }
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryInfo cat = (CategoryInfo) list.getItemAtPosition(position);
                FragmentManager fm = getFragmentManager();

                Bundle bundle = new Bundle();
                bundle.putString("catid", cat.getId());
                sSearchByCategory.setArguments(bundle);
                fm.beginTransaction().replace(R.id.tab_home, sSearchByCategory).commit();
            }
        });
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
        fm.beginTransaction().replace(R.id.tab_home, sHomeFragment).commit();
    }

}
