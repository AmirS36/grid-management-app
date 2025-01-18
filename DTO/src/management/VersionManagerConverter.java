package management;

import javafx.collections.ObservableMap;
import sheet.SheetConverter;
import sheet.base.api.Sheet;
import sheet.management.api.VersionManager;
import sheet.SheetDTO;

import java.util.HashMap;
import java.util.Map;

public class VersionManagerConverter {

    public static VersionManagerDTO toDTO(VersionManager versionManager) {
        ObservableMap<Integer, Sheet> versionsMap = versionManager.getVersionsMap();
        Map<Integer,SheetDTO> dtoVersionsMap = new HashMap<>();

        for (Integer key : versionsMap.keySet()) {
            dtoVersionsMap.put(key, SheetConverter.toDTO(versionsMap.get(key)));
        }
        return new VersionManagerDTO(dtoVersionsMap);
    }
}
