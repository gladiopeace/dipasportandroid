package com.dipacommerce.dipasport.views;

import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.controls.SplashScreen;
import com.dipacommerce.dipasport.utils.Languages;
import com.dipacommerce.dipasport.utils.NetworkUtils;

public class SplashScreenActivity extends SplashScreen implements SplashScreen.Callback,
        Languages.OnLanguageEventListener {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        
        setContentView(R.layout.activity_splash_screen);
        mContext = this;
        RegisterCallback(this);
        setCancelable(false);
        DiPaSport.initContext(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mTimeDuration = 3; // 3s
        StartSplashScreen();
    }

    @Override
    public void onTimeOut() {
        new Languages(this, this).chooseLanguage();
    }

    public void onITSelected() {
        DiPaSport.setLanguage(DiPaSport.LANGUAGE_IT);
    }

    @Override
    public void onUKSelected() {
        DiPaSport.setLanguage(DiPaSport.LANGUAGE_UK);
    }
    
    public void onLanguageSelected() {
        finish();
        Intent homepage = new Intent(this, HomePageActivity.class);
        homepage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homepage);
    };
}