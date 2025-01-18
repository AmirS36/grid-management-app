import sheet.cell.Cell;
import sheet.coordinate.Coordinate;
import sheet.base.api.Sheet;
import sheet.coordinate.CoordinateFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SheetLayout {

    public static void displaySheet(Sheet sheet) {
        int cellWidth = sheet.getColWidth();
        int cellHeight = sheet.getRowHeight();
        int rows = sheet.getRowsLength();
        int cols = sheet.getColsLength();

        Map<Coordinate, Cell> activeCells = sheet.getActiveCells();

        // Print sheet name and version
        System.out.println(" ");
        System.out.println("Sheet Name: " + sheet.getName());
        System.out.println("Version: " + sheet.getVersion());

        // Print the first row (column letters)
        System.out.print("    |"); // Initial space before column letters
        for (int col = 0; col < cols; col++) {
            String columnLetter = convertToColumnLetter(col);
            String centeredColumn = centerString(columnLetter, cellWidth);
            System.out.print(centeredColumn + "|");
        }
        System.out.println();

        // Print the rows with centered row numbers and content
        for (int row = 0; row < rows; row++) {
            // Collect all cell contents for this row
            List<String[]> rowContent = new ArrayList<>();
            for (int col = 0; col < cols; col++) {
                Coordinate coordinate = CoordinateFactory.createCoordinate(row, col);
                String cellContent = " ";

                // Check if the cell exists in the activeCells map and print its effective value
                if (activeCells.containsKey(coordinate)) {
                    Cell cell = activeCells.get(coordinate);
                    cellContent = cell.getEffectiveValue().getValue().toString(); // Display effective value

                    // Truncate the content if it exceeds the column width
                    if (cellContent.length() > cellWidth) {
                        cellContent = cellContent.substring(0, cellWidth);
                    }
                }

                // Center the content for height and width
                String[] centeredCell = centerStringForHeightAndWidth(cellContent, cellWidth, cellHeight);
                rowContent.add(centeredCell);
            }

            // Print each row of the cell's height, one line at a time
            for (int i = 0; i < cellHeight; i++) {
                if (i == cellHeight / 2) {
                    // Print the row number in the center
                    String rowNumber = String.format("%3d", row + 1); // Center-aligned row number with padding
                    System.out.print(rowNumber + " |");
                } else {
                    System.out.print("    |"); // Empty space where row number would be
                }

                for (String[] cellRows : rowContent) {
                    System.out.print(cellRows[i] + "|");
                }
                System.out.println();
            }
        }
    }

    // Helper method to center a string for both height and width
    private static String[] centerStringForHeightAndWidth(String text, int width, int height) {
        // Create a String[] to hold each row of text for the cell height
        String[] result = new String[height];
        Arrays.fill(result, " ".repeat(width)); // Fill with empty lines first

        // Center the text horizontally
        String centeredText = centerString(text, width);

        // Determine the vertical position to place the centered text
        int middleRow = height / 2;
        result[middleRow] = centeredText;

        return result;
    }

    private static String centerString(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int padding = (width - text.length()) / 2;
        StringBuilder centered = new StringBuilder();
        centered.append(" ".repeat(padding));
        centered.append(text);
        while (centered.length() < width) {
            centered.append(" ");
        }
        return centered.toString();
    }

    public static String convertToColumnLetter(int columnIndex) {
        StringBuilder columnLetter = new StringBuilder();
        while (columnIndex >= 0) {
            columnLetter.insert(0, (char) ('A' + (columnIndex % 26)));
            columnIndex = columnIndex / 26 - 1;
        }
        return columnLetter.toString();
    }
}

