package sheet.management.impl;

import sheet.base.api.Sheet;
import sheet.management.api.SheetManager;

import java.io.Serializable;

public class SheetManagerImpl implements SheetManager, Serializable {

    private String sheetOwner;
    private Sheet currentSheet;
    private VersionManagerImpl versionManager;

    public SheetManagerImpl(){
        versionManager = new VersionManagerImpl();
    }

    public SheetManagerImpl(Sheet sheet){
        versionManager = new VersionManagerImpl();
        currentSheet = sheet;
    }

    public SheetManagerImpl(SheetManager sheetManager){
        versionManager = sheetManager.getVersionManager();
        currentSheet = sheetManager.getCurrentSheet();
    }

    public SheetManagerImpl(String sheetOwner, Sheet sheet){
        this.sheetOwner = sheetOwner;
        currentSheet = sheet;
        versionManager = new VersionManagerImpl();
    }

    @Override
    public VersionManagerImpl getVersionManager() {
        return versionManager;
    }

    @Override
    public Sheet getCurrentSheet() {
        return currentSheet;
    }

    @Override
    public void updateCell(String cellID, String value, String lastUpdatedBy, String type){
        if (currentSheet != null) {
            currentSheet.setCell(cellID, value, lastUpdatedBy,type);
            if (type.equals("Final")) {
                versionManager.saveSheetVersion(currentSheet);
            }
        } else {
            throw new IllegalStateException("No sheet found");
        }
    }


    @Override
    public int getCurrentVersion(){
        if (currentSheet != null) {
            return currentSheet.getVersion();
        } else {
            throw new IllegalStateException("No sheet found");
        }
    }

    @Override
    public String getSheetOwner() {
        return sheetOwner;
    }
}
