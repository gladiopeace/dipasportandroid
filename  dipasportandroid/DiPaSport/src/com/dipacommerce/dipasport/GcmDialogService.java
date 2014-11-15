package com.dipacommerce.dipasport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.dipacommerce.dipasport.views.HomePageActivity;
import com.dipacommerce.dipasport.views.SplashScreenActivity;

public class GcmDialogService extends Activity {
    private static final String uk_open = "Open";
    private static final String uk_close = "Close";
    
    private static final String it_open = "Apri";
    private static final String it_close = "Chiudi";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent params = getIntent();
        if (params != null) {
            Bundle bundle = params.getBundleExtra("params");
            if (bundle != null) {
                
                SharedPreferences prefs = getSharedPreferences(DiPaSport.class.getPackage().getName(), Context.MODE_PRIVATE);
                int lang_id = 0;
                if(prefs != null){
                    lang_id = prefs.getInt("language", 0);
                }
                
                String open_name = lang_id == 0 ? it_open : uk_open;
                String close_name = lang_id == 0 ? it_close : uk_close;
                
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(getResources().getString(R.string.str_contact_company));
                alertDialog.setMessage(lang_id != 0 ? bundle.getString("msg-uk") : bundle.getString("msg-it"));
                alertDialog.setIcon(R.drawable.ic_launcher);
                alertDialog.setPositiveButton(open_name, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent home = new Intent(GcmDialogService.this, SplashScreenActivity.class);
                        startActivity(home);
                    }
                });
                alertDialog.setNegativeButton(close_name, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.create().show();
            }
        }
    }
}
