package subComponents.main;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import subComponents.actionLine.actionLineController;
import subComponents.loadFile.loadFileController;
import subComponents.visuals.visualSettingsController;

public class mainController {

    @FXML private VBox visualSettingsComponent;
    @FXML private visualSettingsController visualSettingsComponentController;

    @FXML private HBox loadFileComponent;
    @FXML private loadFileController loadFileComponentController;

    @FXML private HBox actionLineComponent;
    @FXML private actionLineController actionLineComponentController;


    //TODO - ADD ON TOP
    @FXML
    public void initialize() {
        if (visualSettingsComponentController != null && loadFileComponentController != null && actionLineComponentController != null) {
            visualSettingsComponentController.setMainController(this);
            loadFileComponentController.setMainController(this);
            actionLineComponentController.setMainController(this);

        }
    }

}
