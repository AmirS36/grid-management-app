package sheet.coordinate;

public interface Coordinate {
    int getRow();
    int getCol();
    Coordinate copyCoordinate();
}
