package sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import static sun.security.x509.CertificateAlgorithmId.ALGORITHM;

public class AES {

    private static final String ALGO = "AES";
    private byte[] keyValue;

    public AES (String key){
        keyValue = key.getBytes();
    }

    /*
    //TODO gen sess key
    public SecretKey getSKey(int kSize) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
        rand.generateSeed(kSize * 8); //wywalic mnozenie i przekazywac liczbe bajtow
        keygen.init(kSize * 8, rand);
        SecretKey sessionKey = keygen.generateKey();

        return sessionKey;
    }
    */



    public static void main () {
        try{
            //secret key //klucz sesyjny
            AES aes = new AES("lv432sdfjnsdfjds");

            System.out.println("\nSZYFR BLOKOWY AES");

            String encdata = aes.encrypt("Jakub Szymanski");
            System.out.println("Dane zaszyfrowane: " + encdata);

            String decdata = aes.decrypt(encdata);
            System.out.println("Dane odszyfrowane: " + decdata);

            System.out.println("-----------------------------");

            String key = "lv432sdfjnsdfjds";

            File inputFile = new File("bsk.txt");
            File encryptedFile = new File("bsk.enc");
            File decryptedFile = new File("bsk.dec");

            sample.AES.encryptFile(key, inputFile, encryptedFile);
            sample.AES.decryptFile(key, encryptedFile, decryptedFile);

//            File inputFile2 = new File("bsk.mp4");
//            File encryptedFile2 = new File("enc.mp4");
//            File decryptedFile2 = new File("dec.mp4");
//
//            sample.AES.encryptFile(key, inputFile2, encryptedFile2);
//            sample.AES.decryptFile(key, encryptedFile2, decryptedFile2);

        }catch (Exception ex){
            System.out.println(ex);
        }
    }


    public static void encryptFile(String key, File inputFile, File outputFile) throws Exception {

        Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();

        System.out.println("Plik " + inputFile + " zaszyfrowano do " + outputFile);
    }

    public static void decryptFile(String key, File inputFile, File outputFile) throws Exception {
        Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();

        System.out.println("Plik " + inputFile + " odszyfrowano do " + outputFile);
    }

    public String encrypt (String Data) throws Exception{

        Key key = generateKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = cipher.doFinal(Data.getBytes());

        String encryptedValue = new BASE64Encoder().encode(encValue);
        return encryptedValue;
    }

    public String decrypt (String encryptedData) throws Exception{
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = cipher.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private Key generateKey() throws Exception{
        Key key = new SecretKeySpec(keyValue, "AES");
        return key;
    }

    public static byte[] getIV() throws IOException {   //TODO dodac do encode, zwraca losowy wektor IV
        SecureRandom srandom = new SecureRandom();
        byte[] iv = new byte[16];
        srandom.nextBytes(iv);

        return iv;
    }

}
