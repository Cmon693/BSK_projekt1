package sample;

import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javax.swing.undo.AbstractUndoableEdit;
import java.io.File;

public class Controller implements Initializable{

    @FXML
    private Label label;

    @FXML
    private Button button;

    @FXML
    private Button encodeSelect;

    @FXML
    private Label encodeLabel;

    @FXML
    private TextField encodeTextField;

    @FXML
    private Button decodeSelect;

    @FXML
    private Label decodeLabel;

    @FXML
    private TextField decodeTextField;


    @FXML
    private ComboBox keyComboBox;
    ObservableList<String> keyList = FXCollections.observableArrayList("128", "192", "256");


    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
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
    private void generateButtonAction(ActionEvent event) throws IOException {
        RSA.main();
    }

    @FXML
    private void encodeButtonAction(ActionEvent event) throws Exception {
        String key = "lv432sdfjnsdfjds"; //TODO ten klucz musi byc przekazywany i chyba zaszyfrowany

        String path = encodeLabel.getText();
        String extention = path.substring(path.lastIndexOf('.'), path.length());
        String pathToSlash = path.substring(0, path.lastIndexOf('\\') + 1);

        File inputFile = new File(path);
        File encryptedFile = new File(pathToSlash + encodeTextField.getText());
        File decryptedFile = new File(pathToSlash + encodeTextField.getText() + extention);

        //TODO if czy dane sa poprawne
        sample.AES.encryptFile(key, inputFile, encryptedFile);
        //sample.AES.decryptFile(key, encryptedFile, decryptedFile);
    }

    @FXML
    private void decodeButtonAction(ActionEvent event) throws Exception {
        String key = "lv432sdfjnsdfjds"; //TODO ten klucz musi byc przekazywany i chyba zaszyfrowany

        String path = decodeLabel.getText();
        String pathToSlash = path.substring(0, path.lastIndexOf('\\') + 1);

        File encryptedFile = new File(path);
        File decryptedFile = new File(pathToSlash + decodeTextField.getText() + ".txt"); //TODO wykrywanie rozszerzenia


        //TODO if czy dane sa poprawne
        sample.AES.decryptFile(key, encryptedFile, decryptedFile);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        keyComboBox.setItems(keyList);
    }

}

