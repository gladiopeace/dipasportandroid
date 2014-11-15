package com.dipacommerce.dipasport.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dipacommerce.dipasport.R;

public class NetworkUtils {
    public static boolean isConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
         
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                              activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    
    
    public static void showAlertNoNetworkConnection(Context context){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(context.getResources().getString(R.string.no_network_connection));
        dialog.setPositiveButton("Ok", null);
        dialog.create().show();
    }
}
