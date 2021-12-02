package com.example.texnika;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import com.orhanobut.hawk.Hawk;

import java.util.Locale;

public class LocaleManager {

    public static Context setLocale(Context mContext){
        Log.d("prefs",getLanguagePref(mContext));
        return updataResources(mContext,getLanguagePref(mContext));
    }

    public static Context setNewLocale(Context mContext, String language){
        setLanguagePref(mContext,language);
        return updataResources(mContext,language);
    }



    private static String getLanguagePref(Context mContext) {
        return Hawk.get("pref_lang","en");
    }

    private static void setLanguagePref(Context context, String localeKey) {
      
        Hawk.put("pref_lang",localeKey);
    }

    private static Context updataResources(Context mContext, String languagePref) {
        Locale locale = new Locale(languagePref);
        Locale.setDefault(locale);

        Resources res = mContext.getResources();

        Configuration configuration = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            configuration.setLocale(locale);
            mContext = mContext.createConfigurationContext(configuration);
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        } else {
            configuration.locale = locale;
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }
        return mContext;

    }

    public static Locale getLocale(Resources res){
        Configuration config = res.getConfiguration();
        return Build.VERSION.SDK_INT >= 24 ?config.getLocales().get(0):config.locale;
    }
}
