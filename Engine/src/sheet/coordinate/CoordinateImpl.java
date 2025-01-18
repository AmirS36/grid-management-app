package sheet.coordinate;

import java.io.Serializable;
import java.util.Objects;

public class CoordinateImpl implements Coordinate, Serializable {
    private final int row;
    private final int col;

    public CoordinateImpl(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public int getRow() { return row;}

    @Override
    public int getCol() { return col;}

    @Override
    public Coordinate copyCoordinate() {
        return new CoordinateImpl(this.row,this.col);
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

    // Helper method to convert column index to column letter (e.g., 0 -> A, 1 -> B, ...)
    private String convertToColumnLetter(int col) {
        StringBuilder columnName = new StringBuilder();
        while (col >= 0) {
            columnName.insert(0, (char) ('A' + (col % 26)));
            col = (col / 26) - 1;
        }
        return columnName.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordinateImpl that = (CoordinateImpl) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
