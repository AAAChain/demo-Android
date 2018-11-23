package org.aaa.chain.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {

    static private PreferenceUtils instance = null;
    static private String IS_FIRST = "isfirst";

    private SharedPreferences pref = null;
    private SharedPreferences.Editor editor = null;

    static public PreferenceUtils getInstance() {
        return instance;
    }

    @SuppressLint("CommitPrefEdits") public static void init(Context context) {
        instance = new PreferenceUtils();
        String PREFERENCE_NAME = "aaainfo";
        instance.pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        instance.editor = instance.pref.edit();
    }

    public boolean getIsFirst() {
        return pref.getBoolean(IS_FIRST, true);
    }

    public synchronized void setIsFirst(boolean isFirst) {
        editor.putBoolean(IS_FIRST, isFirst);
        editor.commit();
    }
}
