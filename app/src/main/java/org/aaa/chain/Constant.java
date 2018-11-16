package org.aaa.chain;

public class Constant {

    private static String currentAccount;
    private static String currentPrivateKey;
    private static String currentPublicKey;
    private static String password;

    public static String getCurrentAccount() {
        return currentAccount;
    }

    public static void setCurrentAccount(String currentAccount) {
        Constant.currentAccount = currentAccount;
    }

    public static String getCurrentPrivateKey() {
        return currentPrivateKey;
    }

    public static void setCurrentPrivateKey(String currentPrivateKey) {
        Constant.currentPrivateKey = currentPrivateKey;
    }

    public static String getCurrentPublicKey() {
        return currentPublicKey;
    }

    public static void setCurrentPublicKey(String currentPublicKey) {
        Constant.currentPublicKey = currentPublicKey;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Constant.password = password;
    }

}
