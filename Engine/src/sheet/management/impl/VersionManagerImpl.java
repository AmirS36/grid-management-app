package sheet.management.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import sheet.base.api.Sheet;
import sheet.management.api.VersionManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class VersionManagerImpl  implements VersionManager, Serializable {

    private final ObservableMap<Integer, Sheet> int2Sheet;

    public VersionManagerImpl() {
        this.int2Sheet = FXCollections.observableHashMap();
    }

    @Override
    public ObservableMap<Integer, Sheet>  getVersionsMap () {
        return this.int2Sheet;
    }

    @Override
    public void saveSheetVersion(Sheet oldSheet){
        Sheet newSheet = oldSheet.createSheetCopy();
        int2Sheet.put(newSheet.getVersion(), newSheet);
    }

}
