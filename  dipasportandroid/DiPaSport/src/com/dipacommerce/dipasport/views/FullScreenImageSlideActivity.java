package com.dipacommerce.dipasport.views;

import java.util.ArrayList;
import java.util.Locale;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.imageslide.CirclePageIndicator;
import com.dipacommerce.dipasport.imageslide.ImageProductFragmentAdapter;
import com.dipacommerce.dipasport.products.IProduct;

public class FullScreenImageSlideActivity extends FragmentActivity {

    public ImageProductFragmentAdapter mAdapter;
    public ViewPager mPager;
    public CirclePageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String languageToLoad = "it"; // your language
        if (DiPaSport.getLanguage() == DiPaSport.LANGUAGE_UK) {
            languageToLoad = "uk"; // your language
        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        
        setContentView(R.layout.fullscreen_image_product);
        BuildActionBar();

        Intent intent = getIntent();
        if (intent != null) {

            ArrayList<String> listimage = intent.getStringArrayListExtra(IProduct.BUNDLE.PRODUCT_IMAGE_PATH);

            mAdapter = new ImageProductFragmentAdapter(getSupportFragmentManager(), listimage);
            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(mAdapter);
            mPager.setOffscreenPageLimit(10);

            mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
            mIndicator.setViewPager(mPager);
            mIndicator.setSnap(true);

            String product_name = intent.getStringExtra(IProduct.BUNDLE.PRODUCT_NAME);
            TextView proCode = (TextView) findViewById(R.id.fullscreen_product_code);
            if (proCode != null) {
                if (product_name.length() == 0) {
                    proCode.setVisibility(View.GONE);
                }
                if (product_name.length() > 0) {
                    proCode.setText(product_name);
                } else {
                    proCode.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 
     */
    private void BuildActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            View mCustomTitle = getLayoutInflater().inflate(R.layout.ctrl_common_title, null);
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
                        finish();
                    }
                });
            }

            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(mCustomTitle);

        }
    }
}