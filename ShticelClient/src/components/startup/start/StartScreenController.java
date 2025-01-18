package components.startup.start;

import components.startup.login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class StartScreenController {

    private Stage primaryStage;

    @FXML private Button startHereButton;
    @FXML private StackPane startScreenStackPane;


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    void startHereButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();

        URL loginFXML = getClass().getResource("/components/startup/login/loginScreen.fxml");
        loader.setLocation(loginFXML);
        Parent root = loader.load();

        LoginController loginController = loader.getController();
        loginController.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Shticel - Login");
        primaryStage.setScene(scene);

        primaryStage.setResizable(false);

        primaryStage.setX((Screen.getPrimary().getVisualBounds().getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((Screen.getPrimary().getVisualBounds().getHeight() - primaryStage.getHeight()) / 2);


        primaryStage.show();
    }

}
