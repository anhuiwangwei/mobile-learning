package com.mobilelearning;

import android.app.Application;

public class MainApplication extends Application {
    
    public static final String BASE_URL = "http://10.0.2.2:8080/";
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
