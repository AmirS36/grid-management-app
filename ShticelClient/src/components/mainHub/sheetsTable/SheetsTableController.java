package components.mainHub.sheetsTable;

import components.mainHub.base.MainHubController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sheetsTable.SheetTableItem;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static util.Constants.REFRESH_RATE;

public class SheetsTableController {

    private MainHubController myMainController;

    @FXML private TableView<SheetTableItem> sheetsTV;
    @FXML private TableColumn<SheetTableItem, String> uploadedByColumn;
    @FXML private TableColumn<SheetTableItem, String> nameColumn;
    @FXML private TableColumn<SheetTableItem, String> sizeColumn;
    @FXML private TableColumn<SheetTableItem, String> permissionColumn;

    private Timer timer;
    private TimerTask sheetsTableRefresher;
    private final BooleanProperty autoUpdate;

    private StringProperty currentSheetName;
    private StringProperty currentSheetOwner;
    private StringProperty currentSheetPermission;
    private BooleanProperty isSheetSelected;
    private BooleanProperty canViewSheet;


    public SheetsTableController() {
        autoUpdate = new SimpleBooleanProperty();  // BooleanProperty for auto-updating
        currentSheetName = new SimpleStringProperty();
        currentSheetOwner = new SimpleStringProperty();
        currentSheetPermission = new SimpleStringProperty();
        isSheetSelected = new SimpleBooleanProperty();
        canViewSheet = new SimpleBooleanProperty();
    }

    @FXML
    public void initialize() {
        // Here you can bind columns to the properties in SheetTableItem class
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().sheetNameProperty());
        uploadedByColumn.setCellValueFactory(cellData -> cellData.getValue().uploadedByProperty());
        sizeColumn.setCellValueFactory(cellData -> cellData.getValue().sheetSizeProperty());
        permissionColumn.setCellValueFactory(cellData -> cellData.getValue().permissionLevelProperty());

        sheetsTV.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentSheetName.set(newValue.getSheetName());
                currentSheetOwner.set(newValue.getUploadedBy());
                currentSheetPermission.set(newValue.getPermissionLevel());
                isSheetSelected.set(true);
                canViewSheet.set(false);
                if (!(newValue.getPermissionLevel().equals("None"))){
                    canViewSheet.set(true);
                }
            }
        });
        startSheetsTableRefresher();
        autoUpdate.set(true);
    }

    public void setMainController(MainHubController myMainController) {
        this.myMainController = myMainController;
    }

    public BooleanProperty autoUpdatesProperty() {
        return autoUpdate;
    }

    public StringProperty getCurrentSheetName() {
        return currentSheetName;
    }

    public StringProperty getCurrentSheetOwner() {
        return currentSheetOwner;
    }

    public StringProperty getCurrentSheetPermission() {
        return currentSheetPermission;
    }

    public BooleanProperty isSheetSelectedProperty() {
        return isSheetSelected;
    }

    public BooleanProperty canViewSheetProperty() {
        return canViewSheet;
    }

    private void updateSheetsTable(List<SheetTableItem> sheetsList) {
        Platform.runLater(() -> {
            ObservableList<SheetTableItem> items = sheetsTV.getItems();
            items.clear();
            items.addAll(sheetsList);

            if (currentSheetName != null) {
                for (SheetTableItem item : items) {
                    if (item.getSheetName().equals(currentSheetName.get())) {
                        sheetsTV.getSelectionModel().select(item);
                        break;
                    }
                }
            }
        });
    }

    public void startSheetsTableRefresher() {
        System.out.println("startSheetsTableRefresher");
        sheetsTableRefresher = new SheetsTableRefresher(
                this::updateSheetsTable,
                autoUpdate
        );
        timer = new Timer();
        timer.schedule(sheetsTableRefresher, REFRESH_RATE, REFRESH_RATE);
    }


}
