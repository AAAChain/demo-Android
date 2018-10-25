package org.aaa.chain.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {

    static private PreferenceUtils instance = null;
    static private String KEY_ACCOUNT = "account";
    static private String KEY_PASSWORD = "password";
    static private String KEY_PRIVATE_KEY = "private_key";
    static private String KEY_PUBLIC_KEY = "public_key";

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

    public String getAccount() {
        return pref.getString(KEY_ACCOUNT, null);
    }

    public synchronized void setAccount(String account) {
        editor.putString(KEY_ACCOUNT, account);
        editor.commit();
    }

    public String getPassword() {
        return pref.getString(KEY_PASSWORD, null);
    }

    public synchronized void setPassword(String password) {
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }

    public String getPrivateKey() {
        return pref.getString(KEY_PRIVATE_KEY, null);
    }

    public synchronized void setPrivateKey(String privateKey) {
        editor.putString(KEY_PRIVATE_KEY, privateKey);
        editor.commit();
    }

    public String getPublicKey() {
        return pref.getString(KEY_PUBLIC_KEY, null);
    }

    public synchronized void setPublicKey(String publicKey) {
        editor.putString(KEY_PUBLIC_KEY, publicKey);
        editor.commit();
    }
}
