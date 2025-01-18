package management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import sheet.base.api.Sheet;
import sheet.SheetDTO;

import java.util.Map;

public class VersionManagerDTO {

    private final Map<Integer, SheetDTO> int2Sheet;

    public VersionManagerDTO() {
        this.int2Sheet = FXCollections.observableHashMap();
    }

    public VersionManagerDTO(Map<Integer, SheetDTO> int2Sheet) {
        this.int2Sheet = int2Sheet;
    }

    public Map<Integer, SheetDTO>  getVersionsMap () {
        return this.int2Sheet;
    }



}
