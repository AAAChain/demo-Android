package org.aaa.chain;

import android.app.Application;
import android.content.Context;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class ChainApplication extends Application {

    /**
     * aaauser1
     *
     * Owner key:
     * Private key: 5Jtsaf2tyLGBTJER4bCBV5pvKiAE4v3v3G4agfQ91DkfLHRSLzX
     * Public key: EOS5odhW8Tw51UetwxnjVMRKv6Eq4SPn6UqidCt57HYN5KjymsA2v
     *
     * Active Key
     * Private key: 5JobQnxtEvshVRZW6berfYvzaUMZq2A8Ax5eZhuZqdTCqT19iLV
     * Public key: EOS5YQkZpsD8xzmguAGL88r2b4RBXxKbV3QGjS8Vg1maBzd8Df9zX
     *
     * ################################################################################
     * aaauser2
     *
     * Owner key:
     * Private key: 5KejiFM4Qq1kshgtShE5n9bVLf63AmVhkReMYjhJPChcoKAog1c
     * Public key: EOS7gqLCvRQFxLfbT2LMbzQQwAngToB3YApob74zgsZ7VN7vUBUtX
     *
     * Active Key
     * Private key: 5J4a77MxGSDnASAZHAV7gThSeoenvLB4nb8wFPkepXoiLyesuf5
     * Public key: EOS5SW9SkzUuTwhbJMsFDRge7cgKoTABiQ6ac3DrDux2UjjpKbm1h
     **/

    public static String account1 = "aaauser1";
    public static String privateKey1 = "5JobQnxtEvshVRZW6berfYvzaUMZq2A8Ax5eZhuZqdTCqT19iLV";
    public static String publicKey1 = "EOS5YQkZpsD8xzmguAGL88r2b4RBXxKbV3QGjS8Vg1maBzd8Df9zX";

    public static String account2 = "aaauser2";
    public static String privateKey2 = "5J4a77MxGSDnASAZHAV7gThSeoenvLB4nb8wFPkepXoiLyesuf5";
    public static String publicKey2 = "EOS5SW9SkzUuTwhbJMsFDRge7cgKoTABiQ6ac3DrDux2UjjpKbm1h";

    private RefWatcher refWatcher;
    private static ChainApplication instance;
    public String balance;

    public String signature;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getBalance() {
        return balance;
    }


    public boolean isAccount1 = false;

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
