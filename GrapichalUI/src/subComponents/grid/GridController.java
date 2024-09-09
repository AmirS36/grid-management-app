package subComponents.grid;

import logic.grid.GridService;
import sheet.base.api.Sheet;
import subComponents.app.MainController;

public class GridController {

    //Controllers
    private MainController myMainController;

    //Utilities
    private GridService gridService = new GridService();
    private Sheet currentSheet;


    public void setMainController(MainController myMainController) {
        this.myMainController = myMainController;
    }

    public Sheet getCurrentSheet() {
        return currentSheet;
    }

    public void setCurrentSheet(Sheet sheet) {
        sheet.updateAllValues();
        this.currentSheet = sheet;
        myMainController.setGridComponent(gridService.createGrid(sheet));
        // Additional code to update the grid based on the new sheet
    }
}
