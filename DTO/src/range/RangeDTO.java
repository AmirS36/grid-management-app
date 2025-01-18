package range;

import cell.CellDTO;
import sheet.cell.Cell;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RangeDTO implements Serializable {
    private String name;
    private Set<String> cells;
    private List<String> influencingOn;

    public RangeDTO(String name, Set<String> cells, List<String> influencingOn) {
        this.name = name;
        this.cells = cells;
        this.influencingOn = influencingOn;
    }

    // Getters
    public String getName() {
        return name;
    }

    public Set<String> getCells() {
        return cells;
    }

    public List<String> getInfluencingOn() {
        return influencingOn;
    }

    @Override
    public String toString() {
        // Collect all the cell coordinates in the range
        // Assuming Cell has a proper toString() method for its coordinates
        String cellsString = String.join(", ", cells);

        return name + ": " + cellsString;
    }
}