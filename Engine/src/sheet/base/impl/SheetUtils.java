package sheet.base.impl;

import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;

public class SheetUtils {

    public static Coordinate convertCellIdToCoordinate(String cellId) {
        // Convert a cell ID like "A1" to a Coordinate object
        cellId = cellId.toUpperCase();
        int col = convertToColumnIndex(cellId.replaceAll("[0-9]", ""));  // Extract column part and convert
        int row = Integer.parseInt(cellId.replaceAll("[A-Z]", "")) - 1;  // Extract row part and convert (adjusting for 0-based index)
        return CoordinateFactory.createCoordinate(row, col);
    }

    public static int convertToColumnIndex(String columnLetter) {
        int columnIndex = convertColumnLetterToIndex(columnLetter);
        return columnIndex - 1;  // Convert to 0-based index
    }

    public static int convertColumnLetterToIndex(String column) {
        int result = 0;
        for (int i = 0; i < column.length(); i++) {
            result *= 26;
            result += column.charAt(i) - 'A' + 1;
        }
        return result;
    }


}
