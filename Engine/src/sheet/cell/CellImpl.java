package sheet.cell;

import javafx.scene.paint.Color;
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
    private String lastUpdatedBy;
    private List<Cell> dependsOn;
    private List<Cell> influencingOn;
    private Color textColor;
    private Color backgroundColor;

    public CellImpl(int row, int col, String originalValue, EffectiveValue effectiveValue, int version) {
        this.coordinate = new CoordinateImpl(row,col);
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.version = version;
        this.lastUpdatedBy = "Owner";
        this.dependsOn = new ArrayList<>();
        this.influencingOn = new ArrayList<>();
        this.textColor = Color.BLACK;
        this.backgroundColor = Color.TRANSPARENT;
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
    public Color getTextColor() {
        return textColor;
    }

    @Override
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    @Override
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void setDefaultColor() {
        this.backgroundColor = Color.TRANSPARENT;
        this.textColor = Color.BLACK;
    }

    @Override
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @Override
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellImpl cell = (CellImpl) o;
        return Objects.equals(coordinate.toString(), cell.coordinate.toString())
        && Objects.equals(effectiveValue, cell.effectiveValue) && Objects.equals(textColor, cell.textColor) && Objects.equals(backgroundColor, cell.backgroundColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(effectiveValue, textColor, backgroundColor);
    }

    @Override
    public String toString() {
        return coordinate.toString();
    }
}
