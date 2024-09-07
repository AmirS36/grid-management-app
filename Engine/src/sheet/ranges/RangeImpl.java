package sheet.ranges;

import sheet.cell.Cell;

import java.util.HashSet;
import java.util.Set;

public class RangeImpl implements Range {
    private Set<Cell> cells = new HashSet<>();
    private String name;

    public RangeImpl(String name, Set<Cell> cells) {
        this.name = name;
        this.cells = cells;
    }

    @Override
    public void addCell(Cell cell) {
        cells.add(cell);
    }

    @Override
    public void removeCell(Cell cell) {
        cells.remove(cell);
    }

    @Override
    public Set<Cell> getCells() {
        return new HashSet<>(cells);
    }

    @Override
    public boolean contains(Cell cell) {
        return cells.contains(cell);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }


}
