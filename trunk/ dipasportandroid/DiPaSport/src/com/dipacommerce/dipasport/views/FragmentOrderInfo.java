package com.dipacommerce.dipasport.views;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;

public class FragmentOrderInfo extends DiPaSport<Object, Object> {
    public static final String ORDER_ID_NUMBER = "orderid";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BuildActionBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_completed, container, false);
        UpdateViews(rootView);
        return rootView;
    }

    @Override
    public void UpdateViews(View rootView) {

        Bundle bundle = getArguments();
        String orderNumber = "";
        if (bundle != null) {
            orderNumber = bundle.getString(ORDER_ID_NUMBER);
        }

        TextView orderid = (TextView) rootView.findViewById(R.id.order_idnumber);
        if (orderid != null) {
            orderid.setText(getOrderIdFormatter(orderNumber));
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
        fm.beginTransaction().replace(R.id.tab_shoppingcart, sCartFragment).commit();
    }

    private String getOrderIdFormatter(String orderid) {
        return String.format("%s: %s", sCtx.getString(R.string.str_order_id_number), orderid);
    }

}
