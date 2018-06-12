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

        AES.main();

        String s = "kuba";
        String s2 = "kuba";

        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        try {
                byte[] encoded = s.getBytes();
                byte[] digest = sha1.digest(encoded);
                String ss = DatatypeConverter.printHexBinary(digest);

                byte[] encoded2 = s2.getBytes();
                byte[] digest2 = sha1.digest(encoded2);
                String ss2 = DatatypeConverter.printHexBinary(digest2);

                System.out.println(ss);
                System.out.println(ss2);

        } catch (UnsupportedOperationException e) {
            System.out.printf("Cant make it work for %s%n");
        }





    }


    public static void main(String[] args) {

        launch(args);
    }
}
