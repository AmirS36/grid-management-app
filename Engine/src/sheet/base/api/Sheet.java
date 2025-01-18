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
    Cell getCell(Coordinate coordinate);
    Cell getCell(String cellID);
    void setCell(String cellID, String value, String lastUpdatedBy, String type);
    Map<Coordinate,Cell> getActiveCells();
    Map<Coordinate,Cell> copyActiveCells();
    int getCellsChanged();
    public Map<Coordinate, Cell> getPreviousActiveCells();
    void setCellsChanged(int cellsChanged);
    void updateAllValues();
    void isWithinBounds(Coordinate coord);
    void setOwner(String owner);
    String getOwner();
    String getSize();

    void addRange(Range range);
    Range getRange(String name);
    void removeRange(String name);
    Range createRange(String name, Coordinate topLeft, Coordinate bottomRight);
    boolean isValidRange(Coordinate topLeft, Coordinate bottomRight, Set<Cell> cells, String name);
    Map<String, Range> getRanges();
}
