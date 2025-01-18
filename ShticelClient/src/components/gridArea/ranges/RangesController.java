package components.gridArea.ranges;

import components.gridArea.base.MainController;
import components.gridArea.ranges.newRangeDialog.NewRangeController;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import range.RangeDTO;
import sheet.SheetDTO;
import util.Constants;
import util.http.HttpClientUtil;


import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static util.Constants.GSON_INSTANCE;

public class RangesController {

    private MainController myMainController;

    @FXML private Button deleteRangeButton;
    @FXML private Button editRangeButton;
    @FXML private Button newRangeButton;
    @FXML private ListView<RangeDTO> rangesListView;

    private SimpleBooleanProperty isRangeSelected;
    private ObservableList<RangeDTO> rangeList;
    private ObjectProperty<RangeDTO> selectedRange;

    //New Range Dialog
    private NewRangeController newRangeController;
    private Parent rangePromptRoot;


    public RangesController() {
        isRangeSelected = new SimpleBooleanProperty(false);
    }

    @FXML
    private void initialize() {
        deleteRangeButton.disableProperty().bind(isRangeSelected.not());
        editRangeButton.disableProperty().bind(isRangeSelected.not());
        initializeNewRangeDialog();

        selectedRange = new SimpleObjectProperty<>();
        selectedRange.bind(rangesListView.getSelectionModel().selectedItemProperty());

        rangesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedRange) -> {
            if (selectedRange != null) {
                myMainController.getGridComponentController().unclickCell();
                isRangeSelected.setValue(true);
                if (oldValue != null){
                unmarkRangeCells(oldValue.getCells());
                }
                markRangeCells(selectedRange.getCells());

            } else {
                isRangeSelected.setValue(false);
                unmarkRangeCells(oldValue.getCells());
            }
        });


    }

    public void setPermissions(SimpleBooleanProperty isReader, SimpleBooleanProperty isWriter, SimpleBooleanProperty isOwner) {
        deleteRangeButton.disableProperty().bind(
                (isOwner.not().and(isWriter.not()))
        );
        newRangeButton.disableProperty().bind(
                (isOwner.not().and(isWriter.not()))
        );
        editRangeButton.disableProperty().bind(
                (isOwner.not().and(isWriter.not()))
        );
    }

    public ListView<RangeDTO> getRangesListView() {
        return rangesListView;
    }

    public void addRange(RangeDTO range) {
        rangeList.add(range);
    }

    private void initializeNewRangeDialog() {
        // Load the FXML file and create a scene for the new range prompt
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("newRangeDialog/newRangeDialog.fxml"));
            rangePromptRoot = loader.load();
            NewRangeController controller = loader.getController();
            controller.setRangesController(this);
            newRangeController = controller;
            controller.setRangePromptRoot(rangePromptRoot);
            controller.newRangeDialogSetup();

        } catch (IOException e) {
            myMainController.showLoadingError(e);
        }
    }

    public void setMyMainController(MainController myMainController) {
        this.myMainController = myMainController;
    }

    public MainController getMyMainController() {
        return myMainController;
    }

    public SheetDTO getMainSheet() {
        return myMainController.getCurrentSheet();
    }

    public void bindRangeList () {
        rangeList = FXCollections.observableArrayList();
        rangesListView.setItems(rangeList);

        SheetDTO currentSheet = myMainController.getCurrentSheet();

        rangeList.setAll(currentSheet.getRanges().values());

        ObservableMap<String, RangeDTO> observableRangesMap = FXCollections.observableMap(currentSheet.getRanges());
        observableRangesMap.addListener((MapChangeListener<String, RangeDTO>) change -> {
            if (change.wasAdded()) {
                rangeList.add(change.getValueAdded());
            } else if (change.wasRemoved()) {
                rangeList.remove(change.getValueRemoved());
            }
        });
    }

    public void setNewRangeList(Collection<RangeDTO> values) {
        rangeList.clear();
        rangeList.setAll(values);
    }

    @FXML
    private void handleDeleteRangeButton() {
        if (!myMainController.isLatestVersion()) {
            myMainController.showLoadingError(new IllegalStateException("New version available! Please update to the latest sheet version " +
                    "(through the button on the right of the action line) to enable editing."));
            return;
        }
        RangeDTO toDeleteRange = rangesListView.getSelectionModel().getSelectedItem();
        deleteRangeRequest(toDeleteRange);
    }

    private void deleteRangeRequest(RangeDTO toDeleteRange) {
        String sheetName = getMainSheet().getName();
        String selectedRangeName = toDeleteRange.getName();

        String finalUrl = HttpUrl
                .parse(Constants.RANGE_ACTION)
                .toString();

        // Create the POST body with the parameters as JSON or form-encoded data
        RequestBody body = new FormBody.Builder()
                .add("sheetName", sheetName)
                .add("action", "delete")
                .add("rangeName", selectedRangeName)
                .build();

        // Create the POST request with the body
        Request request = new Request.Builder()
                .url(finalUrl)
                .post(body) // Specify this is a POST request
                .build();

        HttpClientUtil.runAsyncRequest(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    myMainController.showLoadingError(e);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {  // Ensure the response body is closed
                    String responseMessage = responseBody != null ? responseBody.string() : "Empty response from server";
                    if (response.isSuccessful()) {
                        // If the server returned a valid RangeDTO
                        Platform.runLater(() -> {
                            rangeList.remove(toDeleteRange);
                            myMainController.updateEntireGrid();
                        });
                    } else {
                        // If the server returned an error message
                        Platform.runLater(() -> {
                            myMainController.showLoadingError(new IOException(responseMessage));
                        });
                    }
                } catch (Exception e) {
                    System.out.println("Error processing response: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleNewRangeButton() {
        if (!myMainController.isLatestVersion()) {
            myMainController.showLoadingError(new IllegalStateException("New version available! Please update to the latest sheet version " +
                    "(through the button on the right of the action line) to enable editing."));
            return;
        }

        if (newRangeController.getNewRangeStage() != null) {
            newRangeController.getNewRangeStage().show();
        }
    }

    private void unmarkRangeCells(Set<String> oldCells) {
        Map<String, Label> cell2Label = myMainController.getGridComponentController().getCell2Label();
        for (String cell : oldCells) {
            cell2Label.get(cell).setId(null);
        }
    }

    private void markRangeCells(Set<String> oldCells) {
        Map<String, Label> cell2Label = myMainController.getGridComponentController().getCell2Label();
        for (String cell : oldCells) {
            Label label = cell2Label.get(cell);
            label.setId("range-cell");
        }
    }

    public Parent getRangePromptRoot() {
        return rangePromptRoot;
    }

    public void unclickRange() {
        isRangeSelected.setValue(false);
        unmarkRangeCells(selectedRange.get().getCells());
    }


}
