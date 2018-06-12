package sample;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import static sun.security.x509.CertificateAlgorithmId.ALGORITHM;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import sample.Controller;

public class AES {

    private static final String ALGO = "AES";
    private byte[] keyValue;

    public AES (String key){
        keyValue = key.getBytes();
    }


    //TODO gen sess key
    public static SecretKey getSKey(int kSize) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {

        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
        rand.generateSeed(kSize);
        keygen.init(kSize, rand);
        SecretKey sessionKey = keygen.generateKey();

        return sessionKey;
    }


    public static void main () {
        try{
            System.out.println("\nSZYFR BLOKOWY AES");

            AES aes = new AES("lv432sdfjnsdfjds");
            String encdata = aes.encrypt("Jakub Szymanski");
            System.out.println("Dane zaszyfrowane: " + encdata);
            String decdata = aes.decrypt(encdata);
            System.out.println("Dane odszyfrowane: " + decdata);
            System.out.println("-----------------------------");

            File inputFile = new File("bsk.txt");
            File encryptedFile = new File("bsk.enc");
            //File decryptedFile = new File("bsk.dec");

            //sample.AES.encryptFile(128, "ECB", ".txt", inputFile, encryptedFile, "login");
            //sample.AES.decryptFile(key, encryptedFile, decryptedFile);



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


    public static void encryptFile(int keySize, String cipherMode, String extension, File inputFile, File outputFile, String login) throws Exception {

        String algorythm = "AES";

        SecretKey secretKey = AES.getSKey(keySize); //gen klucza
        byte[] sKey = secretKey.getEncoded();

        RSA rsa = new RSA();

        PublicKey rsaPubKey = rsa.loadPublicKey(login); //typ pubKey załadowany z pliku

        byte[] encodedSKey = rsa.encryptSKey(rsaPubKey, sKey); //szyfrowanie tym pubKey -> to idzie do xmla

//

        byte[] iv = getIV();
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(algorythm + '/' + cipherMode + "/PKCS5Padding");

        if (cipherMode == "ECB") {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        }
        else {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        }

        int blockSize = cipher.getBlockSize();
        int outputSize = cipher.getOutputSize(blockSize);

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        //
        byte[] outputBytes = new byte[outputSize];

        float f, a, b = inputBytes.length;


        int i = 0;
        try {
            for (; i <= inputBytes.length - blockSize; i = i + blockSize) {
                int outLength = cipher.update(inputBytes, i, blockSize, outputBytes);
                if (i % 10000 == 0) {
                    a = i;
                    f = a / b;
                    System.out.println(f*100 + " %");
                    //Controller.setEncodeProgressBar(f); //TODO progress
                    //Controller controller = new Controller();
                    //controller.encodeProgressBar.setProgress(f);
                }
            }
            if (i == inputBytes.length)
                outputBytes = cipher.doFinal();
            else {
                outputBytes = cipher.doFinal(inputBytes, i, inputBytes.length - i);
            }

        } catch (Exception e) {
           e.printStackTrace();
        }
        System.out.println(blockSize);
        System.out.println(outputSize);
        //koniec


        //naglowek xml
        EncryptedFileHeader encryptedFileHeader = new EncryptedFileHeader();
        List<User> list=new ArrayList<User>();

        encryptedFileHeader.setAlgorithm("AES");
        encryptedFileHeader.setKeySize(keySize);
        encryptedFileHeader.setBlockSize("128");
        encryptedFileHeader.setCipherMode(cipherMode);
        encryptedFileHeader.setIV(iv);
        encryptedFileHeader.setFileExtension(extension);
        encryptedFileHeader.setLogin(login);
        encryptedFileHeader.setSessionKey(encodedSKey); //enc by .pub

        User user = new  User();
        user.setEmail(login);
        user.setSessionKey(encodedSKey);
        list.add(user);
        encryptedFileHeader.setUserList(list);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(EncryptedFileHeader.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(encryptedFileHeader, outputFile);
            //jaxbMarshaller.marshal(encryptedFileHeader, System.out);

            long len = outputFile.length();
            //System.out.println( outputFile.length());

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
            //System.out.println( outputFile.length());

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        byte[] outputBytess = cipher.doFinal(inputBytes); //juz zaszyfrowane
        //zapis szyfrogramu na koniec
        FileOutputStream outputStream = new FileOutputStream(outputFile, true); //true daje zapisywanie na koncu pliku
        outputStream.write(outputBytess);

        inputStream.close();
        outputStream.close();


        System.out.println("Plik " + inputFile + " zaszyfrowano do " + outputFile);
        //System.out.println( outputFile.length());
    }

    public static void decryptFile(File inputFile, String pathName, String fieldLogin, String fieldPassword) throws Exception {

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

        String cipherMode = efh.getCipherMode(); //wyciaganie danych z xmla
        System.out.println(cipherMode);
        String algorythm = efh.getAlgorithm();
        System.out.println(algorythm);
        byte[] iv = efh.getIV();
        System.out.println(iv);
        int keySize = efh.getKeySize();
        System.out.println(keySize);
        String extension = efh.getFileExtension();
        System.out.println(extension);

//        User user = new  User();
//        byte[] sessionKey = user.getSessionKey();
        byte[] sessionKey = efh.getSessionKey();
        System.out.println(sessionKey);
        String login = efh.getLogin();
        System.out.println(login);

        inputStreamXML.close();


        if (login.equals(fieldLogin)) ;
        else  return;

        System.out.println("Login correct");

        RSA rsa = new RSA();
        PrivateKey rsaPrivKey = rsa.loadPrivateKey(login); //typ pubKey załadowany z pliku
        byte[] decodedSKey = rsa.decryptSKey(rsaPrivKey, sessionKey); //szyfrowanie tym pubKey -> to idzie do xmla

        //decode
        SecretKeySpec secretKey = new SecretKeySpec(decodedSKey, algorythm);
        Cipher cipher = Cipher.getInstance(algorythm + '/' + cipherMode + "/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        if (cipherMode.equals("ECB")) {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        }
        else {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        }

        int blockSize = cipher.getBlockSize();
        int outputSize = cipher.getOutputSize(blockSize);

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length() - xmlLength - numberLength];

        inputStream.skip(xmlLength + numberLength);
        inputStream.read(inputBytes);

        //
        byte[] outputBytes = new byte[outputSize];

        float f, a, b = inputBytes.length;

        int i = 0;
        try {
            for (; i <= inputBytes.length - blockSize; i = i + blockSize) {
                int outLength = cipher.update(inputBytes, i, blockSize, outputBytes);
                if (i % 10000 == 0) {
                    a = i;
                    f = a / b * 100;
                    System.out.println(f + " %");
                }
            }
            if (i == inputBytes.length)
                outputBytes = cipher.doFinal();
            else {
                outputBytes = cipher.doFinal(inputBytes, i, inputBytes.length - i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(blockSize);
        System.out.println(outputSize);
        //koniec

        File outputFile = new File(pathName + extension);
        byte[] outputBytess = cipher.doFinal(inputBytes);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(outputBytess);

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
