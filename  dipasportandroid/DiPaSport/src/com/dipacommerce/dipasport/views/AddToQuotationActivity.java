package com.dipacommerce.dipasport.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.dipacommerce.dipasport.R;

public class AddToQuotationActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_quotation);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String productId = bundle.getString(FragmentQuotation.PRODUCT_ID);
            FragmentManager fm = getSupportFragmentManager();
            FragmentQuotation quote = new FragmentQuotation();
            Bundle b = new Bundle();
            b.putString(FragmentQuotation.PRODUCT_ID, productId);
            quote.setArguments(b);

            fm.beginTransaction().add(R.id.quotation_container, quote).commit();
        }
    }

}
