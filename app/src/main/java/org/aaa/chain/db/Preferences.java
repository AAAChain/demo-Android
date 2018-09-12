package org.aaa.chain.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.Locale;
import java.util.Random;

public class Preferences {

    static private Preferences instance = null;
    static private String PREFERENCE_NAME = "keypair";
    static private String PUBLIC_KEY = "publickey";
    static private String PRIVATE_KEY = "privatekey";
    static private String IS_HAVE_KEYPAIR = "is_have_keypair";

    private SharedPreferences pref = null;
    private SharedPreferences.Editor editor = null;

    static public Preferences getInstance() {
        return instance;
    }

    @SuppressLint("CommitPrefEdits") public static void init(Context context) {
        instance = new Preferences();
        instance.pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        instance.editor = instance.pref.edit();
    }

    public synchronized String getPublicKey() {
        return pref.getString(PUBLIC_KEY, null);
    }

    public synchronized void setPublicKey(String publicKey) {
        editor.putString(PUBLIC_KEY, publicKey);
        editor.commit();
    }

    public void setPrivateKey(String privateKey) {
        editor.putString(PRIVATE_KEY, privateKey);
        editor.commit();
    }

    public String getPrivateKey() {
        return pref.getString(PRIVATE_KEY, null);
    }

    public void setIsHaveKeypair(boolean isHave) {
        editor.putBoolean(IS_HAVE_KEYPAIR, isHave);
        editor.commit();
    }

    public boolean getIsHaveKeypair() {
        return pref.getBoolean(IS_HAVE_KEYPAIR, false);
    }
}
