package cell;

import coordinate.CoordinateDTO;
import effectiveValue.EffectiveValueDTO;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.List;

public class CellDTO implements Serializable {
    private final String coordinate;
    private final String originalValue;
    private final EffectiveValueDTO effectiveValue; // Could be a simple String representation of EffectiveValue
    private final int version;
    private final String lastUpdatedBy;
    private final List<String> dependsOn;  // Simplifying the list to hold CoordinateDTOs
    private final List<String> influencingOn;
    private final String textColor;  // Represented as a hex string
    private final String backgroundColor;

    public CellDTO(String coordinate, String originalValue, EffectiveValueDTO effectiveValue, int version, String lastUpdatedBy,
                   List<String> dependsOn, List<String> influencingOn,
                   String textColor, String backgroundColor) {

        this.coordinate = coordinate;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.version = version;
        this.lastUpdatedBy = lastUpdatedBy;
        this.dependsOn = dependsOn;
        this.influencingOn = influencingOn;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    public CellDTO(String coordinate, String originalValue) {
        this.coordinate = coordinate;
        this.originalValue = originalValue;
        this.effectiveValue = new EffectiveValueDTO("EMPTY", " ");
        this.version = 0;
        this.lastUpdatedBy = "";
        this.dependsOn = null;
        this.influencingOn = null;
        this.textColor = null;
        this.backgroundColor = null;
    }

    // Getters for all fields
    public String getCoordinate() {
        return coordinate;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public EffectiveValueDTO getEffectiveValue() {
        return effectiveValue;
    }

    public int getVersion() {
        return version;
    }

    public List<String> getDependsOn() {
        return dependsOn;
    }

    public List<String> getInfluencingOn() {
        return influencingOn;
    }

    public String getTextColor() {
        return textColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    @Override
    public String toString() {
        return coordinate;
    }
}
