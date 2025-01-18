package components.gridArea.commands;

import cell.CellDTO;
import components.gridArea.base.MainController;
import components.gridArea.grid.GridController;
import coordinate.CoordinateDTO;
import coordinate.CoordinateDTOFactory;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.grid.GridService;
import logic.grid.SheetUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import resources.Resources;
import sheet.SheetDTO;
import util.Constants;
import util.http.HttpClientUtil;


import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class CommandsCenterController {

    private MainController myMainController;
    private Stage newGridStage;
    private GridService gridService;

    //Tab 1 - Design
    @FXML private Tab settingTabButton;
    @FXML private VBox visualSettingVbox;
    @FXML private HBox gridSettingsHbox;
    @FXML private Button CenterAlignmentButton;
    @FXML private Button leftAlignmentButton;
    @FXML private Button rightAlignmentButton;
    @FXML private HBox alignmentButtons;
    @FXML private Spinner<Integer> rowHeightSpinner;
    @FXML private Spinner<Integer> columnWidthSpinner;

    @FXML private HBox cellDesignHbox;
    @FXML private ColorPicker mainColorPicker;
    @FXML private ColorPicker backgroundColorPicker;


    //Tab 2 - Sorting
    @FXML private Tab sortingTabButton;
    @FXML private TextField sortingFromTF;
    @FXML private TextField sortingToTF;
    @FXML private TextField sortingColumnsTF;
    @FXML private Button viewSortButton;

    //Tab 3 - Filtering
    private List<List<CellDTO>> rowsToFilter = new ArrayList<>();
    
    @FXML private TextField filterFromTF;
    @FXML private TextField filterToTF;
    @FXML private TextField filterColumnsTF;
    @FXML private Button filterButton;

    @FXML private Label uniqueValuesHeader;
    @FXML private ListView<CheckBox> uniqueValuesLV;
    @FXML private Button applyFilterButton;
    @FXML private Button cancelFilterButton;

    int startRow;
    int startCol;

    //Tab 4 - Value Simulation
    @FXML private VBox valueSimultationVbox;
    @FXML private TextField minRangeTF;
    @FXML private TextField maxRangeTF;
    @FXML private TextField stepTF;
    @FXML private Button showSliderButton;
    @FXML private Slider vsSlider;

    private Boolean isSimulateReady = false;

    //Tab 5 - Visual
    @FXML private ComboBox<String> themesComboBox;
    @FXML private ToggleButton animationsToggleButton;
    private boolean animationsEnabled = false;


    @FXML
    private void initialize() {
        gridService = new GridService();

        if (columnWidthSpinner.getValueFactory() == null) {
            columnWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 150, 50, 2));
        }

        columnWidthSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                GridController gridController = myMainController.getGridComponentController();
                ColumnConstraints columnConstraints = gridController.getSelectedColumnConstraint();
                if (columnConstraints != null) {
                    updateColumnConstraints(columnConstraints, newValue);
                }
            }
        });

        if (rowHeightSpinner.getValueFactory() == null) {
            rowHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 150, 50, 2));
        }

        rowHeightSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                GridController gridController = myMainController.getGridComponentController();
                RowConstraints rowConstraints = gridController.getSelectedRowConstraint();
                if (rowConstraints != null) {
                    updateRowConstraints(rowConstraints, newValue);
                }
            }
        });

        vsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Snap to the nearest tick based on stepValue
            double stepValue = vsSlider.getMajorTickUnit(); // Assuming major tick unit is the step
            double snappedValue = Math.round(newValue.doubleValue() / stepValue) * stepValue;

            // Update the slider value if it's not on the tick
            if (newValue.doubleValue() != snappedValue) {
                vsSlider.setValue(snappedValue);
            }

            // Format the snapped value to one decimal place
            DecimalFormat decimalFormat = new DecimalFormat("#.0");
            String formattedValue = decimalFormat.format(snappedValue);

            // Get the selected cell and update with the formatted value
            String selectedCell = myMainController.getActionLineComponentController().getSelectedCell();
            myMainController.getGridComponentController().updateCell(selectedCell, formattedValue, "Soft");
        });

    }

    public void setPermissions(SimpleBooleanProperty isReader, SimpleBooleanProperty isWriter, SimpleBooleanProperty isOwner) {
        visualSettingVbox.disableProperty().bind(
                (isOwner.not().and(isWriter.not()))
        );
        valueSimultationVbox.disableProperty().bind(
                (isOwner.not().and(isWriter.not()))
        );
    }

    public void bindCommandsCenter(SimpleBooleanProperty isColumnSelected, SimpleBooleanProperty isRowSelected, SimpleBooleanProperty isCellSelected) {
        rowHeightSpinner.disableProperty().bind(isRowSelected.not());
        columnWidthSpinner.disableProperty().bind(isColumnSelected.not());
        alignmentButtons.disableProperty().bind(isColumnSelected.not());
        cellDesignHbox.disableProperty().bind(isCellSelected.not());

        themesComboBox.getItems().add("Default");
        themesComboBox.getItems().add("Greenish");
        themesComboBox.getItems().add("Light");
    }

    public void setMainController(MainController myMainController) {
        this.myMainController = myMainController;
    }

    public Spinner<Integer> getColumnWidthSpinner() {
        return columnWidthSpinner;
    }

    public Spinner<Integer> getRowHeightSpinner() {
        return rowHeightSpinner;
    }

    //Actions ----------------------------------------------------------------------------------------------------------------------------------
    //TAB 1 - Settings
    @FXML
    private void leftAlButtonAction(ActionEvent event) {
        GridController gridController = myMainController.getGridComponentController();

        if (gridController.getSelectedColumn() != null) {
            int columnIndex = gridController.getSelectedColumnIndex();
            applyAlignmentToColumn(columnIndex, Pos.CENTER_LEFT);
        }
    }

    @FXML
    private void centerAlButtonAction(ActionEvent event) {
        GridController gridController = myMainController.getGridComponentController();

        if (gridController.getSelectedColumn() != null) {
            int columnIndex = gridController.getSelectedColumnIndex();
            applyAlignmentToColumn(columnIndex, Pos.CENTER);
        }
    }

    @FXML
    private void rightAlButtonAction(ActionEvent event) {
        GridController gridController = myMainController.getGridComponentController();

        if (gridController.getSelectedColumn() != null) {
            int columnIndex = gridController.getSelectedColumnIndex();
            applyAlignmentToColumn(columnIndex, Pos.CENTER_RIGHT);
        }
    }

    private void applyAlignmentToColumn(int columnIndex, Pos alignment) {
        GridPane gridPane = myMainController.getMainGrid();
        GridController gridController = myMainController.getGridComponentController();
        for (int row = 0; row < gridPane.getRowCount(); row++) {
            Label label = gridController.getLabelAt(row, columnIndex - 1); // You can create a helper method to retrieve the label
            if (label != null) {
                label.setAlignment(alignment);
            }
        }
    }

    private void updateColumnConstraints(ColumnConstraints selectedColumn, double width) {
        selectedColumn.setMinWidth(width);
        selectedColumn.setPrefWidth(width);
        selectedColumn.setMaxWidth(width);
    }

    private void updateRowConstraints(RowConstraints rowConstraints, double height) {
        rowConstraints.setMinHeight(height);
        rowConstraints.setPrefHeight(height);
        rowConstraints.setMaxHeight(height);
    }

    //TODO - UPDATE COLOR METHOD
    @FXML
    private void mainColorAction (ActionEvent event) {
        GridController gridController = myMainController.getGridComponentController();
        Label label = gridController.getSelectedCell();
        CellDTO currentCell = gridController.getCellFromLabel(label);

        Color selectedColor = mainColorPicker.getValue();
        // Convert the Color to a CSS-compatible string
        String colorHex = String.format("#%02X%02X%02X",
                (int) (selectedColor.getRed() * 255),
                (int) (selectedColor.getGreen() * 255),
                (int) (selectedColor.getBlue() * 255));

        String currentStyle = label.getStyle();
        label.setStyle(currentStyle + "-fx-text-fill: " + colorHex + ";");

        cellStyleChangeRequest(currentCell, colorHex, "MainColor");
    }


    @FXML
    private void backgroundColorAction (ActionEvent event) {
        GridController gridController = myMainController.getGridComponentController();
        Label label = gridController.getSelectedCell();
        CellDTO currentCell = gridController.getCellFromLabel(label);


        Color selectedColor = backgroundColorPicker.getValue();
        // Convert the Color to a CSS-compatible string
        String colorHex = String.format("#%02X%02X%02X",
                (int) (selectedColor.getRed() * 255),
                (int) (selectedColor.getGreen() * 255),
                (int) (selectedColor.getBlue() * 255));

        String currentStyle = label.getStyle();
        label.setStyle(currentStyle + "-fx-background-color: " + colorHex + ";");

        cellStyleChangeRequest(currentCell, colorHex, "BackgroundColor");
    }

    @FXML
    private void cleanDesignButtonAction(ActionEvent event) {
        GridController gridController = myMainController.getGridComponentController();
        Label label = gridController.getSelectedCell();
        label.setStyle("");

        CellDTO currentCell = gridController.getCellFromLabel(label);

        cellStyleChangeRequest(currentCell," ", "Clear");
    }

    private void cellStyleChangeRequest(CellDTO currentCell, String colorHex, String updateType) {
        String sheetName = myMainController.getCurrentSheet().getName();

        String finalUrl = HttpUrl
                .parse(Constants.UPDATE_CELL_DESIGN)
                .toString();

        RequestBody body = new FormBody.Builder()
                .add("sheetName", sheetName)
                .add("cellID", currentCell.getCoordinate())
                .add("updateType", "updateType")
                .add("colorHex", colorHex)
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
                try (ResponseBody responseBody = response.body()) {  // Ensure response body is closed
                    if (responseBody == null) {
                        System.out.println("Empty response from server.");
                        return;
                    }
                    if (!response.isSuccessful()) {
                        Platform.runLater(() -> {
                            myMainController.showLoadingError(new Exception(responseBody.toString()));
                        });
                    }
                } catch (Exception e) {
                    System.out.println("Error processing response: " + e.getMessage());
                }
            }
        });
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    //TAB 2 - Sorting

    @FXML
    public void viewSortButtonAction(ActionEvent actionEvent) {
        String fromTF = sortingFromTF.getText();
        String toTF = sortingToTF.getText();
        String columns = sortingColumnsTF.getText().toUpperCase();

        try {
            if (fromTF.isEmpty() || toTF.isEmpty() || columns.isEmpty()) {
                throw new IllegalArgumentException("All fields must be filled");
            }

            CoordinateDTO from = SheetUtils.convertCellIdToCoordinate(fromTF);
            CoordinateDTO to = SheetUtils.convertCellIdToCoordinate(toTF);

            if (from.getRow() > to.getRow() || from.getCol() > to.getCol()) {
                throw new IllegalArgumentException("Invalid range input. " + from + " cant be bigger than " + to);
            }

            int startRow = from.getRow();
            int startCol = from.getCol();

            List<List<CellDTO>> rows = getRowsFromRange(from,to);
            int rangeStartColumn = from.getCol();

            String[] columnIndices = columns.split(",");
            validateColumnIndices(columnIndices, to.getCol() + 1);

            Comparator<List<CellDTO>> comparator = null;

            for (String columnIndexString : columnIndices) {
                int absoluteColumnIndex = SheetUtils.convertToColumnIndex(columnIndexString.trim());
                int relativeColumnIndex = absoluteColumnIndex - rangeStartColumn; // Relative to range

                Comparator<List<CellDTO>> columnComparator = (row1, row2) -> compareByColumn(row1, row2, relativeColumnIndex);

                if (comparator == null) {
                    comparator = columnComparator;  // Initial comparator (first column)
                } else {
                    comparator = comparator.thenComparing(columnComparator);  // Add secondary comparators (for ties)
                }
            }
            // Sort the rows based on the composite comparator
            if (comparator != null) {
                rows.sort(comparator);
            }

            if (animationsEnabled) {
                RotateTransition rotate = new RotateTransition(Duration.seconds(1), viewSortButton);
                rotate.setFromAngle(0);
                rotate.setToAngle(360);
                rotate.play();
            }

            SheetDTO displaySheet = createDisplaySheetFromSortedRows(rows);
            displaySheet(displaySheet, startRow, startCol);


        } catch (Exception e) {
            myMainController.showLoadingError(e);
        }
    }

    private void validateColumnIndices(String[] columnIndices, int maxColumns) {

        for (String column : columnIndices) {
            String trimmedColumn = column.trim().toUpperCase(); // Trim whitespace and convert to uppercase

            // Check if the column is a single letter
            if (trimmedColumn.length() != 1 || !Character.isLetter(trimmedColumn.charAt(0))) {
                throw new IllegalArgumentException("Invalid column index: " + column);
            }

            // Convert the letter to a numeric index (A=0, B=1, C=2, ...)
            int columnIndex = trimmedColumn.charAt(0) - 'A';

            // Check if the index is within the valid range
            if (columnIndex < 0 || columnIndex >= maxColumns) {
                throw new IllegalArgumentException("Column index " + trimmedColumn + " is out of bounds.");
            }
        }
    }

    private int compareByColumn(List<CellDTO> row1, List<CellDTO> row2, int relativeColumnIndex) {
        // Ensure the relative index is within bounds
        if (relativeColumnIndex < 0 || relativeColumnIndex >= row1.size()) {
            throw new IndexOutOfBoundsException("Invalid column index for this range.");
        }
        // Get the cell values for comparison
        CellDTO cell1 = row1.get(relativeColumnIndex);
        CellDTO cell2 = row2.get(relativeColumnIndex);

        String type1 = cell1.getEffectiveValue().getCellType();
        String type2 = cell2.getEffectiveValue().getCellType();

        // Prefer numeric over non-numeric
        if (type1.equals("NUMERIC") && !type2.equals("NUMERIC")) {
            return -1; // cell1 is preferred
        }
        if (!type1.equals("NUMERIC") && type2.equals("NUMERIC")) {
            return 1; // cell2 is preferred
        }
        if (!type1.equals("NUMERIC")) {
            return 0;
        }

        Double value1 = Double.parseDouble(cell1.getEffectiveValue().getValue());
        Double value2 = Double.parseDouble(cell2.getEffectiveValue().getValue());

        // Handle null values appropriately
        if (value1 == null && value2 == null) return 0;
        if (value1 == null) return -1; // Treat nulls as less than any number
        if (value2 == null) return 1;

        // Compare and return
        return value1.compareTo(value2);  // Sort from lowest to highest
    }

    private List<List<CellDTO>> getRowsFromRange(CoordinateDTO topLeft, CoordinateDTO bottomRight) {
        SheetDTO currentSheet = myMainController.getCurrentSheet();

        // Check if the coordinates are within the sheet's boundaries
        currentSheet.isWithinBounds(topLeft);
        currentSheet.isWithinBounds(bottomRight);

        List<List<CellDTO>> rows = new ArrayList<>();

        // Get the start and end row and column indices
        int startRow = topLeft.getRow();
        int endRow = bottomRight.getRow();
        int startCol = topLeft.getCol();
        int endCol = bottomRight.getCol();

        // Iterate through the specified range and collect rows
        for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
            List<CellDTO> currentRow = new ArrayList<>();

            for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
                // Retrieve the cell at the current row and column from the sheet
                CellDTO cell = currentSheet.getCell(CoordinateDTOFactory.createCoordinate(rowIndex,colIndex));
                currentRow.add(cell);
            }

            rows.add(currentRow); // Add the completed row to the list
        }

        return rows; // Return the list of rows
    }

    public SheetDTO createDisplaySheetFromSortedRows(List<List<CellDTO>> sortedRows) {
        int rowCount = sortedRows.size();
        int colCount = sortedRows.isEmpty() ? 0 : sortedRows.getFirst().size();

        // Prepare the cells map
        Map<String, CellDTO> cellsMap = new HashMap<>();

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            List<CellDTO> row = sortedRows.get(rowIndex);
            for (int colIndex = 0; colIndex < colCount; colIndex++) {
                CellDTO cell = row.get(colIndex);
                CoordinateDTO coordinate = CoordinateDTOFactory.createCoordinate(rowIndex, colIndex); // Assuming top-left starts at (0, 0)
                cellsMap.put(coordinate.toString(), cell); // Add the cell to the map with its coordinate
            }
        }
        // Create the new SheetImpl object with default values for non-relevant parameters
        return new SheetDTO("display", rowCount, colCount,20,100,0,0,cellsMap,null,null);
    }

    private void displaySheet(SheetDTO sheet, int startRow, int startCol) {

        GridPane viewOnlyGrid = gridService.createViewOnlyGrid(sheet, startRow, startCol);
        newGridStage = new Stage();
        newGridStage.setScene(new Scene(viewOnlyGrid));
        newGridStage.setTitle("New Sort");
        newGridStage.initModality(Modality.APPLICATION_MODAL);
        newGridStage.setResizable(false);
        newGridStage.showAndWait();
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    //TAB 3 - Filtering

    @FXML
    public void filterButtonAction(ActionEvent actionEvent) {
        String fromTF = filterFromTF.getText();
        String toTF = filterToTF.getText();
        String columns = filterColumnsTF.getText().toUpperCase();

        try {
            if (fromTF.isEmpty() || toTF.isEmpty() || columns.isEmpty()) {
                throw new IllegalArgumentException("All fields must be filled");
            }
            CoordinateDTO from = SheetUtils.convertCellIdToCoordinate(fromTF);
            CoordinateDTO to = SheetUtils.convertCellIdToCoordinate(toTF);

            startRow = from.getRow();
            startCol = from.getCol();

            if (from.getRow() > to.getRow() || from.getCol() > to.getCol()) {
                throw new IllegalArgumentException("Invalid range input. " + from + " cant be bigger than " + to);
            }

            rowsToFilter = getRowsFromRange(from, to);
            List<Integer> columnIndices = parseColumns(columns, from.getCol(), to.getCol());
            Set<String> uniqueValues = getUniqueValues(rowsToFilter, columnIndices);

            if (animationsEnabled) {
                RotateTransition rotate = new RotateTransition(Duration.seconds(1), filterButton);
                rotate.setFromAngle(0);
                rotate.setToAngle(360);
                rotate.play();
            }

            showFilterCheckboxes(uniqueValues);

        } catch (Exception e) {
            myMainController.showLoadingError(e);
        }
    }

    private List<Integer> parseColumns(String columns, int rangeStartCol, int rangeEndCol) {
        String[] columnArray = columns.split(",");
        List<Integer> columnIndices = new ArrayList<>();

        for (String col : columnArray) {
            int absoluteColumnIndex = SheetUtils.convertToColumnIndex(col.trim());
            if (absoluteColumnIndex < rangeStartCol || absoluteColumnIndex > rangeEndCol) {
                throw new IllegalArgumentException("Column " + col + " is out of range.");
            }
            // Calculate the relative column index
            int relativeColumnIndex = absoluteColumnIndex - rangeStartCol;

            columnIndices.add(relativeColumnIndex);
        }

        return columnIndices;
    }

    private Set<String> getUniqueValues(List<List<CellDTO>> rows, List<Integer> columnIndices) {
        Set<String> uniqueValues = new HashSet<>();

        for (List<CellDTO> row : rows) {
            for (int colIndex : columnIndices) {
                CellDTO cell = row.get(colIndex);
                String cellValue = cell.getEffectiveValue().getValue().toString();
                uniqueValues.add(cellValue);
            }
        }

        return uniqueValues;
    }

    private void toggleFilteringSelection() {
        uniqueValuesHeader.setVisible(!uniqueValuesHeader.isVisible());
        uniqueValuesLV.getItems().clear();
        uniqueValuesLV.setVisible(!uniqueValuesLV.isVisible());
        applyFilterButton.setVisible(!applyFilterButton.isVisible());
        cancelFilterButton.setVisible(!cancelFilterButton.isVisible());

        filterFromTF.setDisable(!filterFromTF.isDisable());
        filterToTF.setDisable(!filterToTF.isDisable());
        filterColumnsTF.setDisable(!filterColumnsTF.isDisable());
        filterButton.setDisable(!filterButton.isDisable());

        settingTabButton.setDisable(!settingTabButton.isDisabled());
        sortingTabButton.setDisable(!sortingTabButton.isDisabled());

        myMainController.toggleAllButCommandsCenter();

    }

    private void showFilterCheckboxes(Set<String> uniqueValues) {
        toggleFilteringSelection();

        for (String value : uniqueValues) {
            CheckBox checkBox = new CheckBox(value);
            uniqueValuesLV.getItems().add(checkBox);
        }

    }

    public void cancelFilterButtonAction(ActionEvent actionEvent) {
        toggleFilteringSelection();
    }

    public void ApplyFilterButtonAction(ActionEvent actionEvent) {
        List<String> selectedValues = getSelectedValues();
        List<List<CellDTO>> filteredRows = getFilteredRows(selectedValues);
        SheetDTO displaySheet = createDisplaySheetFromSortedRows(filteredRows);
        displaySheet(displaySheet,startRow,startCol);
    }

    private List<String> getSelectedValues () {
        List<String> selectedValues = new ArrayList<>();
        for (CheckBox checkBox : uniqueValuesLV.getItems()) {
            if (checkBox.isSelected()) {
                selectedValues.add(checkBox.getText());  // Assuming the text of the CheckBox is the unique value
            }
        }
        return selectedValues;
    }
    
    private List<List<CellDTO>> getFilteredRows (List<String> selectedValues) {
        List<List<CellDTO>> filteredRows = new ArrayList<>();

        for (List<CellDTO> row : rowsToFilter) {
            boolean rowMatches = false;
            for (CellDTO cell : row) {
                String cellValue = cell.getEffectiveValue().getValue().toString();  // Or however you retrieve the string value of the cell
                if (selectedValues.contains(cellValue)) {
                    rowMatches = true;
                    break;  // No need to check other cells in this row if one matches
                }
            }
            if (rowMatches) {
                filteredRows.add(row);  // Add this row to the filtered list if any cell matches
            }
        }
        return filteredRows;
    }

    // Tab 4 - Value Simulation

    public void showSliderButtonAction(ActionEvent actionEvent) {
        String minRange = minRangeTF.getText();
        String maxRange = maxRangeTF.getText();
        String step = stepTF.getText();

        Label selectedCellLabel = myMainController.getGridComponentController().getSelectedCell();
        CellDTO selectedCell = myMainController.getGridComponentController().getCellFromLabel(selectedCellLabel);

        try {
            // Check if fields are filled
            if (minRange.isEmpty() || maxRange.isEmpty() || step.isEmpty()) {
                throw new IllegalArgumentException("All fields must be filled");
            }

            // Check if a cell is selected
            if (selectedCell == null) {
                throw new IllegalArgumentException("You must select a cell");
            }

            // Verify if values are numeric
            double minValue, maxValue, stepValue;
            try {
                minValue = Double.parseDouble(minRange);
                maxValue = Double.parseDouble(maxRange);
                stepValue = Double.parseDouble(step);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Range, step and cell values must be numeric");
            }



            // Verify the selected cell’s value is numeric
            if (selectedCell.getOriginalValue() == null || !isNumeric(selectedCell.getOriginalValue())) {
                throw new IllegalArgumentException("Selected cell must contain a numeric value");
            }

            // Set up the slider range and step increment
            vsSlider.setVisible(true);
            vsSlider.setMin(minValue);
            vsSlider.setMax(maxValue);
            vsSlider.setBlockIncrement(stepValue);
            vsSlider.setValue(Double.parseDouble(selectedCell.getOriginalValue())); // Set slider to cell's current value
            vsSlider.setMajorTickUnit(stepValue);
            vsSlider.setMinorTickCount(0);
            vsSlider.setSnapToTicks(true);

            this.isSimulateReady = true;

        } catch (Exception e) {
            myMainController.showLoadingError(e);
        }
    }

    private boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void resetVirtualSimulation () {
        vsSlider.setVisible(false);
        minRangeTF.setText("");
        maxRangeTF.setText("");
        stepTF.setText("");
    }

    public Double getSliderValue () {
        return vsSlider.getValue();
    }

    public Boolean isSimulateReady () {
        return isSimulateReady;
    }

    public void setIsSimulateReady (Boolean isSimulateReady) {
        this.isSimulateReady = isSimulateReady;
    }


    // Tab 5 - Visual
    public void animationsToggleButtonAction(ActionEvent actionEvent) {
        animationsEnabled = animationsToggleButton.isSelected();
        if (animationsEnabled) {
            animationsToggleButton.setText("ON");
        }
        else {
            animationsToggleButton.setText("OFF");
        }
    }

    public boolean isAnimationsEnabled () {
        return animationsEnabled;
    }


    //Others
    public void settingTabButtonAction(Event event) {
    }

    public void filteringTabButtonAction(Event event) {
        myMainController.getGridComponentController().unclickCell();
    }

    public void sortingTabButtonAction(Event event) {
        myMainController.getGridComponentController().unclickCell();
    }

    public void visualTabButtonAction(Event event) {
        myMainController.getGridComponentController().unclickCell();
    }


    public void themesComboBoxAction(ActionEvent actionEvent) {
        String selectedItem = themesComboBox.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            switch (selectedItem) {
                case "Default":
                    changeTheme("/resources/themes/default.css");
                    break;
                case "Greenish":
                    changeTheme("/resources/themes/greenish.css");
                    break;
                case "Light":
                    changeTheme("/resources/themes/light-theme.css");
                    break;
                default:
                    // Handle any unexpected cases if needed
                    break;
            }
        }
    }

    private void changeTheme (String cssFile) {
        Resources.setCurrentTheme(cssFile);

        ScrollPane scrollPane = myMainController.getMainScrollPane();
        scrollPane.getStylesheets().clear();
        scrollPane.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());

        GridPane mainGrid = myMainController.getMainGrid();
        mainGrid.getStylesheets().clear();
        mainGrid.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());

        Parent rangePromptRoot = myMainController.getRangesComponentController().getRangePromptRoot();
        rangePromptRoot.getStylesheets().clear();
        rangePromptRoot.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());

        Pane versionSelectorComponent = myMainController.getVersionSelectorComponent();
        versionSelectorComponent.getStylesheets().clear();
        versionSelectorComponent.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
    }



}
