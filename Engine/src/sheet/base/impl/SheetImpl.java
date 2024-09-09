package sheet.base.impl;

import sheet.effectiveValue.CellType;
import sheet.effectiveValue.EffectiveValue;
import sheet.cell.Cell;
import sheet.cell.CellImpl;
import sheet.coordinate.Coordinate;
import sheet.base.api.Sheet;
import sheet.coordinate.CoordinateFactory;
import sheet.effectiveValue.EffectiveValueImpl;
import sheet.ranges.Range;
import sheet.ranges.RangeImpl;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SheetImpl implements Sheet, Serializable {

    private final String name;
    private final int rowsLength;
    private final int colsLength;
    private final Map<Coordinate, Cell> activeCells;
    private int rowHeight = 3;
    private int colWidth = 15;
    private int version;
    private int cellsChanged;
    private Map<Coordinate, Cell> previousActiveCells;
    private final Map<String, Range> ranges;

    public SheetImpl(String name, int rows, int cols, int rowHeight, int colWidth, Map<Coordinate, Cell> cells, int version, int cellsChanged) {
        this.name = name;
        this.rowsLength = rows;
        this.colsLength = cols;
        this.rowHeight = rowHeight;
        this.colWidth = colWidth;
        this.activeCells = cells; // Ensure deep copy
        this.version = version;
        this.cellsChanged = cellsChanged;
        this.ranges = new HashMap<>();
    }

    @Override
    public Sheet createSheetCopy() {
        return new SheetImpl(this.name, this.rowsLength, this.colsLength, this.rowHeight, this.colWidth, this.copyActiveCells(), this.version, this.cellsChanged);
    }

    @Override
    public String getName() {return name;}

    @Override
    public int getVersion() {return version;}

    @Override
    public void setVersion(int version) {this.version = version;}

    @Override
    public int getRowHeight() {return rowHeight;}

    @Override
    public int getColWidth() {return colWidth;}

    @Override
    public int getRowsLength() {return rowsLength;}

    @Override
    public int getColsLength() {return colsLength;}

    @Override
    public int getCellsChanged() {return cellsChanged;}

    @Override
    public void setCellsChanged(int cellsChanged) {this.cellsChanged = cellsChanged;}

    public Cell getCell(int col, int row) {
        return getCell(getColumnName(col) + (row + 1));
    }

    private String getColumnName(int col) {
        StringBuilder columnName = new StringBuilder();
        while (col >= 0) {
            columnName.insert(0, (char) ('A' + col % 26));
            col = col / 26 - 1;
        }
        return columnName.toString();
    }

    @Override
    public Cell getCell(String cellID) {

        validateCellReference(cellID);
        // Convert cellPosition (e.g., "B3") to row and column indices
        int col = cellID.charAt(0) - 'A';  // Convert 'B' to 1, 'A' to 0, etc.
        int row = Integer.parseInt(cellID.substring(1)) - 1;  // Convert '3' to 2, etc.

        Coordinate coordinate = CoordinateFactory.createCoordinate(row,col);
        if (coordinate.getRow() + 1 < 1 || coordinate.getRow() + 1 > rowsLength || coordinate.getCol() + 1 < 1 || coordinate.getCol() + 1 > colsLength) {
            throw new IllegalArgumentException("Cell ("+ coordinate + ") is out of sheet boundaries.");
        }
        Optional<Cell> maybeCell = Optional.ofNullable(activeCells.get(coordinate));

        return maybeCell.orElse(null);
    }

    @Override
    public Map<Coordinate,Cell> getActiveCells(){return activeCells;}

    @Override
    public void setCell(String cellID, String value) {
        int currentVersion = getVersion();
        previousActiveCells = copyActiveCells();

        // Convert cellPosition (e.g., "B3") to row and column indices
        int col = cellID.charAt(0) - 'A';  // Convert 'B' to 1, 'A' to 0, etc.
        int row = Integer.parseInt(cellID.substring(1)) - 1;  // Convert '3' to 2, etc.

        Coordinate coordinate = CoordinateFactory.createCoordinate(row, col);

        Optional<Cell> maybeCell = Optional.ofNullable(activeCells.get(coordinate));
        if(maybeCell.isEmpty()) {
            EffectiveValue effectiveValue = new EffectiveValueImpl(CellType.STRING, " ");
            Cell newCell = new CellImpl(coordinate.getRow(), coordinate.getCol(), " ", effectiveValue, 0);
            activeCells.put(coordinate, newCell);
        }

        Cell currentCell = activeCells.get(coordinate);

        // Step 1: Remove current cell from influencing lists of its old dependencies
        for (Cell dependentCell : currentCell.getDependsOn()) {
            dependentCell.removeInfluence(currentCell);
        }

        // Clear the old dependencies
        currentCell.clearDependsOn();

        // Step 2: Update the cell's value and identify new dependencies
        currentCell.updateEffectiveValue(value, this);
        currentCell.setCellOriginalValue(value);
        currentCell.setVersion(currentVersion + 1);

        List<Cell> newDependencies = new ArrayList<>();
        if (isFunction(currentCell.getOriginalValue())) {
            // Parse the function and extract dependencies
            List<Coordinate> referencedCoordinates = parseFunctionForReferences(currentCell.getOriginalValue());
            for (Coordinate refCoord : referencedCoordinates) {
                Cell referencedCell = activeCells.get(refCoord);
                newDependencies.add(referencedCell);
            }
        }

        // Step 3: Update the dependencies of the current cell
        currentCell.setDependsOn(newDependencies);

        // Step 4: Update influencing cells for new dependencies
        for (Cell dependency : newDependencies) {
            dependency.addInfluence(currentCell);
        }

        // Step 5: Perform topological sort and update all affected cells
        List<Cell> sortedCells = topologicalSortFromCell(currentCell);

        // Step 6: Recalculate values of all cells in topologically sorted order
        for (int i = sortedCells.size() - 1; i >= 0; i--) {
            Cell cell = sortedCells.get(i);
            cell.updateEffectiveValue(cell.getOriginalValue(), this); // Update value based on the original formula/value
        }

        // Update the version number and track changes
        setVersion(currentVersion + 1);
        calculateCellsChanged();
    }

    private List<Cell> topologicalSortFromCell(Cell startCell) {
        // Use a set to track visited cells and a set to track the recursion stack
        Set<Cell> visited = new HashSet<>();
        Set<Cell> recStack = new HashSet<>();
        List<Cell> sortedCells = new ArrayList<>();

        // Perform depth-first search to ensure correct topological order and detect cycles
        if (!dfsVisit(startCell, visited, recStack, sortedCells)) {
            throw new IllegalStateException("Cycle detected in cell dependencies! Please choose another value for Cell " + startCell.getCoordinate());
        }

        // Return the cells in topologically sorted order
        return sortedCells;
    }

    private boolean dfsVisit(Cell cell, Set<Cell> visited, Set<Cell> recStack, List<Cell> sortedCells) {
        if (recStack.contains(cell)) {
            // Cycle detected
            return false;
        }
        if (visited.contains(cell)) {
            return true; // Already processed
        }

        // Mark the cell as visited and add to the recursion stack
        visited.add(cell);
        recStack.add(cell);

        // Visit all cells that this cell influences
        for (Cell influencedCell : cell.getInfluencingOn()) {
            if (!dfsVisit(influencedCell, visited, recStack, sortedCells)) {
                return false; // Cycle detected in a deeper recursion
            }
        }

        // After visiting all influenced cells, remove from the recursion stack and add to sorted list
        recStack.remove(cell);
        sortedCells.add(cell);
        return true;
    }

    private boolean isFunction(String str) {
        return str.startsWith("{") && str.endsWith("}");
    }

    private List<Coordinate> parseFunctionForReferences(String inputValue) {
        List<Coordinate> references = new ArrayList<>();

        // Define the pattern to match {REF,<CellID>} with optional spaces around the comma
        Pattern pattern = Pattern.compile("\\{REF\\s*,\\s*([A-Z]+[0-9]+)\\}");
        Matcher matcher = pattern.matcher(inputValue);

        // Find all matches of the {REF,<CellID>} pattern
        while (matcher.find()) {
            String cellId = matcher.group(1);  // Extract the CellID part (e.g., A1, B2)
            Coordinate coordinate = convertCellIdToCoordinate(cellId);  // Convert CellID to Coordinate
            references.add(coordinate);  // Add the Coordinate to the list
        }

        return references;
    }

    private Coordinate convertCellIdToCoordinate(String cellId) {
        // Convert a cell ID like "A1" to a Coordinate object
        int col = convertToColumnIndex(cellId.replaceAll("[0-9]", ""));  // Extract column part and convert
        int row = Integer.parseInt(cellId.replaceAll("[A-Z]", "")) - 1;  // Extract row part and convert (adjusting for 0-based index)
        return CoordinateFactory.createCoordinate(row, col);
    }

    private void calculateCellsChanged() {
        setCellsChanged(0);

        for (Coordinate coord : activeCells.keySet()) {
            if (!previousActiveCells.containsKey(coord) ||
                    !activeCells.get(coord).equals(previousActiveCells.get(coord))) {
                setCellsChanged(getCellsChanged() + 1);
            }
        }
    }

    @Override
    public Map<Coordinate,Cell> copyActiveCells() {

        Map<Coordinate,Cell> copy = new HashMap<>();

        for (Map.Entry<Coordinate, Cell> entry : this.activeCells.entrySet()) {
            Coordinate copiedCoordinate = entry.getKey().copyCoordinate();
            Cell copiedCell = entry.getValue().copyCell();

            copy.put(copiedCoordinate, copiedCell);
        }
        return copy;
    }

    private int convertToColumnIndex(String columnLetter) {
        int columnIndex = 0;
        for (int i = 0; i < columnLetter.length(); i++) {
            columnIndex *= 26;
            columnIndex += columnLetter.charAt(i) - 'A' + 1;
        }
        return columnIndex - 1;  // Convert to 0-based index
    }

    @Override
    public void updateAllValues() {

        //step 1: update all cells dependencies and influences
        updateDependenciesAndInfluences();

        // Step 2: Perform topological sorting based on the dependency graph
        List<Cell> topologicalOrder = topologicalSortCells();

        // Step 3: Update each cell in the correct order
        for (Cell cell : topologicalOrder) {
            cell.updateEffectiveValue(cell.getOriginalValue(),this);
        }

    }

    private void updateDependenciesAndInfluences() {
        // Step 1: Clear current dependencies and influences for all cells
        for (Cell cell : activeCells.values()) {
            cell.clearDependenciesAndInfluences();
        }

        // Step 2: Recalculate dependencies and update influencing lists
        for (Cell cell : activeCells.values()) {
            // Recalculate dependencies
            updateCellDependencies(cell);

            // Step 3: For each dependency of the current cell,
            // add the current cell to the influencing list of the dependency

            for (Cell dependency : cell.getDependsOn()) {
                dependency.addInfluence(cell); // Add the current cell to the influencing list of its dependency
            }
        }
    }

    private void updateCellDependencies(Cell currentCell) {
//HELLO
        List<Cell> newDependencies = new ArrayList<>();
        if (isFunction(currentCell.getOriginalValue())) {
            // Parse the function and extract dependencies
            List<Coordinate> referencedCoordinates = parseFunctionForReferences(currentCell.getOriginalValue());
            for (Coordinate refCord : referencedCoordinates) {
                Cell referencedCell = activeCells.get(refCord);
                newDependencies.add(referencedCell);
            }
        }
        currentCell.setDependsOn(newDependencies);
    }

    private List<Cell> topologicalSortCells() {
        // Create a map to track the in-degree of each cell
        Map<Cell, Integer> inDegree = new HashMap<>();
        Queue<Cell> queue = new LinkedList<>();
        List<Cell> topologicalOrder = new ArrayList<>();

        // Initialize in-degree map
        for (Cell cell : activeCells.values()) {
            inDegree.put(cell, cell.getDependsOn().size());
            if (cell.getDependsOn().isEmpty()) {
                queue.add(cell); // Cells with no dependencies can be processed first
            }
        }

        // Perform Kahn's algorithm for topological sorting
        while (!queue.isEmpty()) {
            Cell cell = queue.poll();
            topologicalOrder.add(cell);

            for (Cell influencedCell : cell.getInfluencingOn()) {
                int currentInDegree = inDegree.get(influencedCell) - 1;
                inDegree.put(influencedCell, currentInDegree);

                if (currentInDegree == 0) {
                    queue.add(influencedCell); // Once no dependencies remain, add to queue
                }
            }
        }

        if (topologicalOrder.size() != activeCells.size()) {
            throw new IllegalStateException("Cycle detected in cell dependencies");
        }
        return topologicalOrder;
    }

    public  void validateCellReference(String input) {
        // Regular expression for valid cell reference (e.g., A5, B12, Z99)
        String cellPattern = "^[A-Z]+[1-9][0-9]*$";

        // Check if the input matches the cell reference pattern
        if (input == null || !input.matches(cellPattern)) {
            throw new IllegalArgumentException("Invalid cell reference format: " + input);
        }

        // Check for Hebrew characters (range from Unicode \u0590 to \u05FF)
        for (char c : input.toCharArray()) {
            if (c >= '\u0590' && c <= '\u05FF') {
                throw new IllegalArgumentException("Cell reference contains Hebrew characters: " + input);
            }
        }
    }

    //Ranges

    @Override
    public void addRange(Range range) {
        ranges.put(range.getName(),range);
    }

    @Override
    public Range getRange(String name) {
        return ranges.get(name);
    }

    @Override
    public void removeRange(String name) {
        ranges.remove(name);
    }

    @Override
    public Range createRange(String name, Coordinate topLeft, Coordinate bottomRight) {
        Set<Cell> cells = new HashSet<>();

        // Iterate through the specified range and add cells to the set
        for (int row = topLeft.getRow(); row <= bottomRight.getRow(); row++) {
            for (int col = topLeft.getCol(); col <= bottomRight.getCol(); col++) {
                Coordinate coord = CoordinateFactory.createCoordinate(row, col);
                Cell cell = getCell(coord.toString()); // Assuming there's a method to get a cell by its coordinate
                if (cell != null) {
                    cells.add(cell);
                }
            }
        }
        // Validate the range
        if (isValidRange(topLeft, bottomRight, cells)) {
            return new RangeImpl(name,cells);
        } else {
            throw new IllegalArgumentException("The range is invalid.");
        }
    }

    @Override
    public boolean isValidRange(Coordinate topLeft, Coordinate bottomRight, Set<Cell> cells) {
        // Check if the cells form a valid rectangular block
        // Optionally, you can add more specific validation based on your requirements
        for (int row = topLeft.getRow(); row <= bottomRight.getRow(); row++) {
            for (int col = topLeft.getCol(); col <= bottomRight.getCol(); col++) {
                Coordinate coord = CoordinateFactory.createCoordinate(row, col);
                if (!cells.contains(getCell(coord.toString()))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Map<String, Range> getRanges() {
        return ranges;
    }
}
