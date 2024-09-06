package logic.grid;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import sheet.base.api.Sheet;
import sheet.cell.Cell;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;

import java.util.Map;

public class GridService {

    public GridPane createGrid(Sheet sheet) {
        GridPane gridPane = new GridPane();

        Map<Coordinate, Cell> cells = sheet.getActiveCells(); // Assuming this method exists
        int numRows = sheet.getRowsLength(); // Method to get the number of rows
        int numCols = sheet.getColsLength(); // Method to get the number of columns

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Coordinate coordinate = CoordinateFactory.createCoordinate(row,col);
                Cell cell = cells.get(coordinate);

                Node cellNode = createCellNode(cell);
                gridPane.add(cellNode, col, row);
            }
        }
        return gridPane;
    }

    private Node createCellNode(Cell cell) {
        Label cellLabel = new Label();

        if (cell != null && cell.getEffectiveValue() != null) {
            cellLabel.setText(cell.getEffectiveValue().getValue().toString());
        } else {
            cellLabel.setText(""); // Empty cell
        }

        // Additional styling or event handling here
        return cellLabel;
    }
}
