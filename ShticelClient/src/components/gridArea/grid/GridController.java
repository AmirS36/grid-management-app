package components.gridArea.grid;

import cell.CellDTO;
import javafx.application.Platform;
import javafx.scene.control.Cell;
import management.SheetManagerDTO;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import sheet.SheetDTO;
import components.gridArea.base.MainController;
import coordinate.CoordinateDTO;
import coordinate.CoordinateDTOFactory;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.util.Duration;
import logic.grid.GridService;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static util.Constants.GSON_INSTANCE;

public class GridController {

    //Controllers
    private MainController myMainController;

    //Utilities
    private GridService gridService = new GridService();
    private SheetDTO currentSheet;

    private Map<CoordinateDTO, StringProperty> cell2TextProperty;
    private Map<String, Label> cell2Label;

    private ObjectProperty<Label> selectedCell;
    private ObjectProperty<Label> selectedColumn;
    private ObjectProperty<Label> selectedRow;

    private String lastCoordinateClicked;


    @FXML
    public void initialize() {
        selectedCell = new SimpleObjectProperty<>();
        selectedCell.set(null);
        selectedCell.addListener((observableValue, oldLabelSelection, newSelectedLabel) -> {
            if (oldLabelSelection != null) {
                oldLabelSelection.setId(null);
            }
            if (newSelectedLabel != null) {
                newSelectedLabel.setId("selected-cell");
            }
            Double sliderValue = myMainController.getCommandsCenterController().getSliderValue();
            Boolean simulateReady = myMainController.getCommandsCenterController().isSimulateReady();

            if (sliderValue != null && simulateReady) {
                myMainController.updateCell(myMainController.getActionLineComponentController().getSelectedCell(), String.valueOf(sliderValue));
                myMainController.getCommandsCenterController().setIsSimulateReady(false);
            }
            myMainController.getCommandsCenterController().resetVirtualSimulation();

        });


        selectedColumn = new SimpleObjectProperty<>();
        selectedColumn.addListener((observableValue, oldLabelSelection, newSelectedLabel) -> {
            if (oldLabelSelection != null) {
                oldLabelSelection.setId(null);
            }
            if (newSelectedLabel != null) {
                newSelectedLabel.setId("selected-cell");
            }
        });
        selectedColumn.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Get constraints from the GridPane
                ColumnConstraints columnConstraints = getSelectedColumnConstraint();
                if (columnConstraints != null) {
                    // Assuming the rowHeightSpinner is set from the CommandsCenter
                    myMainController.getCommandsCenterController().getColumnWidthSpinner().getValueFactory().setValue((int) columnConstraints.getPrefWidth());
                }
            }
        });

        selectedRow = new SimpleObjectProperty<>();
        selectedRow.addListener((observableValue, oldLabelSelection, newSelectedLabel) -> {
            if (oldLabelSelection != null) {
                oldLabelSelection.setId(null);
            }
            if (newSelectedLabel != null) {
                newSelectedLabel.setId("selected-cell");
            }
        });
        selectedRow.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Get constraints from the GridPane
                RowConstraints rowConstraints = getSelectedRowConstraint();
                if (rowConstraints != null) {
                    // Assuming the rowHeightSpinner is set from the CommandsCenter
                    myMainController.getCommandsCenterController().getRowHeightSpinner().getValueFactory().setValue((int) rowConstraints.getPrefHeight());
                }
            }
        });
    }

    //Getters and Setters --------------------------------------------------------
    public void setMainController(MainController myMainController) {
        this.myMainController = myMainController;
    }

    public SheetDTO getCurrentSheet() {
        return currentSheet;
    }

    public void setCurrentSheet(SheetDTO currentSheet) {
        this.currentSheet = currentSheet;
        myMainController.setGridComponent(gridService.createGrid(currentSheet, this));
        myMainController.setIsGriPresent(true);
    }

    public void setCell2TextProperty(Map<CoordinateDTO, StringProperty> cell2TextProperty) {
        this.cell2TextProperty = cell2TextProperty;
    }

    public void setCell2Label(Map<String, Label> cell2Label) {
        this.cell2Label = cell2Label;
    }

    public Map<String, Label> getCell2Label() {
        return cell2Label;
    }

    public Label getSelectedCell() {
        return selectedCell.get();
    }

    public Label getSelectedColumn() {
        return selectedColumn.get();
    }



    //Actions --------------------------------------------------------
    public void handleCellClick(CoordinateDTO coordinate, Label cellLabel) {
        if (selectedCell.get() != null) {
            CellDTO previousCell = getCellFromLabel(selectedCell.get());
            unmarkCellFriends(previousCell);
        }

        CellDTO currentCell = getCurrentSheet().getCell(coordinate);
        if (currentCell == null) {
            currentCell = new CellDTO(coordinate.toString(),"");
        }
        selectedCell.set(cellLabel);
        myMainController.updateActionLine(currentCell);
        markCellFriends(currentCell);

        myMainController.setIsCellPresent(true);
        myMainController.setIsColumnSelected(false);
        myMainController.setIsRowSelected(false);

        selectedColumn.set(null);
        selectedRow.set(null);

    }

    public void updateCell(String cellID, String value, String type) {
        updateCellRequest(cellID, value, type);
        myMainController.updateActionLine(currentSheet.getCell(cellID));
    }

    private void updateCellRequest(String cellID, String value, String type) {
        String sheetName = currentSheet.getName();

        String finalUrl = HttpUrl
                .parse(Constants.UPDATE_SHEET_CELL)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .addQueryParameter("cellID", cellID)
                .addQueryParameter("value", value)
                .addQueryParameter("type", type)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        HttpClientUtil.runAsyncRequest(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    // Handle failure (e.g., show an alert to the user)
                    myMainController.showLoadingError(e);
                    System.out.println("Failed to load sheet: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    SheetManagerDTO sheetManagerDTO = GSON_INSTANCE.fromJson(responseBody, SheetManagerDTO.class);

                    Platform.runLater(() -> {
                        myMainController.updateSheetManger(sheetManagerDTO);
                        //myMainController.updateActionLine(new CellDTO(cellID, value));
                    });
                } else {
                    myMainController.showLoadingError(new IOException(response.body().string()));
                }
            }
        });
    }

    public void updateEntireGrid() {
        for (Map.Entry<CoordinateDTO, StringProperty> entry : cell2TextProperty.entrySet()) {
            CoordinateDTO coordinate = entry.getKey();
            StringProperty textProperty = entry.getValue();

            CellDTO currentCell = getCurrentSheet().getCell(coordinate);
            if (currentCell == null) {
                currentCell = new CellDTO(coordinate.toString(), " ");
            }
            textProperty.set(currentCell.getEffectiveValue().getValue());
        }
    }

    public void handleColumnClick(String columnName, Label ColumnLabel) {
        System.out.println("Column clicked");
        unclickCell();
        selectedColumn.set(ColumnLabel);

        myMainController.setIsColumnSelected(true);
        myMainController.setIsCellPresent(false);
        myMainController.setIsRowSelected(false);

        selectedCell.set(null);
        selectedRow.set(null);

        myMainController.updateActionLine(MainController.theEmptyCell);
    }

    public void handleRowClick(String columnName, Label ColumnLabel) {
        System.out.println("Row clicked");
        unclickCell();
        selectedRow.set(ColumnLabel);

        myMainController.setIsRowSelected(true);
        myMainController.setIsColumnSelected(false);
        myMainController.setIsCellPresent(false);

        selectedCell.set(null);
        selectedColumn.set(null);

        myMainController.updateActionLine(MainController.theEmptyCell);
    }

    public ColumnConstraints getSelectedColumnConstraint() {

        GridPane gridPane = myMainController.getMainGrid();
        Integer colIndex = GridPane.getColumnIndex(selectedColumn.get());

        if (colIndex != null && colIndex > 0) {
            return gridPane.getColumnConstraints().get(colIndex);
        }
        return null;
    }

    public RowConstraints getSelectedRowConstraint() {

        GridPane gridPane = myMainController.getMainGrid();
        Integer rowIndex = GridPane.getRowIndex(selectedRow.get());

        if (rowIndex != null && rowIndex > 0) {
            return gridPane.getRowConstraints().get(rowIndex);
        }
        return null;
    }

    public int getSelectedColumnIndex() {
        Label label = selectedColumn.getValue();
        return GridPane.getColumnIndex(label);
    }

    public Label getLabelAt(int row, int col) {
        CoordinateDTO coord = CoordinateDTOFactory.createCoordinate(row,col);
        return cell2Label.get(coord.toString());
    }

    public CellDTO getCellFromLabel(Label label) {
        Integer rowIndex = GridPane.getRowIndex(label);
        Integer colIndex = GridPane.getColumnIndex(label);
        CoordinateDTO coord = CoordinateDTOFactory.createCoordinate(rowIndex - 1,colIndex - 1);
        return getCurrentSheet().getCell(coord);
    }

    private void markCellFriends(CellDTO currentCell) {
        if (currentCell == null || currentCell.getOriginalValue().equals("")) {
            return;
        }
        List<String> dependsOn = currentCell.getDependsOn();
        for (String dependsOnCell : dependsOn) {
            cell2Label.get(dependsOnCell).setId("selected-cell-depends-on");
        }

        List<String> influencingOn = currentCell.getInfluencingOn();
        for (String influencingCell : influencingOn) {
            cell2Label.get(influencingCell).setId("selected-cell-influencing-on");
        }
    }

    private void unmarkCellFriends(CellDTO currentCell) {
        if (currentCell == null || currentCell.getOriginalValue().equals("")) {
            return;
        }
        List<String> dependsOn = currentCell.getDependsOn();
        for (String dependsOnCell : dependsOn) {
            cell2Label.get(dependsOnCell).setId(null);
        }

        List<String> influencingOn = currentCell.getInfluencingOn();
        for (String influencingCell : influencingOn) {
            cell2Label.get(influencingCell).setId(null);
        }
    }

    public void unclickCell() {
        if (selectedCell.get() != null) {
            unmarkCellFriends(getCellFromLabel(selectedCell.get()));

            myMainController.setIsRowSelected(false);
            myMainController.setIsColumnSelected(false);
            myMainController.setIsCellPresent(false);

            selectedCell.set(null);
            selectedRow.set(null);
            selectedColumn.set(null);

            myMainController.updateActionLine(MainController.theEmptyCell);
        }
    }

}
