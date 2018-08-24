package org.aaa.chain;

import android.app.Application;
import android.content.Context;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class ChainApplication extends Application {

    private RefWatcher refWatcher;
    private static ChainApplication instance;
    public String balance;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public static ChainApplication getInstance() {
        return instance;
    }

    @Override public void onCreate() {
        super.onCreate();

        instance = this;

        refWatcher = setupLeakCanary();
    }

    private RefWatcher setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        ChainApplication leakApplication = (ChainApplication) context.getApplicationContext();
        return leakApplication.refWatcher;
    }
}
