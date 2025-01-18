package components.gridArea.versionSelector;

import components.gridArea.base.MainController;
import components.gridArea.versionSelector.serverUpdates.SheetUpdatesRefresher;
import components.mainHub.sheetsTable.SheetsTableRefresher;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.grid.GridService;
import management.VersionManagerDTO;
import sheet.SheetDTO;


import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static util.Constants.REFRESH_RATE;

public class VersionSelectorController {

    MainController myMainController;
    private Stage newVSStage;
    private Stage newGridStage;
    private Parent mainComponent;
    private GridService gridService;

    @FXML private TableView<VersionData> vsTableView;
    @FXML private TableColumn<VersionData, Number> versionColumn;
    @FXML private TableColumn<VersionData, Number> cellsChangedColumn;

    @FXML private Button VSApplyButton;
    @FXML private Button VSCancelButton;

    private SimpleBooleanProperty isVersionSelected;

    //Utilities
    private VersionManagerDTO versionManager;

    private Timer timer;
    private TimerTask sheetUpdatesRefresher;

    public void myInitialize() {
        gridService = new GridService();
        isVersionSelected = new SimpleBooleanProperty(false);
        VSApplyButton.disableProperty().bind(isVersionSelected.not());
        // Define how each column gets its data
        versionColumn.setCellValueFactory(cellData -> cellData.getValue().versionNumberProperty());
        cellsChangedColumn.setCellValueFactory(cellData -> cellData.getValue().cellChangesProperty());

        // Convert versionManager data to VersionData and populate the table
        ObservableList<VersionData> versionDataList = FXCollections.observableArrayList();

        // Loop through the versionManager to get versions and cell changes
        for (Map.Entry<Integer, SheetDTO> entry : versionManager.getVersionsMap().entrySet()) {
            int versionNumber = entry.getKey();
            int cellChanges = entry.getValue().getCellsChanged(); // Assuming this returns an integer

            versionDataList.add(new VersionData(versionNumber, cellChanges));
        }

        // Add the data to the TableView
        vsTableView.setItems(versionDataList);

        Map<Integer, SheetDTO> versionsMap = versionManager.getVersionsMap();
        ObservableMap<Integer, SheetDTO> ObservableVersionsMap = FXCollections.observableMap(versionsMap);

        ObservableVersionsMap.addListener((MapChangeListener<Integer, SheetDTO>) change -> {
            if (change.wasAdded()) {
                int versionNumber = change.getKey();
                SheetDTO newSheet = change.getValueAdded();
                versionDataList.add(new VersionData(versionNumber, newSheet.getCellsChanged()));
            }

            if (change.wasRemoved()) {
                int versionNumber = change.getKey();
                versionDataList.removeIf(data -> data.getVersionNumber() == versionNumber);
            }
        });

        vsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            isVersionSelected.setValue(newValue != null);
        });

        startSheetUpdatesRefresher();
    }


    public void setMyMainController (MainController myMainController) {
        this.myMainController = myMainController;
    }

    public void setVersionManager(VersionManagerDTO versionManger) {
        this.versionManager = versionManger;
    }

    public void setVSComponentRoot(Parent mainComponent) {
        this.mainComponent = mainComponent;
    }

    public Stage getNewVSStage() {
        return newVSStage;
    }

    public void newVersionSelectorDialogSetup() {
        newVSStage = new Stage();
        newVSStage.setScene(new Scene(mainComponent));
        newVSStage.setTitle("Version Selector");
        newVSStage.initModality(Modality.APPLICATION_MODAL);
        newVSStage.setResizable(false);
    }

    //Actions

    @FXML
    void VSApplyButtonAction(ActionEvent event) {
        int versionNumber = vsTableView.getSelectionModel().getSelectedItem().getVersionNumber();
        vsTableView.getSelectionModel().clearSelection();
        SheetDTO sheet = versionManager.getVersionsMap().get(versionNumber);
        GridPane viewOnlyGrid = gridService.createViewOnlyGrid(sheet,0,0);

        newGridStage = new Stage();
        newGridStage.setScene(new Scene(viewOnlyGrid));
        newGridStage.setTitle(sheet.getName() + " v." + sheet.getVersion());
        newGridStage.initModality(Modality.APPLICATION_MODAL);
        newGridStage.setResizable(false);
        newGridStage.showAndWait();
    }

    @FXML
    void VSCancelButtonAction(ActionEvent event) {
        newVSStage.close();
    }

    public void startSheetUpdatesRefresher() {

        System.out.println("startSheetUpdatesRefresher");
        sheetUpdatesRefresher = new SheetUpdatesRefresher(
                myMainController
        );
        timer = new Timer();
        timer.schedule(sheetUpdatesRefresher, REFRESH_RATE, REFRESH_RATE);
    }
}
