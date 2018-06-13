package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Map;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        System.out.println("BSK projekt 1 AES");

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();

        //Generator.generateWithTime();

        //RSA.main();
        AES aes = new AES();
        aes.main();


    }


    public static void main(String[] args) {

        launch(args);
    }
}
