package com.hungdt.qrcode.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MySetting {
    public static final String MAX_LENGTH = "maxLength";
    public static final String COUNT = "count";
    public static final String CONFIG_GG_FB = "dfhhddfhdf";
    public static final String SETTINGS = "ggggdfgdfhfgs";
    public static final String KEY_REMOVE_ADS = "ncvdnrgdcn";
    public static final String KEY_RATE_APP = "yhfghhnrdffndxcx";
    public static final String KEY_SUBSCRIPTION = "nrcvfdbnbre";


    public static int getCount(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return preferences.getInt(COUNT, 0);
    }

    public static void setCount(Context context, int count) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(COUNT, count);
        editor.apply();
    }

    public static int getMaxLength(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return preferences.getInt(MAX_LENGTH, 3);
    }

    public static void setMaxLength(Context context, int maxLength) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(MAX_LENGTH, maxLength);
        editor.apply();
    }

    //Put = set Giá trị
    public static void putConfigGgFb(Context context, long value) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(CONFIG_GG_FB, value);
        editor.apply();
    }

    //Get
    public static long getConfigGgFb(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return preferences.getLong(CONFIG_GG_FB, 0);
    }

    public static void setSubscription(Context context, boolean isSub) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_SUBSCRIPTION, isSub);
        editor.apply();
    }

    public static boolean isSubscription(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_SUBSCRIPTION, false);
    }
    public static int isRateApp(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_RATE_APP, 0);
    }

    public static boolean putRateApp(Context context, int value) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_RATE_APP, value);
        return editor.commit();
    }

    public static void putRemoveAds(Context context, boolean removeAds) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_REMOVE_ADS, removeAds);
        editor.apply();
    }

    public static boolean isRemoveAds(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_REMOVE_ADS, false);
    }


}
