package sheet.ranges;

import sheet.cell.Cell;
import java.util.Set;

public interface Range {
    void addCell(Cell cell);
    void removeCell(Cell cell);
    Set<Cell> getCells();
    boolean contains(Cell cell);
    String getName();
    void setName(String name);

}
