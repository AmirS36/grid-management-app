package sheet.ranges;

import sheet.cell.Cell;

import java.util.*;
import java.util.stream.Collectors;

public class RangeImpl implements Range {
    private Set<Cell> cells = new HashSet<>();
    private String name;
    private List<Cell> influencingOn;

    public RangeImpl(String name, Set<Cell> cells) {
        this.name = name;
        this.cells = cells;
        this.influencingOn = new ArrayList<>();
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

    @Override
    public List<List<Cell>> getRows() {
        // Create a list to hold all cells from the set
        List<Cell> allCells = new ArrayList<>(this.getCells());

        // Use a Map to group cells by their row index
        Map<Integer, List<Cell>> rowMap = new HashMap<>();

        // Iterate over each cell and group them into rows
        for (Cell cell : allCells) {
            int rowIndex = cell.getCoordinate().getRow();
            rowMap.computeIfAbsent(rowIndex, k -> new ArrayList<>()).add(cell);
        }

        // Create a list to store the sorted rows
        List<List<Cell>> sortedRows = new ArrayList<>();

        // Sort the keys (row indices) to maintain order
        List<Integer> sortedRowIndices = new ArrayList<>(rowMap.keySet());
        Collections.sort(sortedRowIndices);

        // Populate the sortedRows list with the cells in the correct order
        for (int rowIndex : sortedRowIndices) {
            sortedRows.add(rowMap.get(rowIndex));
        }

        return sortedRows;
    }

    @Override
    public void addInfluence(Cell cell) {
        influencingOn.add(cell);
    }

    @Override
    public void removeInfluence(Cell cell) {
        influencingOn.remove(cell);
    }

    @Override
    public boolean isInfluencing(Cell cell) {
        if (influencingOn.contains(cell)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isFreeToGo() {
        return influencingOn.isEmpty();
    }

    @Override
    public List<Cell> getInfluencedCells() {
        return  influencingOn;
    }


    @Override
    public String toString() {
        // Collect all the cell coordinates in the range
        String cellsString = cells.stream()
                .map(Cell::toString) // Assuming Cell has a proper toString() method for its coordinates
                .collect(Collectors.joining(", "));

        return name + ": " + cellsString;
    }
}
