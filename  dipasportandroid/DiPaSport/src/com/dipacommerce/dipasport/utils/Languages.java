package com.dipacommerce.dipasport.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.views.HomePageActivity;

public class Languages implements View.OnClickListener {

    public static final String PREFS_LANGUAGES = "languages";

    private Activity mContext;
    private AlertDialog mLanguageDlg;
    private OnLanguageEventListener mOnLanguageSelected;
    private SharedPreferences mPrefs;

    public Languages(Activity context, OnLanguageEventListener onLanguageSelected) {
        mContext = context;
        mOnLanguageSelected = onLanguageSelected;
        mPrefs = mContext.getSharedPreferences("languages", Context.MODE_PRIVATE);
    }

    /**
     * 
     */
    public void chooseLanguage() {
        int language = mPrefs.getInt(PREFS_LANGUAGES, -1);
        if (language != -1) {
            DiPaSport.setLanguage(language);
            if(mOnLanguageSelected != null){
                mOnLanguageSelected.onLanguageSelected();
            }
        } else {
            chooseLanguage(true);
        }
        
    }

    /**
     * 
     * @param reset
     */
    public void chooseLanguage(boolean reset) {
        if (reset) {
            mContext.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mLanguageDlg = new AlertDialog.Builder(mContext).create();
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.languages, null);
                    view.findViewById(R.id.language_it).setOnClickListener(Languages.this);
                    view.findViewById(R.id.language_uk).setOnClickListener(Languages.this);
                    mLanguageDlg.setView(view);
                    mLanguageDlg.setTitle("Scegli la lingua\nSelect default language");
                    mLanguageDlg.setCancelable(false);
                    mLanguageDlg.show();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        SharedPreferences.Editor editor = mPrefs.edit();
        if (id == R.id.language_it) {
            if (mOnLanguageSelected != null) {
                mOnLanguageSelected.onITSelected();
            }
            editor.putInt(PREFS_LANGUAGES, DiPaSport.LANGUAGE_IT);
        } else {
            if (mOnLanguageSelected != null) {
                mOnLanguageSelected.onUKSelected();
            }
            editor.putInt(PREFS_LANGUAGES, DiPaSport.LANGUAGE_UK);
        }
        editor.commit();
        if (mLanguageDlg != null) {
            mLanguageDlg.dismiss();
        }

        if(mOnLanguageSelected != null){
            mOnLanguageSelected.onLanguageSelected();
        }
    }

    public abstract interface OnLanguageEventListener {
        public void onITSelected();

        public void onUKSelected();
        
        public void onLanguageSelected();
    }
}
