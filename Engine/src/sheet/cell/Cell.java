package sheet.cell;

import sheet.base.api.Sheet;
import sheet.coordinate.Coordinate;
import sheet.effectiveValue.EffectiveValue;

import java.util.List;

public interface Cell {
    Cell copyCell();
    Coordinate getCoordinate();
    String getOriginalValue();
    void setCellOriginalValue(String value);
    EffectiveValue getEffectiveValue();
    void updateEffectiveValue(String value, Sheet sheet);
    int getVersion();
    void setVersion(int version);
    void setDependsOn(List<Cell> dependsOn);
    void setInfluencingOn(List<Cell> influencingOn);
    List<Cell> getDependsOn();
    List<Cell> getInfluencingOn();
    void addInfluence(Cell cell);
    void removeInfluence(Cell cell);
    void addDependency(Cell cell);
    void removeDependency(Cell cell);
    void clearDependsOn();
    void clearInfluencingOn();
    void clearDependenciesAndInfluences();
}
