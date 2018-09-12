package org.aaa.chain.utils;

import android.content.Context;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class PBEUtils {

    private static PBEUtils instance;

    public static PBEUtils getInstance() {
        if (instance == null) {
            instance = new PBEUtils();
        }
        return instance;
    }

    /**
     * 定义加密方式
     * 支持以下任意一种算法
     * <p/>
     * <pre>
     * PBEWithMD5AndDES
     * PBEWithMD5AndTripleDES
     * PBEWithSHA1AndDESede
     * PBEWithSHA1AndRC2_40
     * </pre>
     */
    private final static String KEY_PBE = "PBEWITHMD5andDES";

    private final static int SALT_COUNT = 100;

    /**
     * 转换密钥
     *
     * @param key 字符串
     */
    private static Key stringToKey(String key) {
        SecretKey secretKey = null;
        try {
            PBEKeySpec keySpec = new PBEKeySpec(key.toCharArray());
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_PBE);
            secretKey = factory.generateSecret(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return secretKey;
    }

    public File encryptFile(Context context, String filePath, String privateKey, String publicKey) {
        byte[] bytes = FileUtils.getInstance().getBytes(filePath);
        byte[] enBytes = encryptPBE(bytes, privateKey, publicKey.getBytes());
        File file = new File(filePath);
        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String prefix = fileName.substring(0, fileName.lastIndexOf("."));
        return FileUtils.getInstance().getFile(enBytes, context.getExternalFilesDir("").getAbsolutePath(), prefix + "_encrypt." + suffix);
    }

    public File decryptFile(Context context, String filePath, String privateKey, String publicKey) {
        byte[] enbytes = FileUtils.getInstance().getBytes(filePath);
        byte[] deBytes = decryptPBE(enbytes, privateKey, publicKey.getBytes());
        File file = new File(filePath);
        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String prefix = fileName.substring(0, fileName.lastIndexOf("."));
        return FileUtils.getInstance().getFile(deBytes, context.getExternalFilesDir("").getAbsolutePath(), prefix + "_decrypt." + suffix);
    }

    /**
     * PBE 加密
     *
     * @param data 需要加密的字节数组
     * @param key 密钥
     * @param salt 盐
     */
    private static byte[] encryptPBE(byte[] data, String key, byte[] salt) {
        byte[] bytes = null;
        try {
            // 获取密钥
            Key k = stringToKey(key);
            PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, SALT_COUNT);
            Cipher cipher = Cipher.getInstance(KEY_PBE);
            cipher.init(Cipher.ENCRYPT_MODE, k, parameterSpec);
            bytes = cipher.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * PBE 解密
     *
     * @param data 需要解密的字节数组
     * @param key 密钥
     * @param salt 盐
     */
    public static byte[] decryptPBE(byte[] data, String key, byte[] salt) {
        byte[] bytes = null;
        try {
            // 获取密钥
            Key k = stringToKey(key);
            PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, SALT_COUNT);
            Cipher cipher = Cipher.getInstance(KEY_PBE);
            cipher.init(Cipher.DECRYPT_MODE, k, parameterSpec);
            bytes = cipher.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * BASE64 加密
     *
     * @param key 需要加密的字节数组
     * @return 字符串
     * @throws Exception
     */
    public static String encryptBase64(byte[] key) {
        return Base58.encode(key);
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        // 加密前的原文
        String str = "hello world !!!";
        // 口令
        String key = "qwert";
        // 初始化盐
        byte[] salt = key.getBytes();
        // 采用PBE算法加密
        byte[] encData = encryptPBE(str.getBytes(), key, salt);
        // 采用PBE算法解密
        byte[] decData = decryptPBE(encData, key, salt);
        String encStr = null;
        String decStr = null;
        try {
            encStr = encryptBase64(encData);
            decStr = new String(decData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("加密前：" + str);
        System.out.println("加密后：" + encStr);
        System.out.println("解密后：" + decStr);
    }
}