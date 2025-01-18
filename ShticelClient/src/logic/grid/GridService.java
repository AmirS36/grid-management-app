package logic.grid;

import cell.CellDTO;
import components.gridArea.grid.GridController;
import coordinate.CoordinateDTO;
import coordinate.CoordinateDTOFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import resources.Resources;
import sheet.SheetDTO;

import java.util.HashMap;
import java.util.Map;

public class GridService {

    public GridPane createGrid(SheetDTO sheet, GridController myGridController) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setGridLinesVisible(true);
        gridPane.setPrefWidth(600);
        gridPane.setMaxHeight(400);
        gridPane.setPadding(new Insets(20.0, 20.0, 20.0, 20.0));
        int colWidth = sheet.getColWidth();
        int rowHeight = sheet.getRowHeight();

        // Adding Column Constraints
        for (int col = 0; col <= sheet.getColsLength(); col++) { // Adjust for headers
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setHalignment(HPos.CENTER);
            colConstraints.setHgrow(Priority.ALWAYS);
            colConstraints.setMinWidth(50);
            colConstraints.setPrefWidth(100);
            if (col == 0) {
                colConstraints.setMaxWidth(120); // Special handling for the first column
            }
            gridPane.getColumnConstraints().add(colConstraints);
        }

        // Adding Row Constraints
        for (int row = 0; row <= sheet.getRowsLength(); row++) { // Adjust for headers
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.ALWAYS);
            rowConstraints.setMinHeight(50);
            rowConstraints.setPrefHeight(50);
            if (row == 0) {
                rowConstraints.setMaxHeight(80); // Special handling for the first row
            }
            gridPane.getRowConstraints().add(rowConstraints);
        }

        // Create headers for the first row and column
        for (int col = 1; col <= sheet.getColsLength(); col++) {
            String columnName = Character.toString((char) ('A' + col - 1)); // Convert to column names A, B, C...
            Label headerLabel = createHeaderLabel(columnName);

            headerLabel.setOnMouseClicked(event -> {
                myGridController.handleColumnClick(columnName, headerLabel);

            });

            gridPane.add(headerLabel, col, 0);
        }
        Label rootLabel = createHeaderLabel("");
        gridPane.add(rootLabel, 0, 0);

        for (int row = 1; row <= sheet.getRowsLength(); row++) {
            Label headerLabel = createHeaderLabel(Integer.toString(row)); // Row numbers

            final int currentRow = row;
            headerLabel.setOnMouseClicked(event -> {
                myGridController.handleRowClick(Integer.toString(currentRow), headerLabel);

            });

            gridPane.add(headerLabel, 0, row);
        }

        // This map will hold the StringProperty for each coordinate
        Map<CoordinateDTO, StringProperty> cellTextProperties = new HashMap<>();
        Map<String, Label> cell2Label = new HashMap<>();

        // Populate cells with data from the sheet
        Map<String, CellDTO> activeCells = sheet.getActiveCells();
        for (int row = 1; row <= sheet.getRowsLength(); row++) {
            for (int col = 1; col <= sheet.getColsLength(); col++) {
                CoordinateDTO coordinate = CoordinateDTOFactory.createCoordinate(row - 1, col - 1);

                StringProperty cellProperty = new SimpleStringProperty();
                cellTextProperties.put(coordinate, cellProperty);

                CellDTO cell = sheet.getCell(coordinate);
                // Check if the cell exists in the activeCells map and bind its text
                if (cell == null) {
                    cell = new CellDTO(coordinate.toString(), " ");
                }

                Label cellLabel = createGridLabel(cell);

                // Set the initial value of the StringProperty to the effective value of the cell
                cellProperty.set(cell.getEffectiveValue().getValue());

                cellLabel.setOnMouseClicked(event -> {
                    myGridController.handleCellClick(coordinate, cellLabel);
                });

                // Bind the label text to the StringProperty
                cellLabel.textProperty().bind(cellProperty);
                cellLabel.setUserData(cell.getCoordinate().toString());
                cell2Label.put(coordinate.toString(), cellLabel);

                // Add the label to the grid at the appropriate position
                gridPane.add(cellLabel, col, row);
            }
        }
        myGridController.setCell2TextProperty(cellTextProperties);
        myGridController.setCell2Label(cell2Label);

       // gridPane.getStylesheets().add(getClass().getResource(Resources.getCurrentTheme()).toExternalForm());
        return gridPane;
    }

    public GridPane createViewOnlyGrid(SheetDTO sheet, int startRow, int startCol) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setGridLinesVisible(true);
        gridPane.setPrefWidth(600);
        gridPane.setPadding(new Insets(20.0, 20.0, 20.0, 20.0));


        // Adding Column Constraints
        for (int col = 0; col <= sheet.getColsLength(); col++) { // Adjust for headers
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setHalignment(HPos.CENTER);
            colConstraints.setHgrow(Priority.ALWAYS);
            colConstraints.setMinWidth(100);
            colConstraints.setPrefWidth(100);
            if (col == 0) {
                colConstraints.setMaxWidth(120); // Special handling for the first column
            }
            gridPane.getColumnConstraints().add(colConstraints);
        }

        // Adding Row Constraints
        for (int row = 0; row <= sheet.getRowsLength(); row++) { // Adjust for headers
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.ALWAYS);
            rowConstraints.setMinHeight(20);
            rowConstraints.setPrefHeight(50);
            if (row == 0) {
                rowConstraints.setMaxHeight(80); // Special handling for the first row
            }
            gridPane.getRowConstraints().add(rowConstraints);
        }

        // Create headers for the first row and column
        for (int col = 1; col <= sheet.getColsLength(); col++) {
            String columnName = Character.toString((char) ('A' + col + startCol - 1)); // Convert to column names A, B, C...
            Label headerLabel = createHeaderLabel(columnName);
            gridPane.add(headerLabel, col, 0);
        }
        Label rootLabel = createHeaderLabel("");
        gridPane.add(rootLabel, 0, 0);

        for (int row = 1; row <= sheet.getRowsLength(); row++) {
            Label headerLabel = createHeaderLabel(Integer.toString(row + startRow)); // Row numbers
            gridPane.add(headerLabel, 0, row);
        }

        // Populate cells with data from the sheet
        for (int row = 1; row <= sheet.getRowsLength(); row++) {
            for (int col = 1; col <= sheet.getColsLength(); col++) {
                CoordinateDTO coordinate = CoordinateDTOFactory.createCoordinate(row - 1, col - 1);

                CellDTO cell = sheet.getCell(coordinate);
                if (cell == null) {
                    cell = new CellDTO(coordinate.toString(), " ");
                }
                Label cellLabel = createGridLabel(cell);

                cellLabel.setText(cell.getEffectiveValue().getValue());
                gridPane.add(cellLabel, col, row);
            }
        }
        gridPane.getStylesheets().add(getClass().getResource(Resources.getCurrentTheme()).toExternalForm());
        return gridPane;
    }

    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.setAlignment(Pos.CENTER);
        GridPane.setHgrow(label, Priority.ALWAYS);
        GridPane.setVgrow(label, Priority.ALWAYS);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.getStyleClass().add("grid-header");
        return label;
    }

    private Label createGridLabel(CellDTO cell) {
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        GridPane.setHgrow(label, Priority.ALWAYS);
        GridPane.setVgrow(label, Priority.ALWAYS);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.getStyleClass().add("grid-cell");

        if (cell.getTextColor() == null)
            return label;

        return label;
    }

    private Color fromHexString(String hexColor) {
        // Handle both 6-character and 8-character hex codes (with alpha)
        if (hexColor.length() == 7) {
            hexColor += "ff";  // Add full opacity if not specified
        }
        return Color.web(hexColor);
    }
}