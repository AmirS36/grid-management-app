package components.gridArea.ranges.newRangeDialog;

import components.gridArea.ranges.RangesController;
import coordinate.CoordinateDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.grid.SheetUtils;
import management.SheetManagerDTO;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import range.RangeDTO;
import sheet.SheetDTO;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;

import static util.Constants.GSON_INSTANCE;


public class NewRangeController {

    private RangesController rangesController;
    private Stage newRangeStage;
    private Parent rangePromptRoot;


    @FXML private Button newRangeCancelButton;
    @FXML private Button newRangeCreateButton;
    @FXML private TextField newRangeFromTF;
    @FXML private TextField newRangeNameTF;
    @FXML private TextField newRangeToTF;


    public void setRangesController(RangesController rangesController) {
        this.rangesController = rangesController;
    }

    public void setNewRangeStage(Stage newRangeStage) {
        this.newRangeStage = newRangeStage;
    }

    public Stage getNewRangeStage() {
        return newRangeStage;
    }

    public void setRangePromptRoot(Parent rangePromptRoot) {
        this.rangePromptRoot = rangePromptRoot;
    }

    public void newRangeDialogSetup() {
        newRangeStage = new Stage();
        newRangeStage.setScene(new Scene(rangePromptRoot));
        newRangeStage.setTitle("Create New Range");
        newRangeStage.initModality(Modality.APPLICATION_MODAL);
        newRangeStage.setResizable(false);
    }

    @FXML
    private void newRangeCancelButtonAction() {
        newRangeStage.close();
    }


    @FXML
    private void newRangeCreateButtonAction() {
        String fromCoord = newRangeFromTF.getText().toUpperCase();
        String toCoord = newRangeToTF.getText().toUpperCase();
        String rangeName = newRangeNameTF.getText();

        if (fromCoord.isEmpty() || toCoord.isEmpty() || rangeName.isEmpty()) {
            showError("All fields must be filled out.");
            return;
        }
        try {
            SheetDTO currentSheet = rangesController.getMainSheet();
            CoordinateDTO from = SheetUtils.convertCellIdToCoordinate(fromCoord);
            CoordinateDTO to = SheetUtils.convertCellIdToCoordinate(toCoord);

            // Check if the coordinates are within the sheet's boundaries
            currentSheet.isWithinBounds(from);
            currentSheet.isWithinBounds(to);

            // Check if the range name already exists
            if (currentSheet.getRanges().containsKey(rangeName)) {
                showError("A range with this name already exists.");
                return;
            }

            // Create the range and add it to the sheet
            addRangeRequest(rangeName,fromCoord,toCoord);

            newRangeFromTF.setText("");
            newRangeToTF.setText("");
            newRangeNameTF.setText("");

            newRangeStage.close();

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void addRangeRequest (String rangeName, String fromCoord, String toCoord) {
        String sheetName = rangesController.getMainSheet().getName();

        String finalUrl = HttpUrl
                .parse(Constants.RANGE_ACTION)
                .toString();

        RequestBody body = new FormBody.Builder()
                .add("sheetName", sheetName)
                .add("action", "add")
                .add("rangeName", rangeName)
                .add("fromCoord", fromCoord)
                .add("toCoord", toCoord)
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
                    showError(e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {  // Ensure response body is closed
                    if (responseBody == null) {
                        System.out.println("Empty response from server.");
                        return;
                    }

                    String responseBodyString = responseBody.string();
                    if (response.isSuccessful()) {
                        RangeDTO rangeDTO = GSON_INSTANCE.fromJson(responseBodyString, RangeDTO.class);

                        Platform.runLater(() -> {
                            rangesController.addRange(rangeDTO);
                        });
                    } else {
                        Platform.runLater(() -> {
                            showError(responseBody.toString());
                        });
                    }
                } catch (Exception e) {
                    System.out.println("Error processing response: " + e.getMessage());
                }
            }
        });
    }


}
