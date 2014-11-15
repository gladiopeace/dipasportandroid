package com.dipacommerce.dipasport.views;

import java.io.IOException;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.data.Constants;
import com.dipacommerce.dipasport.utils.Languages;
import com.dipacommerce.dipasport.utils.NetworkUtils;
import com.dipacommerce.dipasport.utils.camera.CameraManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class HomePageActivity extends FragmentActivity implements Languages.OnLanguageEventListener {
    public boolean isPostBack = false;
    public boolean isCaptureProduct = false;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateLanguage();

        setContentView(R.layout.activity_main);

        mContext = this;

        DiPaSport.clearSearch();

        if (Constants.DEBUG_MODE) {
            Toast.makeText(this, "[You are running in DEBUG mode]", Toast.LENGTH_LONG).show();
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new TabsFragment()).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLanguage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedImage) {
        updateLanguage();
        CameraManager camera = CameraManager.getInstance();
        camera.processDataImage(requestCode, resultCode, imageReturnedImage);
        super.onActivityResult(requestCode, resultCode, imageReturnedImage);
    }

    @Override
    public void onPostResume() {
        super.onPostResume();
        updateLanguage();
        if (isPostBack) {
            TabsFragment.setTabSelected(TabsFragment.TAB_SEARCH_INDEX);
            isPostBack = false;// reset it
        }

        if (isCaptureProduct) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.tab_home, new FragmentSubmitProduct()).commit();
            isCaptureProduct = false;
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(getString(R.string.str_confirm_exit_app));
        dialog.setPositiveButton(getString(R.string.str_dialog_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        dialog.setNegativeButton(getString(R.string.str_dialog_cancel), null);

        dialog.create().show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.languages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new Languages(this, this).chooseLanguage(true);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUKSelected() {
        DiPaSport.setLanguage(DiPaSport.LANGUAGE_UK);
        showAlertRestartApplication();
    }

    @Override
    public void onITSelected() {
        DiPaSport.setLanguage(DiPaSport.LANGUAGE_IT);
        showAlertRestartApplication();
    }

    @Override
    public void onLanguageSelected() {
        // Nothing
    }

    private void showAlertRestartApplication() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Press OK to restart application.");
        alert.setCancelable(false);
        alert.setPositiveButton(R.string.str_dialog_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // finish();
                Intent splashscreen = new Intent(HomePageActivity.this, SplashScreenActivity.class);
                // splashscreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                // Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(splashscreen);
            }
        });
        alert.setNegativeButton(getString(R.string.str_dialog_cancel), null);
        alert.create().show();
    }

    private void updateLanguage() {
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
    }

    public void startCameraIntent(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }


}
