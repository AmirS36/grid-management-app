package coordinate;

import java.util.HashMap;
import java.util.Map;

public class CoordinateDTOFactory {

    private static final Map<String, CoordinateDTO> cachedCoordinates = new HashMap<>();

    public static CoordinateDTO createCoordinate(int row, int column) {
        String key = row + ":" + column;

        // Reuse cached coordinate if it exists
        if (cachedCoordinates.containsKey(key)) {
            return cachedCoordinates.get(key);
        }

        // Create a new CoordinateDTO if not already cached
        CoordinateDTO coordinate = new CoordinateDTO(row, column);
        cachedCoordinates.put(key, coordinate);

        return coordinate;
    }
}
