package sheet.management.api;

import sheet.base.api.Sheet;
import sheet.management.impl.VersionManagerImpl;

public interface SheetManager {
    public Sheet getCurrentSheet();
    public VersionManagerImpl getVersionManager();
    public void updateCell(String cellID, String value, String lastUpdatedBy, String type);
    public int getCurrentVersion();
    public String getSheetOwner();

}
