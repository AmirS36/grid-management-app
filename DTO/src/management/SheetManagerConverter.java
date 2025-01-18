package management;

import sheet.SheetConverter;
import sheet.management.api.SheetManager;
import sheet.SheetDTO;

public class SheetManagerConverter {

    public static SheetManagerDTO toDTO(SheetManager sheetManager) {
        VersionManagerDTO vmDTO = VersionManagerConverter.toDTO(sheetManager.getVersionManager());
        SheetDTO currentSheet = SheetConverter.toDTO(sheetManager.getCurrentSheet());
        String Owner = sheetManager.getSheetOwner();

        return new SheetManagerDTO(Owner, currentSheet, vmDTO);

    }
}
