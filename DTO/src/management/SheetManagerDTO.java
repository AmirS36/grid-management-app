package management;

import sheet.SheetDTO;

public class SheetManagerDTO {

    private String sheetOwner;
    private SheetDTO currentSheet;
    private VersionManagerDTO versionManager;

    public SheetManagerDTO(){
        versionManager = new VersionManagerDTO();
    }

    public SheetManagerDTO(String sheetOwner, SheetDTO currentSheet, VersionManagerDTO versionManager) {
        this.sheetOwner = sheetOwner;
        this.currentSheet = currentSheet;
        this.versionManager = versionManager;
    }

    public String getSheetOwner() {
        return sheetOwner;
    }

    public SheetDTO getCurrentSheet() {
        return currentSheet;
    }

    public VersionManagerDTO getVersionManager() {
        return versionManager;
    }
}
