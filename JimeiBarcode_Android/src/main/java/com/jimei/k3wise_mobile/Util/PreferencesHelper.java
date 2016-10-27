package com.jimei.k3wise_mobile.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.jimei.k3wise_mobile.SettingsActivity;

/**
 * Created by lee on 2016/9/11.
 */
public class PreferencesHelper {
    Context context;

    public PreferencesHelper(Context context) {
        this.context = context;
    }

    public void Save(String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences("jimei", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String Get(String key){
        SharedPreferences preferences = context.getSharedPreferences("jimei", context.MODE_PRIVATE);
        return preferences.getString(key,"");
    }

    public static String Get(Context context,String key) {
        try {
            Context c = context.createPackageContext("com.jimei.k3wise_mobile", Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
            return preferences.getString(key, "");
        }catch (Exception ex){
            ShowDialog.ExceptionDialog(context, ex.getMessage());
            return "";
        }
    }

    public static void Save(Context context, String key, String value) {
        try {
            Context c = context.createPackageContext("com.jimei.k3wise_mobile", Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.commit();
        }catch (Exception ex) {
            ShowDialog.ExceptionDialog(context, ex.getMessage());
        }
    }
}
