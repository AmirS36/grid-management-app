package components.mainHub.permissions;

import components.mainHub.base.MainHubController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import okhttp3.HttpUrl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import permissionsTable.PermissionsTableItemDTO;
import util.Constants;
import util.http.HttpClientUtil;
import permissionsTable.PermissionsTableItem;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static util.Constants.GSON_INSTANCE;

public class PermissionsTableController {

    private MainHubController myMainController;

    @FXML private Label permissionsCurrentSheetLabel;
    @FXML private Button refreshTableButton;

    @FXML private TableView<PermissionsTableItem> permissionsTV;
    @FXML private TableColumn<PermissionsTableItem, String> accessLevelColumn;
    @FXML private TableColumn<PermissionsTableItem, String> requestStatusColumn;
    @FXML private TableColumn<PermissionsTableItem, String> usernameColumn;

    private BooleanProperty isPermissionRequestSelected;
    private StringProperty requestDescription;

    private String requestedBy;
    private String requestedPermission;

    public PermissionsTableController() {
        isPermissionRequestSelected = new SimpleBooleanProperty();
        requestDescription = new SimpleStringProperty();
    }

    @FXML
    public void initialize() {
        accessLevelColumn.setCellValueFactory(cellData -> cellData.getValue().accessLevelProperty());
        requestStatusColumn.setCellValueFactory(cellData -> cellData.getValue().requestStatusProperty());
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().userNameProperty());

        permissionsCurrentSheetLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                refreshTableButton.setDisable(false);
                requestPermissionsDataForSheet(newValue);
            }
        });

        permissionsTV.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (myMainController.getCurrentUserName().equals(myMainController.getSheetsTableComponentController().getCurrentSheetOwner().get()))
                {
                    isPermissionRequestSelected.set(true);
                    requestDescription.set(newValue.getUserName() + " has requested to get "
                            + newValue.getAccessLevel() + " Permission to the sheet '" +  permissionsCurrentSheetLabel.getText() +"'");
                }
                else {
                    requestDescription.set("You are not the Owner of the current sheet! ");
                }
                requestedBy = newValue.getUserName();
                requestedPermission = newValue.getAccessLevel();
            }
        });

    }

    public void afterInitialize() {
        permissionsCurrentSheetLabel.textProperty().bind(myMainController.getSheetsTableComponentController().getCurrentSheetName());
    }


    public void setMainController(MainHubController myMainController) {
        this.myMainController = myMainController;
    }

    public BooleanProperty isPermissionRequestSelectedProperty() {
        return isPermissionRequestSelected;
    }

    public StringProperty requestDescriptionProperty() {
        return requestDescription;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public String getRequestedPermission() {
        return requestedPermission;
    }

    public void requestPermissionsDataForSheet(String sheetName) {

        String finalUrl = HttpUrl
                        .parse(Constants.PERMISSIONS_TABLE)
                        .newBuilder()
                        .addQueryParameter("sheetName", sheetName)
                        .build()
                        .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace(); // Log the error
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();

                if (responseBody.contains("\"message\":\"Empty\"")) {
                    System.out.println("No permissions found for sheet: " + sheetName);
                    Platform.runLater(() -> permissionsTV.getItems().clear()); // Clear the table if no data
                    return;
                }

                // Parse the response into PermissionsTableItemDTO objects
                PermissionsTableItemDTO[] permissionsArray = GSON_INSTANCE.fromJson(
                        responseBody, PermissionsTableItemDTO[].class);

                // Convert DTOs to JavaFX-friendly PermissionsTableItem objects
                List<PermissionsTableItem> permissionsList = Arrays.stream(permissionsArray)
                        .map(dto -> new PermissionsTableItem(dto.getUserName(), dto.getAccessLevelAsString(), dto.getRequestStatusAsString()))
                        .collect(Collectors.toList());

                // Update the TableView on the JavaFX thread
                Platform.runLater(() -> updatePermissionsTable(permissionsList));
            }
        });
    }

    private void updatePermissionsTable(List<PermissionsTableItem> permissionsList) {
        ObservableList<PermissionsTableItem> items = permissionsTV.getItems();
        items.clear();
        items.addAll(permissionsList);
    }

    public void refreshTableButtonAction(ActionEvent actionEvent) {
        requestPermissionsDataForSheet(permissionsCurrentSheetLabel.getText());
    }

    public String getPermissionsCurrentSheetLabel() {
        return permissionsCurrentSheetLabel.getText();
    }
}



