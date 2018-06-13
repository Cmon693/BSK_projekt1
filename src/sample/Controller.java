package sample;

import java.io.*;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.undo.AbstractUndoableEdit;
import javax.xml.bind.DatatypeConverter;

public class Controller implements Initializable{

    @FXML
    private Label label;

    @FXML
    private Button button;

    //ENCODE
    @FXML
    private Button encodeSelect;

    @FXML
    private Label encodeLabel;

    @FXML
    private TextField encodeTextField;

    @FXML
    public ProgressBar encodeProgressBar;

    @FXML
    public ListView encodeListView;

    @FXML
    private ComboBox keyComboBox;
    ObservableList<String> keyList = FXCollections.observableArrayList("128", "192", "256");

    @FXML
    private ComboBox modeComboBox;
    ObservableList<String> modeList = FXCollections.observableArrayList("ECB", "OFB", "CBC", "CFB");

    //DECODE
    @FXML
    private Button decodeSelect;

    @FXML
    private Label decodeLabel;

    @FXML
    private TextField decodeTextField;

    @FXML
    private TextField decodeLogin;

    @FXML
    private TextField decodePassword;

    @FXML
    private ProgressBar decodeProgressBar;

    //USER
    @FXML
    private TextField userLogin;

    @FXML
    private TextField userPassword;




    //ACTIONS
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
    }

    //@FXML
    public void setEncodeProgressBar(double value) {
        encodeProgressBar.setProgress(value);
    }

    @FXML
    private void encodeSelectAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null)
        {
            encodeLabel.setText(selectedFile.getAbsolutePath());
        }
        System.out.println(encodeLabel.getText());
    }

    @FXML
    private void decodeSelectAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null)
        {
            decodeLabel.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void generateButtonAction(ActionEvent event) throws IOException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        //RSA.main();
        String password = userPassword.getText();
        Pattern letter = Pattern.compile("[a-zA-z]");
        Pattern digit = Pattern.compile("[0-9]");
        Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

        Matcher hasLetter = letter.matcher(password);
        Matcher hasDigit = digit.matcher(password);
        Matcher hasSpecial = special.matcher(password);

        if(password.length()>=8 && hasLetter.find() && hasDigit.find() && hasSpecial.find())
            System.out.println("Pass ok");
        else return; // to działa!!!


        //zapis uz do pliku
        String path = System.getProperty("user.home");
        path += "\\AppData\\Local\\bsk\\";

        FileOutputStream fos = new FileOutputStream(new File(path + "users.txt"), true);
        fos.write("\n".getBytes());
        fos.write(userLogin.getText().getBytes());
        fos.write("\n".getBytes());
        fos.write(userPassword.getText().getBytes());
        fos.close();


        encodeListView.getItems().add(userLogin.getText());

        RSA rsa = new RSA();
        rsa.saveKeys(userLogin.getText(), userPassword.getText());

    }

    @FXML
    private void addUsersToList() throws IOException {
        String path = System.getProperty("user.home");
        path += "\\AppData\\Local\\bsk\\";

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path + "users.txt"))));
            br.readLine();
            String tmp;
            while((tmp = br.readLine()) != null){
                encodeListView.getItems().add(tmp);
                br.readLine();
            }
            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        encodeListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML
    private void encodeButtonAction(ActionEvent event) throws Exception {
        //String key = "lv432sdfjnsdfjds";
        encodeProgressBar.setProgress(0.0);
        System.out.println(encodeProgressBar.getProgress());

        String path = encodeLabel.getText();
        String extention = path.substring(path.lastIndexOf('.'), path.length());
        String pathToSlash = path.substring(0, path.lastIndexOf('\\') + 1);

        File inputFile = new File(path);
        File encryptedFile = new File(pathToSlash + encodeTextField.getText());
        //File decryptedFile = new File(pathToSlash + encodeTextField.getText() + extention);

        System.out.println(keyComboBox.getValue());
        int keyLength = Integer.parseInt(keyComboBox.getValue().toString());
        System.out.println(modeComboBox.getValue());
        String cipherMode = modeComboBox.getValue().toString();

        String login = encodeListView.getSelectionModel().getSelectedItem().toString();

        //TODO if czy dane sa poprawne
        AES aes = new AES();
        aes.encryptFile(keyLength, cipherMode, extention, inputFile, encryptedFile, login);

        encodeProgressBar.setProgress(1.0);
        System.out.println(encodeProgressBar.getProgress());

    }

    @FXML
    private void decodeButtonAction(ActionEvent event) throws Exception {
        //String key = "lv432sdfjnsdfjds"; //TODO ten klucz musi byc przekazywany i chyba zaszyfrowany

        String path = decodeLabel.getText();
        String pathToSlash = path.substring(0, path.lastIndexOf('\\') + 1);

        File encryptedFile = new File(path);
        //File decryptedFile = new File(pathToSlash + decodeTextField.getText() + ".txt");
        String PathName = pathToSlash + decodeTextField.getText();

        String login = decodeLogin.getText();
        String password = decodePassword.getText();

        //TODO if czy dane sa poprawne
        AES aes = new AES();
        aes.decryptFile(encryptedFile, PathName, login, password);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        keyComboBox.setItems(keyList);
        modeComboBox.setItems(modeList);
        encodeProgressBar.setProgress(0.0);
        //decodeProgressBar.setProgress(0.0);

        try {
            addUsersToList();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

