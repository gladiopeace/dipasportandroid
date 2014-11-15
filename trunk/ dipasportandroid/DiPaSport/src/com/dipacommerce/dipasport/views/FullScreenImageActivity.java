package com.dipacommerce.dipasport.views;

import java.util.Locale;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.network.imagedownloader.ImageLoader;
import com.dipacommerce.dipasport.products.IProduct;
import com.dipacommerce.dipasport.utils.imagezoom.PhotoView;

public class FullScreenImageActivity extends FragmentActivity {

    private ImageLoader<PhotoView> mImageLoader;
    private static final String PROTOCOL = "http://";

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
        
        setContentView(R.layout.fragment_fullscreen_image_product);

        BuildActionBar();

        Intent intent = getIntent();
        if (intent != null) {
            String product_path = intent.getStringExtra(IProduct.BUNDLE.PRODUCT_IMAGE_PATH);
            String product_name = intent.getStringExtra(IProduct.BUNDLE.PRODUCT_NAME);
            PhotoView product_image = (PhotoView) findViewById(R.id.imageEnhance);

            if (product_path.startsWith(PROTOCOL)) {
                if (product_path.length() > 0) {
                    mImageLoader = new ImageLoader<PhotoView>(this);
                    mImageLoader.DisplayImage(product_path, R.drawable.loader, product_image);
                }
            } else {
                int index = intent.getIntExtra("imgindex", 0);
                Bitmap bmp = FragmentHome.mFotos.get(index).getBitmap();
                if (bmp != null) {
                    product_image.setImageBitmap(bmp);
                }
                /*
                CameraManager camera = CameraManager.getInstance();
                Bitmap bmp = camera.getImageProduct();
                if (bmp != null) {
                    product_image.setImageBitmap(bmp);
                }
                */
            }

            TextView proCode = (TextView) findViewById(R.id.fullscreen_product_code);
            if (proCode != null) {
                if (product_name.length() == 0) {
                    proCode.setVisibility(View.GONE);
                    return;
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
