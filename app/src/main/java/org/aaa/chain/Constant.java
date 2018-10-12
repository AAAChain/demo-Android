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
            publicKey = "AAA7RccFsFi5NqgDQerEYRJ7odQ5EX135N1kD4v1hxKAfrCadKxp3";
        } else {
            publicKey = "AAA5HZLkL5tat8aEa8egckNNeuFHWvLSCvC5v2v7N8WPSpQT2FzPM";
        }

        return publicKey;
    }

    public static String getAnotherPublicKey() {
        if (ChainApplication.getInstance().isAccount1) {
            publicKey = "AAA5HZLkL5tat8aEa8egckNNeuFHWvLSCvC5v2v7N8WPSpQT2FzPM";
        } else {
            publicKey = "AAA7RccFsFi5NqgDQerEYRJ7odQ5EX135N1kD4v1hxKAfrCadKxp3";
        }

        return publicKey;
    }

    public static String getPrivateKey() {
        if (ChainApplication.getInstance().isAccount1) {
            privateKey = "5JWYoMqLxGAmHi5BnhYRSdaTpNsF4jzcUCgKq57LMHqHqnCGJn4";
        } else {
            privateKey = "5KZyaoA9W2N6CP7EDoYBXXVMySVm1ZswVte2beByL2SD1C1cnEk";
        }

        return privateKey;
    }

    public static String getAnotherPrivateKey() {
        if (ChainApplication.getInstance().isAccount1) {
            privateKey = "5KZyaoA9W2N6CP7EDoYBXXVMySVm1ZswVte2beByL2SD1C1cnEk";
        } else {
            privateKey = "5JWYoMqLxGAmHi5BnhYRSdaTpNsF4jzcUCgKq57LMHqHqnCGJn4";
        }

        return privateKey;
    }
}
