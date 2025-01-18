import components.startup.start.StartScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ClientMain extends Application {

    public static void main(String[] args) {
        Thread.currentThread().setName("main");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader();

        // load main fxml
        URL mainFXML = getClass().getResource("components/startup/start/startScreen.fxml");
        loader.setLocation(mainFXML);
        Parent root = loader.load();

        StartScreenController startScreenController = loader.getController();
        startScreenController.setPrimaryStage(primaryStage);

        // Set the scene and show the stage
        Scene scene = new Scene(root);
        primaryStage.setTitle("Shticel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}