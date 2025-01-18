package sheet.management.api;

import javafx.collections.ObservableMap;
import sheet.base.api.Sheet;
import java.util.Map;

public interface VersionManager {
    public ObservableMap<Integer, Sheet> getVersionsMap ();
    public void saveSheetVersion(Sheet oldSheet);

}
