package sample;

import com.sun.scenario.effect.impl.prism.PrTexture;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class RSA {
    private  static final String PUBLIC_KEY_FILE = "Public.key";
    private  static final String PRIVATE_KEY_FILE = "Private.key";

    public static void main () throws IOException{
        try {
            System.out.println("GENEROWANIE klucza publicznego i prywatnego");
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            //System.out.println("parametry key pair");
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec rsaPublicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
            RSAPrivateKeySpec rsaPrivateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);

            System.out.println("ZAPIS kluczy do plikow");
            RSA rsaObj = new RSA();
            //save
            rsaObj.saveKeys(PUBLIC_KEY_FILE, rsaPublicKeySpec.getModulus(), rsaPublicKeySpec.getPublicExponent());
            rsaObj.saveKeys(PRIVATE_KEY_FILE, rsaPrivateKeySpec.getModulus(), rsaPrivateKeySpec.getPrivateExponent());

            //encrypt
            //tutaj moze byc szyfrowany klucz sesyjny - to secret key, secureRandom
            byte[] encryptedData = rsaObj.encryptData("Przykladowy tekst");

            //decrypt
            rsaObj.decryptData(encryptedData);


        } catch (NoSuchAlgorithmException | InvalidKeySpecException e){
            System.out.println(e);
        }
    }

    private void saveKeys (String fileName, BigInteger mod, BigInteger exp) throws IOException{

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            System.out.println("Generowanie " + fileName);
            fos = new FileOutputStream(fileName);
            oos = new ObjectOutputStream(new BufferedOutputStream(fos));
            oos.writeObject(mod);
            oos.writeObject(exp);
            System.out.println(fileName + " wygenerowany!");

        } catch (Exception e){
            System.out.println(e);
        } finally {
            if (oos != null){
                oos.close();
                if (fos != null){
                    fos.close();
                }
            }
        }
    }

    private byte[] encryptData(String data) throws IOException{
        System.out.println("SZYFROWANIE rozpoczate");
        System.out.println("dane przed: "+ data);
        byte[] dataToEncrypt = data.getBytes();
        byte[] encryptedData = null;

        try{
            PublicKey publicKey = readPublicKeyFromFile(this.PUBLIC_KEY_FILE);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            encryptedData = cipher.doFinal(dataToEncrypt);
            System.out.println("dane po: " + encryptedData);

        }catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e){
            e.printStackTrace();
        }
        System.out.println("Szyfrowanie zakonczone");
        return encryptedData;
    }

    private void decryptData(byte[] data) throws IOException{
        System.out.println("Deszyfracja rozpoczata");
        byte[] decryptedData = null;
        try {
            PrivateKey privateKey = readPrivateKeyFromFile(this.PRIVATE_KEY_FILE);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey );
            decryptedData = cipher.doFinal(data);
            System.out.println("odszyfrowane: " + new String (decryptedData));

        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){
            e.printStackTrace();
        }
        System.out.println("Zakonczono deszyfracje");

    }

    public PublicKey readPublicKeyFromFile(String fileName) throws IOException{
        FileInputStream fis = null;
        ObjectInput ois = null;
        try {
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);

            BigInteger modulus = (BigInteger) ois.readObject();
            BigInteger exponent = (BigInteger) ois.readObject();

            //get public key
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = factory.generatePublic(rsaPublicKeySpec);
            return publicKey;

        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException e){
            e.printStackTrace();
        } finally {
            if (ois != null){
                ois.close();
                if (fis != null){
                    fis.close();
                }
            }
        }
        return null;
    }

    public PrivateKey readPrivateKeyFromFile(String fileName) throws IOException{
        FileInputStream fis = null;
        ObjectInput ois = null;
        try {
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);

            BigInteger modulus = (BigInteger) ois.readObject();
            BigInteger exponent = (BigInteger) ois.readObject();

            //get Private key
            RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = factory.generatePrivate(rsaPrivateKeySpec);
            return privateKey;

        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException e){
            e.printStackTrace();
        } finally {
            if (ois != null){
                ois.close();
                if (fis != null){
                    fis.close();
                }
            }
        }
        return null;
    }

}
