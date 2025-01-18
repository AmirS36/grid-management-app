package coordinate;

import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateImpl;

public class CoordinateConverter {

    public static  CoordinateDTO toDTO(Coordinate coordinate) {
        return new CoordinateDTO(coordinate.getRow(), coordinate.getCol());
    }

    public static Coordinate fromDTO(CoordinateDTO dto) {
        return new CoordinateImpl(dto.getRow(), dto.getCol());
    }
}
