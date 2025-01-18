package sheetsTable;

public class SheetTableItemDTO {
    private String uploadedBy;
    private String sheetName;
    private String sheetSize;
    private String permissionLevel;

    public SheetTableItemDTO(String uploadedBy, String sheetName, String sheetSize, String permissionLevel) {
        this.uploadedBy = uploadedBy;
        this.sheetName = sheetName;
        this.sheetSize = sheetSize;
        this.permissionLevel = permissionLevel;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getSheetSize() {
        return sheetSize;
    }

    public void setSheetSize(String sheetSize) {
        this.sheetSize = sheetSize;
    }
}
