package com.dipacommerce.dipasport.views;

import java.util.Locale;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HistoryActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        
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
        
        setContentView(R.layout.activity_barcode_history);

        if (arg0 == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.barcode_container, new FragmentBarcodeHistory()).commit();
        }

        BuildActionBar();
    }

    /**
     * 
     */
    private void BuildActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            View mCustomTitle = getLayoutInflater().inflate(R.layout.ctrl_common_title, null);
            ((TextView) mCustomTitle.findViewById(R.id.title_name)).setText(getString(R.string.menu_history));

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
