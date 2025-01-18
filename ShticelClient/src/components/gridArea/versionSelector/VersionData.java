package components.gridArea.versionSelector;

import javafx.beans.property.SimpleIntegerProperty;

public class VersionData {
    private final SimpleIntegerProperty versionNumber;
    private final SimpleIntegerProperty cellChanges;

    public VersionData(int versionNumber, int cellChanges) {
        this.versionNumber = new SimpleIntegerProperty(versionNumber);
        this.cellChanges = new SimpleIntegerProperty(cellChanges);
    }

    public int getVersionNumber() {
        return versionNumber.get();
    }

    public SimpleIntegerProperty versionNumberProperty() {
        return versionNumber;
    }

    public int getCellChanges() {
        return cellChanges.get();
    }

    public SimpleIntegerProperty cellChangesProperty() {
        return cellChanges;
    }
}