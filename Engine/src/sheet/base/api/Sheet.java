package sheet.base.api;

import sheet.cell.Cell;
import sheet.coordinate.Coordinate;
import sheet.ranges.Range;

import java.util.Map;
import java.util.Set;

public interface Sheet {
    Sheet createSheetCopy();
    int getVersion();
    String getName();
    void setVersion(int version);
    int getRowHeight();
    int getColWidth();
    int getRowsLength();
    int getColsLength();
    Cell getCell(int col, int row);
    Cell getCell(String cellID);
    void setCell(String cellID, String value);
    Map<Coordinate,Cell> getActiveCells();
    Map<Coordinate,Cell> copyActiveCells();
    int getCellsChanged();
    void setCellsChanged(int cellsChanged);
    void updateAllValues();

    void addRange(Range range);
    Range getRange(String name);
    void removeRange(String name);
    Range createRange(String name, Coordinate topLeft, Coordinate bottomRight);
    boolean isValidRange(Coordinate topLeft, Coordinate bottomRight, Set<Cell> cells);
    Map<String, Range> getRanges();
}
