package sample;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.Key;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import static sun.security.x509.CertificateAlgorithmId.ALGORITHM;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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
            //sample.AES.decryptFile(key, encryptedFile, decryptedFile);

//            File inputFile2 = new File("bsk.mp4");
//            File encryptedFile2 = new File("enc.mp4");
//            File decryptedFile2 = new File("dec.mp4");
//
//            sample.AES.encryptFile(key, inputFile2, encryptedFile2);
//            sample.AES.decryptFile(key, encryptedFile2, decryptedFile2);

//            Path p = Paths.get("test.txt");
//            sample.AES.splitTextFiles(p, 1);
//            System.out.println("split");


        }catch (Exception ex){
            System.out.println(ex);
        }
    }

    public static void splitTextFiles(Path bigFile, int maxRows) throws IOException{
        //</EncryptedFileHeader>

        int i = 1;
        try(BufferedReader reader = Files.newBufferedReader(bigFile)){
            String line = null;
            int lineNum = 1;
            boolean flag = true;

            Path splitFile = Paths.get(i + "split.txt");
            BufferedWriter writer = Files.newBufferedWriter(splitFile, StandardOpenOption.CREATE);

            while ((line = reader.readLine()) != null) {

                if(lineNum > maxRows && flag == true){
                    writer.close();
                    lineNum = 1;
                    i++;
                    splitFile = Paths.get(i + "split.txt");
                    writer = Files.newBufferedWriter(splitFile, StandardOpenOption.CREATE);
                    flag = false;
                }

                writer.append(line);
                writer.newLine();
                lineNum++;
            }

            writer.close();
        }
    }


    public static void encryptFile(String key, File inputFile, File outputFile) throws Exception {

        Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        String s = secretKey.toString();

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes); //juz zaszyfrowane


        //naglowek xml
        EncryptedFileHeader encryptedFileHeader = new EncryptedFileHeader();
        List<User> list=new ArrayList<User>();

        encryptedFileHeader.setAlgorithm("AES");
        encryptedFileHeader.setKeySize("256");
        encryptedFileHeader.setBlockSize("128");
        encryptedFileHeader.setCipherMode("ECB");
        encryptedFileHeader.setIV("12345");

        User user = new  User();
        user.setEmail("q@gmail.com");
        user.setSessionKey(s);
        list.add(user);
        encryptedFileHeader.setUserList(list);
        user = new  User();
        user.setEmail("asd@gmail.com");
        user.setSessionKey(s);
        list.add(user);
        encryptedFileHeader.setUserList(list);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(EncryptedFileHeader.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(encryptedFileHeader, outputFile);
            jaxbMarshaller.marshal(encryptedFileHeader, System.out);

            long len = outputFile.length();
            System.out.println( outputFile.length());

            //dopisanie dlugosci xml w bajtach na poczatku pliku
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile)));
            String result = "";
            String line = "";
            while( (line = br.readLine()) != null){
                result = result + line + "\n";
            }
            result = len + "\n" + result;
            outputFile.delete();
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(result.getBytes());
            fos.flush();
            System.out.println( outputFile.length());

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        //zapis szyfrogramu na koniec
        FileOutputStream outputStream = new FileOutputStream(outputFile, true); //true daje zapisywanie na koncu pliku
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();


        System.out.println("Plik " + inputFile + " zaszyfrowano do " + outputFile);
        System.out.println( outputFile.length());

    }

    public static void decryptFile(String key, File inputFile, File outputFile) throws Exception {

        Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        //zczytanie pierwszej linii z dlugoscią xml
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
        String len = br.readLine();
        br.close();
        //System.out.println(len);

        int xmlLength = Integer.parseInt(len);
        int numberLength = 4;

        FileInputStream inputStreamXML = new FileInputStream(inputFile);
        inputStreamXML.skip(numberLength);

        byte[] inputBytesXML = new byte[xmlLength];
        inputStreamXML.read(inputBytesXML, 0, xmlLength);

        JAXBContext jaxbContext = JAXBContext.newInstance(EncryptedFileHeader.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        ByteArrayInputStream bais = new ByteArrayInputStream(inputBytesXML);
        EncryptedFileHeader efh = (EncryptedFileHeader) jaxbUnmarshaller.unmarshal(bais); //zczytywanie danych z xmla

        String mode = efh.getCipherMode(); //wyciaganie danych z xmla
        System.out.println(mode);
        String algo = efh.getAlgorithm();
        System.out.println(algo);
        String iv = efh.getIV();
        System.out.println(iv);
        String keySize = efh.getKeySize();
        System.out.println(keySize);

        inputStreamXML.close();

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length() - xmlLength - numberLength];

        inputStream.skip(xmlLength + numberLength);

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
