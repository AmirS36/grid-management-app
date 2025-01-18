package serverWide.sheetsTable;

import serverWide.permissions.PermissionManager;
import sheet.base.api.Sheet;
import sheet.management.api.SheetManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetsTable {

    private final Map<String, SheetManager> sheetsMap;
    private final PermissionManager permissionManager;

    public SheetsTable() {
        sheetsMap = new HashMap<>();
        permissionManager = new PermissionManager();
    }

    public synchronized void addSheet(String sheetName, SheetManager sheetManager) {
        sheetsMap.put(sheetName, sheetManager);
    }

    public SheetManager getSheetManager(String sheetName) {
        return sheetsMap.get(sheetName);
    }

    public synchronized boolean removeSheet(String sheetName) {
        if (sheetsMap.containsKey(sheetName)) {
            sheetsMap.remove(sheetName);
            return true;  // Successfully removed
        }
        return false;  // Sheet not found
    }

    public synchronized List<Sheet> getAllSheets() {
        List<Sheet> allSheets = new ArrayList<>();
        for (SheetManager manager : sheetsMap.values()) {
            allSheets.add(manager.getCurrentSheet());
        }
        return allSheets;
    }

    public boolean sheetExists(String sheetName) {
        return sheetsMap.containsKey(sheetName);
    }

    public int getSheetCount() {
        return sheetsMap.size();
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }
}