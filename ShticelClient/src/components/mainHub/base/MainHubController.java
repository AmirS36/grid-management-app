package components.mainHub.base;

import components.chatArea.ChatAreaController;
import components.mainHub.actionPanel.ActionPanelController;
import components.mainHub.loadFile.LoadFileController;
import components.mainHub.permissions.PermissionsTableController;
import components.mainHub.sheetsTable.SheetsTableController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainHubController {

    Stage primaryStage;

    @FXML HBox loadFileComponent;
    @FXML LoadFileController loadFileComponentController;

    @FXML TableView sheetsTableComponent;
    @FXML SheetsTableController sheetsTableComponentController;

    @FXML BorderPane permissionsTableComponent;
    @FXML PermissionsTableController permissionsTableComponentController;

    @FXML AnchorPane actionPanelComponent;
    @FXML ActionPanelController actionPanelComponentController;

    ChatAreaController chatAreaController;


    @FXML private BorderPane mainBorderPane;
    @FXML private ScrollPane mainScrollPane;
    @FXML private Label userNameLabel;

    private final StringProperty currentUserName = new SimpleStringProperty();

    @FXML
    void initialize() {
        userNameLabel.textProperty().bind(currentUserName);

        loadFileComponentController.setMainController(this);
        sheetsTableComponentController.setMainController(this);
        permissionsTableComponentController.setMainController(this);
        actionPanelComponentController.setMainController(this);

        permissionsTableComponentController.afterInitialize();
        actionPanelComponentController.afterInitialize();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void updateUserName(String userName) {
        currentUserName.set(userName);
    }

    public String getCurrentUserName() {
        return currentUserName.get();
    }

    public StringProperty currentUserNameProperty() {
        return currentUserName;
    }

    public SheetsTableController getSheetsTableComponentController() {
        return sheetsTableComponentController;
    }

    public PermissionsTableController getPermissionsTableComponentController() {
        return permissionsTableComponentController;
    }

    public LoadFileController getLoadFileComponentController() {
        return loadFileComponentController;
    }

    public void requestPermissionsDataForSheet() {
        permissionsTableComponentController.requestPermissionsDataForSheet(permissionsTableComponentController.getPermissionsCurrentSheetLabel());
    }

    public void setChatAreaController (ChatAreaController chatAreaController) {
        this.chatAreaController = chatAreaController;
    }

    public String getUserName() {
        return currentUserName.get();
    }

    public void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}
