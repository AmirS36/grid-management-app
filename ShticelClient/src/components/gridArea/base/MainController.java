package components.gridArea.base;


import cell.CellDTO;
import components.gridArea.actionLine.ActionLineController;
import components.gridArea.grid.GridController;
import components.gridArea.ranges.RangesController;
import components.gridArea.versionSelector.VersionSelectorController;
import components.mainHub.base.MainHubController;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import logic.permissions.PermissionLevel;
import management.SheetManagerDTO;
import management.VersionManagerDTO;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import resources.Resources;

import components.gridArea.commands.CommandsCenterController;
import sheet.SheetDTO;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;

import static util.Constants.GSON_INSTANCE;

public class MainController {

    public static CellDTO theEmptyCell = new CellDTO(" ", " ");

    @FXML private Label sheetHeaderLabel;
    @FXML private Label userNameLabel;
    @FXML private Label permissionLabel;

    @FXML private ScrollPane mainScrollPane;
    @FXML private BorderPane mainBorderPane;
    private Stage primaryStage;

    @FXML private TabPane commandsCenterComponent;
    @FXML private CommandsCenterController commandsCenterComponentController;

    @FXML private HBox actionLineComponent;
    @FXML private ActionLineController actionLineComponentController;

    @FXML private VBox rangesComponent;
    @FXML private RangesController rangesComponentController;

    @FXML private VBox leftSideVBox;

    private GridPane gridComponent;
    private GridController gridComponentController;

    private Pane versionSelectorComponent;
    private VersionSelectorController versionSelectorComponentController;

    private SheetManagerDTO currentSheetManager;

    private PermissionLevel currentPermissionLevel;

    private SimpleBooleanProperty isGreedPresent;
    private SimpleBooleanProperty isCellSelected;
    private SimpleBooleanProperty isColumnSelected;
    private SimpleBooleanProperty isRowSelected;

    private SimpleBooleanProperty isOwner;
    private SimpleBooleanProperty isReader;
    private SimpleBooleanProperty isWriter;

    private boolean isLatestVersion;

    public MainController() {
        isGreedPresent = new SimpleBooleanProperty(false);
        isCellSelected = new SimpleBooleanProperty(false);
        isColumnSelected = new SimpleBooleanProperty(false);
        isRowSelected = new SimpleBooleanProperty(false);

        isOwner = new SimpleBooleanProperty(false);
        isReader = new SimpleBooleanProperty(false);
        isWriter = new SimpleBooleanProperty(false);

        isLatestVersion = true;
    }

    @FXML
    public void initialize() {
        if (commandsCenterComponentController != null && actionLineComponentController != null && rangesComponentController != null) {
            commandsCenterComponentController.setMainController(this);
            actionLineComponentController.setMainController(this);
            rangesComponentController.setMyMainController(this);
        }

        commandsCenterComponentController
                .bindCommandsCenter(isColumnSelected, isRowSelected, actionLineComponentController.getIsCellPresent());

        Resources.setCurrentTheme("/resources/themes/default.css");
    }

    //First load
    public void setCurrentSheetManager(SheetManagerDTO sheetManagerDTO) {
        this.currentSheetManager = sheetManagerDTO;
        initializeCurrentSheet();
        mainBorderPane.setCenter(gridComponent);
        enableComponentsAfterSheetLoad();
    }

    public void initializeCurrentSheet() {
        gridComponentController.initialize();
        gridComponentController.setCurrentSheet(currentSheetManager.getCurrentSheet());
        initializeVersionSelectorDialog();
        rangesComponentController.bindRangeList();
        isGreedPresent.set(true);
    }

    //After first load
    public void updateSheetManger(SheetManagerDTO sheetManagerDTO) {
        this.currentSheetManager = sheetManagerDTO;
        gridComponentController.setCurrentSheet(sheetManagerDTO.getCurrentSheet());

        rangesComponentController.setNewRangeList(sheetManagerDTO.getCurrentSheet().getRanges().values());
        versionSelectorComponentController.setVersionManager(sheetManagerDTO.getVersionManager());
        versionSelectorComponentController.myInitialize();
    }

    public void setMainHeader (String header) {
        sheetHeaderLabel.setText(header);
    }

    public ScrollPane getMainScrollPane () {
        return mainScrollPane;
    }

    public void toggleGridDisable () {
        gridComponent.setDisable(!gridComponent.isDisable());
    }

    public void toggleVersionSelectorDisable () {
        versionSelectorComponent.setDisable(!versionSelectorComponent.isDisable());
    }

    public void toggleRangesComponentDisable () {
        rangesComponent.setDisable(!rangesComponent.isDisable());
    }

    public void toggleAllButCommandsCenter () {
        toggleGridDisable();
        toggleVersionSelectorDisable();
        toggleRangesComponentDisable();
    }

