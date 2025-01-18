package sheetsTable;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class SheetTableItem {

    private StringProperty uploadedBy;
    private StringProperty sheetName;
    private StringProperty sheetSize;
    private StringProperty permissionLevel;

    public SheetTableItem(String uploadedBy, String sheetName, String sheetSize, String permissionLevel) {
        this.uploadedBy = new SimpleStringProperty(uploadedBy);
        this.sheetName = new SimpleStringProperty(sheetName);
        this.sheetSize = new SimpleStringProperty(sheetSize);
        this.permissionLevel = new SimpleStringProperty(permissionLevel);
    }

    public String getSheetName() {
        return sheetName.get();
    }

    public StringProperty sheetNameProperty() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName.set(sheetName);
    }

    public String getPermissionLevel() {
        return permissionLevel.get();
    }

    public StringProperty permissionLevelProperty() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel.set(permissionLevel);
    }

    public String getUploadedBy() {
        return uploadedBy.get();
    }

    public StringProperty uploadedByProperty() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy.set(uploadedBy);
    }

    public String getSheetSize() {
        return sheetSize.get();
    }

    public StringProperty sheetSizeProperty() {
        return sheetSize;
    }

    public void setSheetSize(String sheetSize) {
        this.sheetSize.set(sheetSize);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SheetTableItem that = (SheetTableItem) o;
        return Objects.equals(sheetName, that.sheetName) && Objects.equals(permissionLevel, that.permissionLevel) && Objects.equals(uploadedBy, that.uploadedBy) && Objects.equals(sheetSize, that.sheetSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sheetName, permissionLevel, uploadedBy, sheetSize);
    }
}