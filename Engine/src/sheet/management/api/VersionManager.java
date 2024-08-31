package sheet.management.api;

import sheet.base.api.Sheet;
import java.util.Map;

public interface VersionManager {
    public Map<Integer, Sheet> getVersionsMap ();
    public void saveSheetVersion(Sheet oldSheet);

}