    public void updateEntireGrid() {
        gridComponentController.updateEntireGrid();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setGridComponentController(GridController gridComponentController) {
        this.gridComponentController = gridComponentController;
    }

    public GridController getGridComponentController() {
        return gridComponentController;
    }

    public RangesController getRangesComponentController() {
        return rangesComponentController;
    }

    public ActionLineController getActionLineComponentController() {
        return actionLineComponentController;
    }

    public Pane getVersionSelectorComponent() {
        return versionSelectorComponent;
    }

    public void setPermissionLevel (PermissionLevel permissionLevel, String userName) {
        this.currentPermissionLevel = permissionLevel;
        switch (currentPermissionLevel) {
            case READER:
                isReader.set(true);
                permissionLabel.setText("Reader");
                break;

            case WRITER:
                isWriter.set(true);
                permissionLabel.setText("Writer");
                break;

            case OWNER:
                isOwner.set(true);
                permissionLabel.setText("Owner");
                break;
        }
        userNameLabel.setText(userName);
    }


    public SheetDTO getCurrentSheet() {
        return gridComponentController.getCurrentSheet();
    }

    public VersionManagerDTO getVersionManger() {
        return this.currentSheetManager.getVersionManager();
    }

    public SimpleBooleanProperty isReaderProperty() {
        return isReader;
    }

    public SimpleBooleanProperty isWriterProperty() {
        return isWriter;
    }

    public SimpleBooleanProperty isOwnerProperty() {
        return isOwner;
    }

    public boolean isLatestVersion() {
        return isLatestVersion;
    }

    public void setIsLatestVersion (boolean isLatestVersion) {
        this.isLatestVersion = isLatestVersion;
    }

    public void setGridComponent (GridPane gridComponent) {
        mainBorderPane.setCenter(gridComponent);
        this.gridComponent = gridComponent;
    }

    public void handleEnterKeyPressed (KeyEvent keyEvent) {
        if (isGreedPresent.get() && keyEvent.getCode() == KeyCode.ENTER && actionLineComponentController.getIsCellPresent().get()) {
            actionLineComponentController.handleUpdateValueButton();
        }
    }

    public void onMouseClickedAnywhere() {
        if (rangesComponentController.getRangesListView().getSelectionModel().getSelectedItem() != null) {
            rangesComponentController.getRangesListView().getSelectionModel().clearSelection();
        }
    }

    public void updateActionLine(CellDTO cell) {
        actionLineComponentController.updateActionLine(cell);
    }

    public void setIsCellPresent(boolean isCellPresent) {
        actionLineComponentController.setIsCellPresent(isCellPresent);
    }

    public void setIsGriPresent(boolean isGreedPresent) {
        actionLineComponentController.setIsGreedPresent(isGreedPresent);
    }

    public void updateCell(String cellID, String value) {
        gridComponentController.updateCell(cellID, value, "Final");
    }

    public void setIsColumnSelected(Boolean isColumnSelected) {
        this.isColumnSelected.set(isColumnSelected);
    }

    public void setIsRowSelected(Boolean isRowSelected) {
        this.isRowSelected.set(isRowSelected);
    }

    public SimpleBooleanProperty isGreedPresentProperty() {
        return isGreedPresent;
    }

    public GridPane getMainGrid() {
        return gridComponent;
    }

    public CommandsCenterController getCommandsCenterController() {
        return commandsCenterComponentController;
    }

    private void initializeVersionSelectorDialog() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/gridArea/versionSelector/versionSelector.fxml"));
            versionSelectorComponent = loader.load();

            VersionSelectorController vsController = loader.getController();
            vsController.setMyMainController(this);
            versionSelectorComponentController = vsController;
            vsController.setVSComponentRoot(versionSelectorComponent);
            vsController.setVersionManager(getVersionManger());
            vsController.newVersionSelectorDialogSetup();
            vsController.myInitialize();

        } catch (IOException e) {
            showLoadingError(e);
        }
    }

    public void showVersionSelectorDialog() {
        versionSelectorComponentController.getNewVSStage().show();
    }

    public void showLoadingError (Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    public void enableComponentsAfterSheetLoad() {
        leftSideVBox.setDisable(false);
        actionLineComponent.setDisable(false);
        actionLineComponentController.setPermissions(isReaderProperty(), isWriterProperty(), isOwnerProperty());
        commandsCenterComponentController.setPermissions(isReaderProperty(), isWriterProperty(), isOwnerProperty());
        rangesComponentController.setPermissions(isReaderProperty(), isWriterProperty(), isOwnerProperty());
    }

    public void backToDashboardAction(ActionEvent actionEvent) throws IOException {
        initiateMainHub();

    }

    private void initiateMainHub() throws IOException {
        FXMLLoader loader = new FXMLLoader();

        URL loginFXML = getClass().getResource("/components/mainHub/base/home.fxml");
        loader.setLocation(loginFXML);
        Parent root = loader.load();

        MainHubController mainHubController = loader.getController();
        mainHubController.setPrimaryStage(primaryStage);
        mainHubController.updateUserName(HttpClientUtil.getUserName());

        Scene scene = new Scene(root);
        primaryStage.setTitle("Shticel - Main Hub");
        primaryStage.setScene(scene);

        primaryStage.setResizable(true);

        primaryStage.setX((Screen.getPrimary().getVisualBounds().getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((Screen.getPrimary().getVisualBounds().getHeight() - primaryStage.getHeight()) / 2);

        primaryStage.show();

    }

    public void showUpdateNotification() {
        actionLineComponentController.showUpdateNotification();
    }

    public void getUpdatedSheet() {
        String sheetName = currentSheetManager.getCurrentSheet().getName();

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
                        updateSheetManger(sheetManagerDTO);
                        updateActionLine(theEmptyCell);
                    });  // Open the new window on the UI thread
                } else {
                    System.out.println("Server returned error: " + response.code());
                }
            }
        });
    }
}
