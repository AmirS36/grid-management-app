package subComponents.app;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sheet.base.api.Sheet;
import subComponents.actionLine.ActionLineController;
import subComponents.grid.GridController;
import subComponents.loadFile.LoadFileController;
import subComponents.ranges.RangesController;
import subComponents.visuals.VisualSettingsController;

public class MainController {

    @FXML private BorderPane mainBorderPane;
    private Stage primaryStage;

    @FXML private VBox visualSettingsComponent;
    @FXML private VisualSettingsController visualSettingsComponentController;

    @FXML private HBox loadFileComponent;
    @FXML private LoadFileController loadFileComponentController;

    @FXML private HBox actionLineComponent;
    @FXML private ActionLineController actionLineComponentController;

    @FXML private VBox rangesComponent;
    @FXML private RangesController rangesComponentController;

    private GridPane gridComponent;
    private GridController gridComponentController;



    //TODO - ADD ON TOP
    @FXML
    public void initialize() {
        if (visualSettingsComponentController != null && loadFileComponentController != null && actionLineComponentController != null && rangesComponentController != null) {
            visualSettingsComponentController.setMainController(this);
            loadFileComponentController.setMainController(this);
            actionLineComponentController.setMainController(this);
            rangesComponentController.setMyMainController(this);

        }
    }


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setGridComponentController(GridController gridComponentController) {
        this.gridComponentController = gridComponentController;
    }

    public void setCurrentSheet (Sheet sheet) {
        gridComponentController.setCurrentSheet(sheet);
    }

    public Sheet getCurrentSheet() {
        return gridComponentController.getCurrentSheet();
    }

    public void setGridComponent (GridPane gridComponent) {
        mainBorderPane.setCenter(gridComponent);
    }

}
