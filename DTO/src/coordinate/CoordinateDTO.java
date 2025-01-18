package coordinate;

import java.io.Serializable;

public class CoordinateDTO implements Serializable {
    private final int row;
    private final int col;

    // Constructor to initialize the immutable fields
    public CoordinateDTO(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // Getter for the row
    public int getRow() {
        return row;
    }

    // Getter for the column
    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        if (row == -1 && col == -1) {
            return "  ";
        }

        String columnLetter = convertToColumnLetter(col);  // Convert the column index to a letter
        int rowNumber = row + 1;  // Convert zero-based index to a 1-based index
        return columnLetter + rowNumber;
    }

    private String convertToColumnLetter(int col) {
        StringBuilder columnName = new StringBuilder();
        while (col >= 0) {
            columnName.insert(0, (char) ('A' + (col % 26)));
            col = (col / 26) - 1;
        }
        return columnName.toString();
    }
}