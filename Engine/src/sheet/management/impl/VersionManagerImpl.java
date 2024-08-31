package sheet.management.impl;

import sheet.base.api.Sheet;
import sheet.management.api.VersionManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class VersionManagerImpl  implements VersionManager, Serializable {

    private final Map<Integer, Sheet> int2Sheet;

    public VersionManagerImpl() {
        this.int2Sheet = new HashMap<>();
    }

    @Override
    public Map<Integer, Sheet>  getVersionsMap () {
        return this.int2Sheet;
    }

    @Override
    public void saveSheetVersion(Sheet oldSheet){
        Sheet newSheet = oldSheet.createSheetCopy();
        int2Sheet.put(newSheet.getVersion(), newSheet);
    }

}
