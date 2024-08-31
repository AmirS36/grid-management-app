package sheet.cell;

import sheet.base.api.Sheet;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateImpl;
import sheet.effectiveValue.EffectiveValue;
import sheet.effectiveValue.EffectiveValueImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CellImpl implements Cell, Serializable {

    private final Coordinate coordinate;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private int version;
    private List<Cell> dependsOn;
    private List<Cell> influencingOn;

    public CellImpl(int row, int col, String originalValue, EffectiveValue effectiveValue, int version) {
        this.coordinate = new CoordinateImpl(row,col);
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.version = version;
        this.dependsOn = new ArrayList<>();
        this.influencingOn = new ArrayList<>();
    }

    @Override
    public Cell copyCell(){
        return new CellImpl(coordinate.getRow(), coordinate.getCol(), originalValue, effectiveValue, version);
    }

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public String getOriginalValue() {
        return originalValue;
    }

    @Override
    public void setCellOriginalValue(String value) {this.originalValue = value;}

    @Override
    public EffectiveValue getEffectiveValue() {return effectiveValue;}

    @Override
    public void updateEffectiveValue(String value, Sheet sheet) {
        this.effectiveValue = new EffectiveValueImpl(value, sheet);
        sheet.setCellsChanged(sheet.getCellsChanged() + 1);
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setVersion(int version) {this.version = version;}

    @Override
    public List<Cell> getDependsOn() {return dependsOn;}

    @Override
    public List<Cell> getInfluencingOn() { return influencingOn; }

    @Override
    public void setDependsOn(List<Cell> dependsOn) {this.dependsOn = dependsOn;}

    @Override
    public void setInfluencingOn(List<Cell> influencingOn) {this.influencingOn = influencingOn;}

    @Override
    public void addInfluence(Cell cell) {influencingOn.add(cell);}

    @Override
    public void removeInfluence(Cell cell) {influencingOn.remove(cell);}

    @Override
    public void addDependency(Cell cell) {dependsOn.add(cell);}

    @Override
    public void removeDependency(Cell cell) {dependsOn.remove(cell);}

    @Override
    public void clearDependsOn() {dependsOn.clear();}

    @Override
    public void clearInfluencingOn() {influencingOn.clear();}

    @Override
    public void clearDependenciesAndInfluences() {
        clearDependsOn();
        clearInfluencingOn();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellImpl cell = (CellImpl) o;
        return Objects.equals(effectiveValue, cell.effectiveValue);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(originalValue);
    }
}
