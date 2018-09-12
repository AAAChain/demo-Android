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
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

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
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

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
        } catch (Exception e) {
            System.out.println("无法初始化算法" + e);
        }
    }

    public static KeyPair genKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECIES", "BC");
        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256k1");
        try {
            keyPairGenerator.initialize(ecGenParameterSpec, new SecureRandom());
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return keyPairGenerator.generateKeyPair();
    }

    public static void main(String[] args) {
        try {

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

    private static ECPublicKey decodePublicKey(ECPrivateKey ecPrivateKey) throws NoSuchProviderException, NoSuchAlgorithmException {
        ECNamedCurveParameterSpec ecNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(ecPrivateKey.getParameters().getG(), ecNamedCurveParameterSpec);
        ECPublicKey ecPublicKey = null;
        try {
            ecPublicKey = (ECPublicKey) KeyFactory.getInstance("EC", "BC").generatePublic(ecPublicKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        System.out.println("ecPublicKey:" + ecPublicKey);
        return ecPublicKey;
    }

    private static ECPrivateKey decodePrivateKey(String keyStr) throws NoSuchProviderException, NoSuchAlgorithmException {

        ECNamedCurveParameterSpec ecNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        BigInteger bigInteger = new BigInteger(1, Base58.decode(keyStr));
        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(bigInteger, ecNamedCurveParameterSpec);

        ECPrivateKey ecPrivateKey = null;
        try {
            ecPrivateKey = (ECPrivateKey) KeyFactory.getInstance("EC", "BC").generatePrivate(ecPrivateKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        System.out.println("ecPrivateKey:" + ecPrivateKey);
        return ecPrivateKey;
    }

    private byte[] encryptFileBytes(byte[] fileBytes, ECPublicKey ecPublicKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("ECIESwithDESede/NONE/PKCS7Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, ecPublicKey);

        return cipher.doFinal(fileBytes);
    }

    private byte[] decryptFileBytes(byte[] fileBytes, ECPrivateKey ecPrivateKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            NoSuchProviderException {

        Cipher cipher = Cipher.getInstance("ECIESwithDESede/NONE/PKCS7Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, ecPrivateKey);

        return cipher.doFinal(fileBytes);
    }

    private static byte[] encrypt(byte[] content, ECPublicKey ecPublicKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("ECIESwithDESede/NONE/PKCS7Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, ecPublicKey);
        byte[] cipherText = cipher.doFinal(content);
        System.out.println("密文: " + cipherText);

        return cipherText;
    }

    private static byte[] decrypt(byte[] content, ECPrivateKey ecPrivateKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            NoSuchProviderException {

        Cipher cipher = Cipher.getInstance("ECIESwithDESede/NONE/PKCS7Padding", "BC");
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
            if (!file.exists()){
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
}