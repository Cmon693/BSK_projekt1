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
import javax.xml.bind.DatatypeConverter;


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

    public void saveKeys(String login, String password) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidKeySpecException, NoSuchPaddingException, IOException {
        KeyPair kp = RSAKeysGenerator();
        String path = System.getProperty("user.home");
        path += "\\AppData\\Local\\bsk\\";

        // haslo na hash
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        byte[] encoded = password.getBytes();
        byte[] passwordHash = sha1.digest(encoded);
        String s = DatatypeConverter.printHexBinary(passwordHash);
        System.out.println(s);

        // tutaj szyfrowanie private hasłem
        byte[] privateKey = kp.getPrivate().getEncoded();
        // skracanie
        SecretKeySpec passKey = new SecretKeySpec(passwordHash, 0, 16, "AES");

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

    public PrivateKey loadPrivateKey(String login, byte[] passwordHash) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String path = System.getProperty("user.home");
        path += "\\AppData\\Local\\bsk\\";

        File file = new File(path + "private\\" + login + ".prv");
        FileInputStream fis = new FileInputStream(file);
        byte[] privateKeyBytes = new byte[(int)file.length()];
        fis.read(privateKeyBytes);
        fis.close();

        // skracanie i decode

        SecretKeySpec passKey = new SecretKeySpec(passwordHash, 0, 16, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, passKey); //tutaj typ key juz skrocony
        byte[] decPriv = cipher.doFinal(privateKeyBytes); //dziala


        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(decPriv));

        return privateKey;
    }
}
