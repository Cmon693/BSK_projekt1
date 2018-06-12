package sample;

import com.sun.scenario.effect.impl.prism.PrTexture;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class RSA {

    public KeyPair RSAKeysGenerator() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair kp = kpg.generateKeyPair();

        return kp;
    }

    public byte[] encryptSKey(PublicKey pubKey, byte[] sKey) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);

        return cipher.doFinal(sKey);
    }

    public byte[] decryptSKey(PrivateKey privKey, byte[] encryptedSKey) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privKey);

        return cipher.doFinal(encryptedSKey);
    }

    public void saveKeys(String login, byte[] password) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidKeySpecException, NoSuchPaddingException, IOException {
        KeyPair kp = RSAKeysGenerator();
        String path = System.getProperty("user.home");
        path += "\\AppData\\Local\\bsk\\";

        // tutaj szyfrowanie private hasłem
        byte[] privateKey = kp.getPrivate().getEncoded();
        // skracanie
        SecretKeySpec passKey = new SecretKeySpec(password, 0, 16, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, passKey); //tutaj typ key juz skrocony
        byte[] encPriv = cipher.doFinal(privateKey);

        FileOutputStream fos1 = new FileOutputStream(path + "public\\" + login + ".pub");
        FileOutputStream fos2 = new FileOutputStream(path + "private\\" + login + ".prv");
        fos1.write(kp.getPublic().getEncoded());
        fos2.write(encPriv);
        fos1.close();
        fos2.close();



    }

    public PublicKey loadPublicKey(String login) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String path = System.getProperty("user.home");
        path += "\\AppData\\Local\\bsk\\";

        FileInputStream fis = new FileInputStream(path + "public\\" + login + ".pub");
        byte[] publicKeyBytes = new byte[1024];
        fis.read(publicKeyBytes);
        fis.close();

        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        return publicKey;
    }

    public PrivateKey loadPrivateKey(String login) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String path = System.getProperty("user.home");
        path += "\\AppData\\Local\\bsk\\";

        FileInputStream fis = new FileInputStream(path + "private\\" + login + ".prv");
        byte[] privateKeyBytes = new byte[1024];
        fis.read(privateKeyBytes);
        fis.close();

        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

        return privateKey;
    }
}
