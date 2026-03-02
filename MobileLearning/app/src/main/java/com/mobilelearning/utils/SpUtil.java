package com.mobilelearning.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {
    
    private static final String SP_NAME = "mobile_learning";
    private static SpUtil instance;
    private SharedPreferences sp;
    
    private SpUtil(Context context) {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }
    
    public static SpUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (SpUtil.class) {
                if (instance == null) {
                    instance = new SpUtil(context.getApplicationContext());
                }
            }
        }
        return instance;
    }
    
    public void putString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }
    
    public String getString(String key) {
        return sp.getString(key, "");
    }
    
    public void putLong(String key, long value) {
        sp.edit().putLong(key, value).apply();
    }
    
    public long getLong(String key) {
        return sp.getLong(key, 0);
    }
    
    public void putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }
    
    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }
    
    public void remove(String key) {
        sp.edit().remove(key).apply();
    }
    
    public void clear() {
        sp.edit().clear().apply();
    }
}
