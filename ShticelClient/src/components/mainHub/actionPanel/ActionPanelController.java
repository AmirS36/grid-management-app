package components.mainHub.actionPanel;

import components.gridArea.base.MainController;
import components.gridArea.grid.GridController;
import components.mainHub.base.MainHubController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Screen;
import javafx.stage.Stage;
import logic.permissions.PermissionLevel;
import management.SheetManagerDTO;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;
import okhttp3.HttpUrl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import sheet.SheetDTO;

import static util.Constants.GSON_INSTANCE;

public class ActionPanelController {

    private MainHubController myMainController;

    @FXML private Label selectedSheetNameLabel;
    @FXML private Label selectedSheetOwnerLabel;
    @FXML private Label yourAccessLevelLabel;
    @FXML private Button viewSheetButton;

    @FXML private ComboBox<String> permissionTypeComboBox;
    private final List<String> permissionLabels = List.of("Reader", "Writer");
    private BooleanProperty permissionSelected;
    @FXML private Button sendRequestButton;

    @FXML private Label requestDetailsLabel;
    @FXML private Button approveRequestButton;
    @FXML private Button declineRequestButton;


    @FXML
    public void initialize() {
        permissionSelected = new SimpleBooleanProperty(false);
        permissionTypeComboBox.getItems().addAll(permissionLabels);

        permissionTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            permissionSelected.set(newValue != null);
        });
    }

    public void afterInitialize() {
        selectedSheetNameLabel.textProperty().bind(myMainController.getSheetsTableComponentController().getCurrentSheetName());
        selectedSheetOwnerLabel.textProperty().bind(myMainController.getSheetsTableComponentController().getCurrentSheetOwner());
        yourAccessLevelLabel.textProperty().bind(myMainController.getSheetsTableComponentController().getCurrentSheetPermission());
        viewSheetButton.disableProperty().bind(myMainController.getSheetsTableComponentController().canViewSheetProperty().not());
        permissionTypeComboBox.disableProperty().bind(myMainController.getSheetsTableComponentController().isSheetSelectedProperty().not());
        sendRequestButton.disableProperty().bind(permissionSelected.not());
        approveRequestButton.disableProperty().bind(myMainController.getPermissionsTableComponentController().isPermissionRequestSelectedProperty().not());
        declineRequestButton.disableProperty().bind(myMainController.getPermissionsTableComponentController().isPermissionRequestSelectedProperty().not());
        requestDetailsLabel.textProperty().bind(myMainController.getPermissionsTableComponentController().requestDescriptionProperty());

    }

    public void setMainController(MainHubController myMainController) {
        this.myMainController = myMainController;
    }

    @FXML
    void handleRequestPermission(ActionEvent event) {
        String sheetName = selectedSheetNameLabel.getText();
        String permissionType = permissionTypeComboBox.getValue();

        String finalUrl = HttpUrl
                .parse(Constants.REQUEST_PERMISSION)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .addQueryParameter("permissionType", permissionType)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("Permission request sent successfully.");
                    myMainController.requestPermissionsDataForSheet();
                } else {
                    System.out.println("Failed to send request: " + response.message());
                }
            }
        });

    }

    @FXML
    void handleApproveRequest(ActionEvent event) {
        sendRequestApproval(true);
        myMainController.requestPermissionsDataForSheet();
    }

    @FXML
    void handleDeclineRequest(ActionEvent event) {
        sendRequestApproval(false);
        myMainController.requestPermissionsDataForSheet();
    }

    private void sendRequestApproval(boolean isApproved) {
        String sheetName = selectedSheetNameLabel.getText();
        String requestedBy = myMainController.getPermissionsTableComponentController().getRequestedBy();
        String requestedPermission = myMainController.getPermissionsTableComponentController().getRequestedPermission();

        // Construct the URL with query parameters
        String finalUrl = HttpUrl
                .parse(Constants.APPROVE_DECLINE_PERMISSION)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .addQueryParameter("requestedBy", requestedBy)
                .addQueryParameter("requestedPermission", requestedPermission)
                .addQueryParameter("isApproved", String.valueOf(isApproved))
                .build()
                .toString();

        // Send the request using HttpClientUtil
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.err.println("Failed to send approval/decline request: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("Request processed successfully for user: " + requestedBy);
                } else {
                    System.err.println("Request failed: " + response.message());
                }
            }
        });
    }


    @FXML
    void handleViewSheet(ActionEvent event) {
        String sheetName = selectedSheetNameLabel.getText();

        String finalUrl = HttpUrl
                .parse(Constants.GET_SHEET)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    // Handle failure (e.g., show an alert to the user)
                    System.out.println("Failed to load sheet: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    SheetManagerDTO sheetManagerDTO = GSON_INSTANCE.fromJson(responseBody, SheetManagerDTO.class);

                    Platform.runLater(() -> {
                        try {
                            openSheetWindow(sheetManagerDTO);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });  // Open the new window on the UI thread
                } else {
                    System.out.println("Server returned error: " + response.code());
                }
            }
        });

    }

    private void openSheetWindow(SheetManagerDTO sheetManagerDTO) throws IOException {
        FXMLLoader loader = new FXMLLoader();

        URL loginFXML = getClass().getResource("/components/gridArea/base/GridFrame.fxml");
        loader.setLocation(loginFXML);
        Parent root = loader.load();

        Stage primaryStage = myMainController.getPrimaryStage();

        MainController mainController = loader.getController();
        mainController.setPrimaryStage(primaryStage);

        GridController gridController = new GridController();
        mainController.setGridComponentController(gridController);
        gridController.setMainController(mainController);

        String sheetName = sheetManagerDTO.getCurrentSheet().getName();
        sheetName = sheetName.substring(0,1).toUpperCase() + sheetName.substring(1);
        String currentUserName = myMainController.getCurrentUserName();

        mainController.setCurrentSheetManager(sheetManagerDTO);
        switch (yourAccessLevelLabel.getText()) {
            case "Owner":
                mainController.setPermissionLevel(PermissionLevel.OWNER, currentUserName);
                mainController.setMainHeader(sheetName);
                break;
            case "Reader":
                mainController.setPermissionLevel(PermissionLevel.READER, currentUserName);
                mainController.setMainHeader(sheetName);
                break;
            case "Writer":
                mainController.setPermissionLevel(PermissionLevel.WRITER, currentUserName);
                mainController.setMainHeader(sheetName);
                break;
        }

        Scene scene = new Scene(root);
        primaryStage.setTitle(sheetManagerDTO.getCurrentSheet().getName());
        primaryStage.setScene(scene);

        primaryStage.setX((Screen.getPrimary().getVisualBounds().getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((Screen.getPrimary().getVisualBounds().getHeight() - primaryStage.getHeight()) / 2);

        primaryStage.show();
    }

}
