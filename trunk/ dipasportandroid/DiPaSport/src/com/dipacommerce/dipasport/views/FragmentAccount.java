package com.dipacommerce.dipasport.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;

public class FragmentAccount extends DiPaSport<Object, Object> {
    private View mRootView;

    public FragmentAccount() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_account_page, container, false);

        UpdateViews(mRootView);

        return mRootView;
    }

    @Override
    public void UpdateViews(View rootView) {
        Button logout = (Button) rootView.findViewById(R.id.account_logout);
        logout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                final AlertDialog.Builder dialog = new AlertDialog.Builder(sCtx);
                dialog.setTitle(sCtx.getString(R.string.str_logout_title));
                dialog.setMessage(sCtx.getString(R.string.str_logout_confirm));

                dialog.setPositiveButton(sCtx.getString(R.string.str_dialog_ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (sCustomer.isLogin()) {
                            sCustomer.logout();

                            FragmentManager fm = getFragmentManager();
                            fm.beginTransaction().replace(R.id.tab_account, sLoginFragment).commit();
                            TabsFragment.setTabSelected(TabsFragment.TAB_ACCOUNT_INDEX);
                        }
                    }
                });
                dialog.setNegativeButton(sCtx.getString(R.string.str_dialog_cancel), null);

                dialog.create().show();

            }
        });

        TextView welcome = (TextView) rootView.findViewById(R.id.account_welcome);
        String welcomeMsg = String.format("%s, %s", sCtx.getString(R.string.str_welcome), sCustomer.getCustomerInfo().getEmail());
        welcome.setText(welcomeMsg);
    }
}
