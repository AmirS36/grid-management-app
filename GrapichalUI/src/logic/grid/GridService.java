package logic.grid;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import sheet.base.api.Sheet;
import sheet.cell.Cell;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;

import java.util.Map;

public class GridService {

    public GridPane createGrid(Sheet sheet) {
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
            colConstraints.setMinWidth(15);
            colConstraints.setPrefWidth(50);
            if (col == 0) {
                colConstraints.setMaxWidth(80); // Special handling for the first column
            }
            gridPane.getColumnConstraints().add(colConstraints);
        }

        // Adding Row Constraints
        for (int row = 0; row <= sheet.getRowsLength(); row++) { // Adjust for headers
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.ALWAYS);
            rowConstraints.setMinHeight(25);
            rowConstraints.setPrefHeight(30);
            if (row == 0) {
                rowConstraints.setMaxHeight(50); // Special handling for the first row
            }
            gridPane.getRowConstraints().add(rowConstraints);
        }

        // Create headers for the first row and column
        for (int col = 1; col <= sheet.getColsLength(); col++) {
            String columnName = Character.toString((char) ('A' + col - 1)); // Convert to column names A, B, C...
            Label headerLabel = createHeaderLabel(columnName);
            gridPane.add(headerLabel, col, 0);
        }

        for (int row = 1; row <= sheet.getRowsLength(); row++) {
            Label headerLabel = createHeaderLabel(Integer.toString(row)); // Row numbers
            gridPane.add(headerLabel, 0, row);
        }

        // Populate cells with data from the sheet
        Map<Coordinate, Cell> activeCells = sheet.getActiveCells();
        for (int row = 1; row <= sheet.getRowsLength(); row++) {
            for (int col = 1; col <= sheet.getColsLength(); col++) {
                Coordinate coordinate = CoordinateFactory.createCoordinate(row, col);

                // Check if the cell exists in the activeCells map and print its effective value
                if (activeCells.containsKey(coordinate)) {
                    Cell cell = activeCells.get(coordinate);
                    Label cellLabel = createCellNode(cell); // Create a label based on the cell's value
                    gridPane.add(cellLabel, col, row);
                }
            }
        }

        return gridPane;
    }

    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.setFont(new Font("System Bold", 16));
        label.setAlignment(Pos.CENTER_RIGHT);
        GridPane.setHgrow(label, Priority.ALWAYS);
        GridPane.setVgrow(label, Priority.ALWAYS);
        return label;
    }

    private Label createCellNode(Cell cell) {
        Label cellLabel = new Label(cell.getEffectiveValue().getValue().toString()); // Convert the value to String
        cellLabel.setFont(new Font(16));
        cellLabel.setAlignment(Pos.CENTER);
        GridPane.setHgrow(cellLabel, Priority.ALWAYS);
        GridPane.setVgrow(cellLabel, Priority.ALWAYS);
        return cellLabel;
    }
}