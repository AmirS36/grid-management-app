package sheet.management.impl;

import sheet.base.api.Sheet;
import sheet.management.api.SheetManager;

import java.io.Serializable;

public class SheetManagerImpl implements SheetManager, Serializable {

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

    @Override
    public VersionManagerImpl getVersionManager() {
        return versionManager;
    }

    @Override
    public Sheet getCurrentSheet() {
        return currentSheet;
    }

    @Override
    public void updateCell(String cellID, String value){
        if (currentSheet != null) {
            currentSheet.setCell(cellID, value);
            versionManager.saveSheetVersion(currentSheet);
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
}
