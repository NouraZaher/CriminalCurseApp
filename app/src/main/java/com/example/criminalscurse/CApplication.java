package com.example.criminalscurse;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;


public class CApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        String lan=sharedPreferences.getString("lan","English (en)");
        LocaleUtils.setPrefLangCode(this,getISOCode(lan));
        LocaleUtils.setLocale(new Locale(LocaleUtils.getPrefLangCode(this)));
        LocaleUtils.updateConfiguration(this, getResources().getConfiguration());
    }
    public static String getISOCode(String lang) {
return lang.substring(lang.indexOf("(")+1,lang.indexOf(")"));
    }
}
