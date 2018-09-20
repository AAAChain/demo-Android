package org.aaa.chain.utils;

import android.content.Context;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.interfaces.ECPrivateKey;
import org.spongycastle.jce.interfaces.ECPublicKey;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.jce.spec.ECPrivateKeySpec;
import org.spongycastle.jce.spec.ECPublicKeySpec;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECPoint;

public class FileUtils {

    private static ECPublicKey publicKey;
    private static ECPrivateKey privateKey;

    private static FileUtils instance;

    public static FileUtils getInstance() {
        if (instance == null) {
            instance = new FileUtils();
        }
        return instance;
    }

    static {
        BouncyCastleProvider bouncyCastleProvider = new org.spongycastle.jce.provider.BouncyCastleProvider();
        Security.insertProviderAt(bouncyCastleProvider, 1);
    }

    public static KeyPair genKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECIES", "SC");
        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256k1");
        try {
            keyPairGenerator.initialize(ecGenParameterSpec, new SecureRandom());
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return keyPairGenerator.generateKeyPair();
    }

    public void testECC() {

        //for (Provider provider : Security.getProviders()) {
        //    System.out.println("Provider: " + provider.getName() + " version: " + provider.getVersion());
        //    for (Provider.Service service : provider.getServices()) {
        //        System.out.printf("  Type : %-30s  Algorithm: %-30s\n", service.getType(), service.getAlgorithm());
        //    }
        //}
        //System.out.println("------------------------------------------------------------------------------------------\n\n");

        try {
            KeyPair keyPair = genKeyPair();
            publicKey = (ECPublicKey) keyPair.getPublic();
            privateKey = (ECPrivateKey) keyPair.getPrivate();

            System.out.println("public key:" + publicKey);
            System.out.println("private key:" + privateKey);

            //打印密钥信息
            ECCurve ecCurve = publicKey.getParameters().getCurve();
            System.out.println("椭圆曲线参数a = " + ecCurve.getA().toBigInteger());
            System.out.println("椭圆曲线参数b = " + ecCurve.getB().toBigInteger());
            System.out.println("椭圆曲线参数q = " + ((ECCurve.Fp) ecCurve).getQ());
            ECPoint basePoint = publicKey.getParameters().getG();
            System.out.println("基点橫坐标              " + basePoint.getAffineXCoord().toBigInteger());
            System.out.println("基点纵坐标              " + basePoint.getAffineYCoord().toBigInteger());
            System.out.println("公钥横坐标              " + publicKey.getQ().getAffineXCoord().toBigInteger());
            System.out.println("公钥纵坐标              " + publicKey.getQ().getAffineYCoord().toBigInteger());
            System.out.println("私钥                   " + privateKey.getD());

            ECPrivateKey ecPrivateKey = decodePrivateKey("5JtXJkmDcMhKAXSB3YonH4jCtNbSELUAPGpExZhn8upLs54oej7");
            ECPublicKey ecPublicKey = decodePublicKey(ecPrivateKey);
            System.out.println("ecPrivateKey-----" + ecPrivateKey);
            System.out.println("ecpublickey-----" + ecPublicKey);
            byte[] encryptContent = encrypt("hello ni hao".getBytes(), ecPublicKey);
            System.out.println("encrypt content:" + new String(encryptContent));
            byte[] decryptContent = decrypt(encryptContent, ecPrivateKey);
            System.out.println("decrypt content:" + new String(decryptContent));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

    }

    public File encryptFile(Context context, String filePath, String privateKey)
            throws FileNotFoundException, NoSuchProviderException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchPaddingException {
        byte[] bytes = getBytes(filePath);
        ECPrivateKey ecPrivateKey = decodePrivateKey(privateKey);
        ECPublicKey ecPublicKey = decodePublicKey(ecPrivateKey);
        byte[] enBytes = encryptFileBytes(bytes, ecPublicKey);
        File file = new File(filePath);
        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        return getFile(enBytes, context.getExternalCacheDir().getAbsolutePath(), "temp." + suffix);
    }

    public File decryptFile(Context context, String filePath, String privateKey)
            throws NoSuchProviderException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
            NoSuchPaddingException {
        byte[] enbytes = getBytes(filePath);
        ECPrivateKey ecPrivateKey = decodePrivateKey(privateKey);
        byte[] deBytes = decryptFileBytes(enbytes, ecPrivateKey);

        File file = new File(filePath);
        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        return getFile(deBytes, context.getExternalFilesDir("").getAbsolutePath(), "test." + suffix);
    }

    public static ECPublicKey decodePublicKey(ECPrivateKey ecPrivateKey) throws NoSuchProviderException, NoSuchAlgorithmException {
        ECNamedCurveParameterSpec ecNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(ecPrivateKey.getParameters().getG(), ecPrivateKey.getParameters());
        ECPublicKey ecPublicKey = null;
        try {
            ecPublicKey = (ECPublicKey) KeyFactory.getInstance("EC", "SC").generatePublic(ecPublicKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return ecPublicKey;
    }

    public static ECPrivateKey decodePrivateKey(String keyStr) throws NoSuchProviderException, NoSuchAlgorithmException {

        ECNamedCurveParameterSpec ecNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        BigInteger bigInteger = new BigInteger(1, Base58.decode(keyStr));
        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(bigInteger, ecNamedCurveParameterSpec);

        ECPrivateKey ecPrivateKey = null;
        try {
            ecPrivateKey = (ECPrivateKey) KeyFactory.getInstance("EC", "SC").generatePrivate(ecPrivateKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return ecPrivateKey;
    }

    private byte[] encryptFileBytes(byte[] fileBytes, ECPublicKey ecPublicKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("ECIESwithDESede/NONE/PKCS7Padding", "SC");
        cipher.init(Cipher.ENCRYPT_MODE, ecPublicKey);

        return cipher.doFinal(fileBytes);
    }

    private byte[] decryptFileBytes(byte[] fileBytes, ECPrivateKey ecPrivateKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            NoSuchProviderException {

        Cipher cipher = Cipher.getInstance("ECIESwithDESede/NONE/PKCS7Padding", "SC");
        cipher.init(Cipher.DECRYPT_MODE, ecPrivateKey);

        return cipher.doFinal(fileBytes);
    }

    public static byte[] encrypt(byte[] content, ECPublicKey ecPublicKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("ECIES", "SC");
        cipher.init(Cipher.ENCRYPT_MODE, ecPublicKey);
        byte[] cipherText = cipher.doFinal(content);
        System.out.println("密文: " + cipherText);

        return cipherText;
    }

    public static byte[] decrypt(byte[] content, ECPrivateKey ecPrivateKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            NoSuchProviderException {

        Cipher cipher = Cipher.getInstance("ECIES", "SC");
        cipher.init(Cipher.DECRYPT_MODE, ecPrivateKey);
        byte[] plainText = cipher.doFinal(content);
        // 打印解密后的明文
        System.out.println("解密后的明文: " + new String(plainText));

        return plainText;
    }

    /**
     * 获得指定文件的byte数组
     */

    public byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 根据byte数组，生成文件
     */
    public File getFile(byte[] buffer, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + "/" + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return file;
    }


    public static void main(String[] args ){
        jdkECDSA();
    }


    public static void jdkECDSA(){
        //1、初始化密钥
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
            keyPairGenerator.initialize(256);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            ECPublicKey ecPublicKey = (ECPublicKey) keyPair.getPublic();
            ECPrivateKey ecPrivateKey = (ECPrivateKey) keyPair.getPrivate();

            //2、执行签名
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(ecPrivateKey.getEncoded());
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Signature signature = Signature.getInstance("SHA1withECDSA");
            signature.initSign(privateKey);
            signature.update("securtity ECDSA".getBytes());
            byte[] result = signature.sign();
            System.out.println("jdk ecdsa sign:" + new String(result));

            //3、验证签名
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(ecPublicKey.getEncoded());
            keyFactory = KeyFactory.getInstance("EC");
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            signature = Signature.getInstance("SHA1withECDSA");
            signature.initVerify(publicKey);
            signature.update("securtity ECDSA".getBytes());
            boolean bool = signature.verify(result);
            System.out.println(bool);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}