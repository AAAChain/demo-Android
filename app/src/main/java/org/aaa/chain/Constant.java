package org.aaa.chain;

public class Constant {

    public static String account;
    public static String privateKey;
    public static String publicKey;

    public static String getAccount() {
        if (ChainApplication.getInstance().isAccount1) {
            account = "aaauser1";
        } else {
            account = "aaauser2";
        }

        return account;
    }

    public static String getAnotherAccount() {
        if (ChainApplication.getInstance().isAccount1) {
            account = "aaauser2";
        } else {
            account = "aaauser1";
        }

        return account;
    }

    public static String getPublicKey() {
        if (ChainApplication.getInstance().isAccount1) {
            publicKey = "EOS5YQkZpsD8xzmguAGL88r2b4RBXxKbV3QGjS8Vg1maBzd8Df9zX";
        } else {
            publicKey = "EOS5SW9SkzUuTwhbJMsFDRge7cgKoTABiQ6ac3DrDux2UjjpKbm1h";
        }

        return publicKey;
    }

    public static String getAnotherPublicKey() {
        if (ChainApplication.getInstance().isAccount1) {
            publicKey = "EOS5SW9SkzUuTwhbJMsFDRge7cgKoTABiQ6ac3DrDux2UjjpKbm1h";
        } else {
            publicKey = "EOS5YQkZpsD8xzmguAGL88r2b4RBXxKbV3QGjS8Vg1maBzd8Df9zX";
        }

        return publicKey;
    }

    public static String getPrivateKey() {
        if (ChainApplication.getInstance().isAccount1) {
            privateKey = "5JobQnxtEvshVRZW6berfYvzaUMZq2A8Ax5eZhuZqdTCqT19iLV";
        } else {
            privateKey = "5J4a77MxGSDnASAZHAV7gThSeoenvLB4nb8wFPkepXoiLyesuf5";
        }

        return privateKey;
    }

    public static String getAnotherPrivateKey() {
        if (ChainApplication.getInstance().isAccount1) {
            privateKey = "5J4a77MxGSDnASAZHAV7gThSeoenvLB4nb8wFPkepXoiLyesuf5";
        } else {
            privateKey = "5JobQnxtEvshVRZW6berfYvzaUMZq2A8Ax5eZhuZqdTCqT19iLV";
        }

        return privateKey;
    }
}
