package com.mobilelearning.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkUtil {
    
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                return true;
            }
        }
        return false;
    }
    
    public static void checkNetwork(Context context) {
        if (!isNetworkAvailable(context)) {
            Toast.makeText(context, "网络连接不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
        }
    }
}
