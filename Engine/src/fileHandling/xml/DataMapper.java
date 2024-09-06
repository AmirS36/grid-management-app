package fileHandling.xml;

import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.base.api.Sheet;
import sheet.cell.Cell;
import sheet.cell.CellImpl;
import sheet.coordinate.Coordinate;
import sheet.effectiveValue.EffectiveValueImpl;
import sheet.base.impl.SheetImpl;
import fileHandling.xml.jaxb.generated.ex1.STLCell;
import fileHandling.xml.jaxb.generated.ex1.STLSheet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataMapper {

    public Sheet mapSTLSheetToSheet (STLSheet stlSheet){
        String name = stlSheet.getName();
        int version = 1;
        int rowsLength = stlSheet.getSTLLayout().getRows();
        int columnsLength = stlSheet.getSTLLayout().getColumns();

        if (rowsLength < 1 || rowsLength > 50 || columnsLength < 1 || columnsLength > 20) {
            throw new IllegalArgumentException("Sheet size in the XML file is invalid. Rows must be between 1 and 50, and columns between 1 and 20. \n" +
                    "But is " + rowsLength + " and " + columnsLength + " instead");
        }

        int rowsHeight = stlSheet.getSTLLayout().getSTLSize().getRowsHeightUnits();
        int colsWidth = stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits();

        Map<Coordinate, Cell> cells = mapSTLCellsToCells(stlSheet.getSTLCells().getSTLCell(), rowsLength, columnsLength);
        return new SheetImpl(name,rowsLength,columnsLength,rowsHeight,colsWidth,cells,version,0);
    }


    private Map<Coordinate,Cell> mapSTLCellsToCells(List<STLCell> stlCells, int rowsLength, int columnsLength) {
        Map<Coordinate,Cell> cells = new HashMap<Coordinate,Cell>();
        for (STLCell stlCell : stlCells) {
            Cell newCell = mapSTLCellToCell(stlCell, rowsLength, columnsLength);
            cells.put(newCell.getCoordinate(), newCell);
        }
        return cells;
    }

    private Cell mapSTLCellToCell(STLCell stlCell, int rowsLength, int columnsLength) {
        int col = stlCell.getColumn().charAt(0) - 'A' + 1;
        int row = stlCell.getRow();

        if (row < 1 || row > rowsLength || col < 1 || col > columnsLength) {
            throw new IllegalArgumentException("Cell at (" + stlCell.getColumn() + ", " + row + ") is out of sheet boundaries.");
        }

        String originalValue = stlCell.getSTLOriginalValue();
        EffectiveValue toBeUpdated = new EffectiveValueImpl(CellType.UNKNOWN, originalValue);
        return new CellImpl(row - 1,col - 1,originalValue,toBeUpdated,1);
    }

}


