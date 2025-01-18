package sheet;

import cell.CellConverter;
import cell.CellDTO;
import coordinate.CoordinateDTO;
import coordinate.CoordinateDTOFactory;
import range.RangeDTO;
import sheet.cell.Cell;
import sheet.cell.CellImpl;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.effectiveValue.EffectiveValueImpl;

import java.io.Serializable;
import java.util.Map;

public class SheetDTO implements Serializable {
    private final String name;
    private final int rowsLength;
    private final int colsLength;
    private final int rowHeight;
    private final int colWidth;
    private final int version;
    private final int cellsChanged;
    private final Map<String, CellDTO> activeCells;
    private final Map<String, CellDTO> previousActiveCells;
    private final Map<String, RangeDTO> ranges;

    // Constructor
    public SheetDTO(String name, int rowsLength, int colsLength, int rowHeight, int colWidth,
                    int version, int cellsChanged,Map<String, CellDTO> activeCells,
                    Map<String, CellDTO> previousActiveCells, Map<String, RangeDTO> ranges) {

        this.name = name;
        this.rowsLength = rowsLength;
        this.colsLength = colsLength;
        this.rowHeight = rowHeight;
        this.colWidth = colWidth;
        this.version = version;
        this.cellsChanged = cellsChanged;
        this.activeCells = activeCells;
        this.previousActiveCells = previousActiveCells;
        this.ranges = ranges;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getRowsLength() {
        return rowsLength;
    }

    public int getColsLength() {
        return colsLength;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public int getColWidth() {
        return colWidth;
    }

    public int getVersion() {
        return version;
    }

    public int getCellsChanged() {
        return cellsChanged;
    }

    public Map<String, CellDTO> getActiveCells() {
        return activeCells;
    }

    public Map<String, CellDTO> getPreviousActiveCells() {
        return previousActiveCells;
    }

    public Map<String, RangeDTO> getRanges() {
        return ranges;
    }

    public CellDTO getCell(CoordinateDTO coordinate) {
        return getCell(getColumnName(coordinate.getCol()) + (coordinate.getRow() + 1));
    }

    private String getColumnName(int col) {
        StringBuilder columnName = new StringBuilder();
        while (col >= 0) {
            columnName.insert(0, (char) ('A' + col % 26));
            col = col / 26 - 1;
        }
        return columnName.toString();
    }

    public CellDTO getCell(String cellID) {

        cellID = cellID.toUpperCase();
        validateCellReference(cellID);
        // Convert cellPosition (e.g., "B3") to row and column indices
        int col = cellID.charAt(0) - 'A';  // Convert 'B' to 1, 'A' to 0, etc.
        int row = Integer.parseInt(cellID.substring(1)) - 1;  // Convert '3' to 2, etc.

        CoordinateDTO coordinate = CoordinateDTOFactory.createCoordinate(row,col);
        if (coordinate.getRow() + 1 < 1 || coordinate.getRow() + 1 > rowsLength || coordinate.getCol() + 1 < 1 || coordinate.getCol() + 1 > colsLength) {
            throw new IllegalArgumentException("Cell ("+ coordinate + ") is out of sheet boundaries.");
        }
        return activeCells.get(cellID);
    }


    public void validateCellReference(String input) {
        // Regular expression for valid cell reference (e.g., A5, B12, Z99)
        String cellPattern = "^[A-Z]+[1-9][0-9]*$";

        // Check if the input matches the cell reference pattern
        if (input == null || !input.matches(cellPattern)) {
            throw new IllegalArgumentException("Invalid cell reference format: " + input);
        }

        // Check for Hebrew characters (range from Unicode \u0590 to \u05FF)
        for (char c : input.toCharArray()) {
            if (c >= '\u0590' && c <= '\u05FF') {
                throw new IllegalArgumentException("Cell reference contains Hebrew characters: " + input);
            }
        }
    }


    public void isWithinBounds(CoordinateDTO coord) {
        if (coord.getRow() + 1 < 1 || coord.getRow() + 1 > rowsLength || coord.getCol() + 1 < 1 || coord.getCol() + 1 > colsLength) {
            throw new IllegalArgumentException("Cell (" + coord + ") is out of sheet boundaries.");
        }
    }
}