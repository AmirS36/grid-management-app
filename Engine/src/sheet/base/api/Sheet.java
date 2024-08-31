package sheet.base.api;

import sheet.cell.Cell;
import sheet.coordinate.Coordinate;

import java.util.Map;

public interface Sheet {
    Sheet createSheetCopy();
    int getVersion();
    String getName();
    void setVersion(int version);
    int getRowHeight();
    int getColWidth();
    int getRowsLength();
    int getColsLength();
    Cell getCell(String cellID);
    void setCell(String cellID, String value);
    Map<Coordinate,Cell> getActiveCells();
    Map<Coordinate,Cell> copyActiveCells();
    int getCellsChanged();
    void setCellsChanged(int cellsChanged);
    void updateAllValues();
}
