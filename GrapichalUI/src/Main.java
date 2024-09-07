import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import subComponents.app.MainController;
import subComponents.grid.GridController;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    public static void main(String[] args) {
        Thread.currentThread().setName("main");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader();

        // load main fxml
        URL mainFXML = getClass().getResource("subComponents/app/main.fxml");
        loader.setLocation(mainFXML);
        Parent root = loader.load();

        // TODO - MAYBE? wire up controller
        MainController mainController = loader.getController();
        mainController.setPrimaryStage(primaryStage);
        GridController gridController = new GridController();
        mainController.setGridComponentController(gridController);
        gridController.setMainController(mainController);

        // Set the scene and show the stage
        Scene scene = new Scene(root);
        primaryStage.setTitle("Shticel by Amir Sofer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}