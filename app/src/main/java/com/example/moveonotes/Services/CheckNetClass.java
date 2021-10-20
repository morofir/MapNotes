package com.example.moveonotes.Services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetClass {

    public static Boolean checknetwork(Context mContext) {

        NetworkInfo info = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return false;
        }
        // here is the roaming option, you can change it if you want to
        // disable internet while roaming, just return false
        info.isRoaming();

        return true;

    }
}